package com.mpapps.clientside_dev_mrx.View;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.mpapps.clientside_dev_mrx.Models.GameModel;
import com.mpapps.clientside_dev_mrx.Models.RouteModel;
import com.mpapps.clientside_dev_mrx.Models.RouteStep;
import com.mpapps.clientside_dev_mrx.R;
import com.mpapps.clientside_dev_mrx.Services.CurrentGameInstance;
import com.mpapps.clientside_dev_mrx.Services.GeoCoderService;
import com.mpapps.clientside_dev_mrx.View.Adapters.MapNamesAdapter;
import com.mpapps.clientside_dev_mrx.ViewModels.MapActivityVM;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener
{
    private static final int GPS_REQUEST = 50;
    private MapView mMapView;
    private MapActivityVM viewModel;
    private CameraPosition cameraPosition;
    private GoogleMap googleMap;
    private RecyclerView playerList;
    private MapNamesAdapter adapter;
    private Polyline routePolyline;

    private List<Marker> mapMarkers;
    Animation FABOpen, FABClose, FABCW, FABCCW;
    boolean isOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        askPermission();
        viewModel = ViewModelProviders.of(this).get(MapActivityVM.class);
        mapMarkers = new ArrayList<>();

        playerList = findViewById(R.id.map_activity_recyclerview);
        playerList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new MapNamesAdapter(this, CurrentGameInstance.getInstance().getGameModel().getValue());
        playerList.setAdapter(adapter);
        setupGoogleMaps(savedInstanceState);

        FloatingActionButton fab_plus = findViewById(R.id.map_activity_fab_plus);
        FloatingActionButton fab_route = findViewById(R.id.map_activity_fab_route);
        FloatingActionButton fab_misterx = findViewById(R.id.map_activity_fab_misterx);

        FABOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        FABClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        FABCW = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_cw);
        FABCCW = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_ccw);

        fab_plus.setOnClickListener(view -> {
            if(isOpen){
                fab_misterx.startAnimation(FABClose);
                fab_route.startAnimation(FABClose);
                fab_plus.startAnimation(FABCCW);
                fab_route.setClickable(false);
                fab_misterx.setClickable(false);
                isOpen = false;
            }else {
                fab_misterx.startAnimation(FABOpen);
                fab_route.startAnimation(FABOpen);
                fab_plus.startAnimation(FABCW);
                fab_route.setClickable(true);
                fab_misterx.setClickable(true);
                isOpen = true;
            }
        });
    }

    private void setupGoogleMaps(Bundle savedInstanceState) {
        mMapView = findViewById(R.id.map_activity_mapview);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
    }

    @Override
    public void onInfoWindowClick(Marker marker)
    {
        //TODO calculate route fragment
    }

    @Override
    public void onMapReady(GoogleMap mMap)
    {
        this.googleMap = mMap;
        googleMap.setOnInfoWindowClickListener(this);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }else {
            googleMap.setMyLocationEnabled(true);
            if(cameraPosition != null){
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }else{
                googleMap.moveCamera(CameraUpdateFactory.zoomTo(14));

                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        }

        viewModel.getCurrentLocation().observe(this, location -> {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory
                    .newLatLng(latLng));
            if(routePolyline != null){
                RouteModel routeModel = viewModel.getCurrentRoute();
                if(routeModel != null){
                    boolean isOnRoute = false;
                    int closestRouteStep = 0;
                    for (int i = 0; i < routeModel.getRouteSteps().size(); i++) {
                        RouteStep routeStep = routeModel.getRouteSteps().get(i);
                        if(PolyUtil.isLocationOnPath(latLng, routeStep.getPolyLinePoints(), false, 10)){
                            isOnRoute = true;
                            closestRouteStep = i;
                        }
                    }
                    if(closestRouteStep > 0)
                        viewModel.getCurrentRoute().getRouteSteps().subList(0, closestRouteStep).clear();
                    if(!isOnRoute){
                        viewModel.reCalculateRoute();
                    }
                }
            }
        });

        viewModel.getRouteModel().observe(this, routeModel -> {
            if(routePolyline != null)
                routePolyline.remove();

            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.width(10);
            polylineOptions.color(Color.BLUE);
            List<LatLng> points = new ArrayList<>();
            for (RouteStep routeStep : routeModel.getRouteSteps()) {
                points.addAll(routeStep.getPolyLinePoints());
            }
            polylineOptions.addAll(points);
            routePolyline = googleMap.addPolyline(polylineOptions);

            viewModel.setCurrentRoute(routeModel);
        });

        viewModel.getPlayerLocations().observe(this, stringLatLngMap -> {
            Iterator it = mapMarkers.iterator();
            while (it.hasNext()){
                Marker marker = (Marker) it.next();
                marker.remove();
                it.remove();
            }
            drawMarkers(stringLatLngMap);
        });
    }

    private void drawMarkers(Map<String, LatLng> playerLocs){
        if(playerLocs == null)
            return;

        for (int i = 0; i < playerLocs.size(); i++) {
            String name = (new ArrayList<>(playerLocs.keySet())).get(i);
            LatLng latLng = (new ArrayList<>(playerLocs.values())).get(i);
            GameModel model = CurrentGameInstance.getInstance().getGameModel().getValue();
            if(model != null){
                String desciption = "Detective";
                boolean isMisterX = model.getPlayers().get(name);
                if (isMisterX) {
                    desciption = "Mister X";
                }
                mapMarkers.add(GeoCoderService.getInstance()
                        .placeMarker(googleMap, latLng, BitmapDescriptorFactory.HUE_BLUE, name, desciption, isMisterX));
            }
        }
    }

    private void askPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    GPS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case GPS_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(getBaseContext().getPackageName());
                    assert i != null;
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "geen locatie", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(googleMap != null) {
            outState.putParcelable("CamPos", googleMap.getCameraPosition());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        cameraPosition = savedInstanceState.getParcelable("CamPos");
    }
}