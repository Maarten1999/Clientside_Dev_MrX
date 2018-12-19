package com.mpapps.clientside_dev_mrx.View;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.mpapps.clientside_dev_mrx.R;
import com.mpapps.clientside_dev_mrx.ViewModels.MapActivityVM;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener
{

    private MapView mMapView;
    private MapActivityVM viewModel;
    private CameraPosition cameraPosition;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        viewModel = ViewModelProviders.of(this).get(MapActivityVM.class);

        setupGoogleMaps(savedInstanceState);

    }

    private void setupGoogleMaps(Bundle savedInstanceState) {
        mMapView = findViewById(R.id.map_activity_mapview);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
    }

    @Override
    public void onInfoWindowClick(Marker marker)
    {

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

//                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
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