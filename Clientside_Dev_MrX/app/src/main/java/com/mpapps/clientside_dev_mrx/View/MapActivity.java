package com.mpapps.clientside_dev_mrx.View;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;
import com.mpapps.clientside_dev_mrx.Models.Constants;
import com.mpapps.clientside_dev_mrx.Models.GameMode;
import com.mpapps.clientside_dev_mrx.Models.GameModel;
import com.mpapps.clientside_dev_mrx.Models.RouteModel;
import com.mpapps.clientside_dev_mrx.Models.RouteStep;
import com.mpapps.clientside_dev_mrx.Models.TravelMode;
import com.mpapps.clientside_dev_mrx.R;
import com.mpapps.clientside_dev_mrx.Services.CurrentGameInstance;
import com.mpapps.clientside_dev_mrx.Services.GeoCoderService;
import com.mpapps.clientside_dev_mrx.Services.GeofenceTransitionsIntentService;
import com.mpapps.clientside_dev_mrx.Services.TimerService;
import com.mpapps.clientside_dev_mrx.View.Adapters.MapNamesAdapter;
import com.mpapps.clientside_dev_mrx.ViewModels.MapActivityVM;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapLongClickListener, MapNamesAdapter.OnParticipantClickListener
{
    private static final int GPS_REQUEST = 50;
    private MapView mMapView;
    private TextView timerTextView;
    private MapActivityVM viewModel;
    private CameraPosition cameraPosition;
    private GoogleMap googleMap;
    private RecyclerView playerList;
    private LinearLayoutManager linearLayoutManager;
    private MapNamesAdapter adapter;
    private Polyline routePolyline;
    private GeofencingClient geofencingClient;
    private PendingIntent geofencePendingIntent;
    private android.support.v7.app.AlertDialog backNavigateDialog;
    private int FinishResultCode;

    SharedPreferences mPref;
    SharedPreferences.Editor mEditor;

    private List<Marker> mapMarkers;
    private Marker tempMarker;
    Animation FABOpen, FABClose, FABCW, FABCCW;
    boolean isOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        askPermission();
        viewModel = ViewModelProviders.of(this).get(MapActivityVM.class);
        geofencingClient = LocationServices.getGeofencingClient(this);
        mapMarkers = new ArrayList<>();

        playerList = findViewById(R.id.map_activity_recyclerview);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        playerList.setLayoutManager(linearLayoutManager);
        adapter = new MapNamesAdapter(this, CurrentGameInstance.getInstance().getGameModel().getValue(), this);
        playerList.setAdapter(adapter);

        timerTextView = findViewById(R.id.map_activity_time_textview);

        setupGoogleMaps(savedInstanceState);
        setupFABBehavior();

        FinishResultCode = Constants.MAPACTIVITY_STOP_GAME_CODE;
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(this, R.style.AlertDialogTheme);
        alertDialogBuilder.setTitle(R.string.stop_the_game)
                .setMessage(R.string.stop_alertdialog_message)
                .setIcon(R.drawable.stop_icon)
                .setPositiveButton(R.string.ok, (dialogInterface, i) ->{
                    //TODO FIREBASE stop games
                    stopService(new Intent(getApplicationContext(), TimerService.class));
                    setResult(FinishResultCode);
                    finish();
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        backNavigateDialog = alertDialogBuilder.create();

        setupCountDownTimer();
    }

    private void setupCountDownTimer(){
        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mEditor = mPref.edit();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        boolean startCountDown = false;
        try {
            String str_value = mPref.getString("data", "");
            if (str_value.matches("")) {
                //todo start countdown
                startCountDown = true;

            } else {

                if (mPref.getBoolean("countdown_timer_finish", false)) {
                    //todo start the countdown
                    startCountDown = true;
                } else {
                    //still counting
                    startCountDown = false;
                }
            }
        } catch (Exception e) { }

        String date_time = simpleDateFormat.format(Calendar.getInstance().getTime());
        GameMode gameMode = CurrentGameInstance.getInstance().getGameModel().getValue().getMode();
        int minutes = 0;
        switch (gameMode) {
            case Easy:
                minutes = 15;
                break;
            case Normal:
                minutes = 60;
                break;
            case Hard:
                minutes = 120;
                break;
            case MisterX:
                minutes = 240;
                break;
        }

        mEditor.putString("data", date_time).apply();
        mEditor.putInt("minutes", minutes).apply();


        Intent intent_service = new Intent(getApplicationContext(), TimerService.class);
        startService(intent_service);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String str_time = intent.getStringExtra("countdown_timer_time");
            Log.i("MapActivityTimer", str_time);
            timerTextView.setText(str_time);
        }
    };
    private void setupGoogleMaps(Bundle savedInstanceState)
    {
        mMapView = findViewById(R.id.map_activity_mapview);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
    }

    private void setupFABBehavior()
    {
        FloatingActionButton fab_plus = findViewById(R.id.map_activity_fab_plus);
        FloatingActionButton fab_route = findViewById(R.id.map_activity_fab_route);
        FloatingActionButton fab_misterx = findViewById(R.id.map_activity_fab_misterx);
        FloatingActionButton fab_stop = findViewById(R.id.map_activity_fab_stop);

        FABOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        FABClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        FABCW = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_cw);
        FABCCW = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_ccw);

        fab_plus.setOnClickListener(view ->
        {
            if (isOpen) {
                fab_misterx.startAnimation(FABClose);
                fab_route.startAnimation(FABClose);
                fab_stop.startAnimation(FABClose);
                fab_plus.startAnimation(FABCCW);
                fab_route.setClickable(false);
                fab_misterx.setClickable(false);
                fab_stop.setClickable(false);
                isOpen = false;
            } else {
                fab_misterx.startAnimation(FABOpen);
                fab_route.startAnimation(FABOpen);
                fab_stop.startAnimation(FABOpen);
                fab_plus.startAnimation(FABCW);
                fab_route.setClickable(true);
                fab_misterx.setClickable(true);
                fab_stop.setClickable(true);
                isOpen = true;
            }
        });

        fab_misterx.setOnClickListener(view -> {
            //TODO use correct name here
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.map_activity_fragment_placeholder);
            if(fragment == null){
                if (CurrentGameInstance.getInstance().getGameModel().getValue().getPlayers()
                        .get(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {
                    MisterXCodeFragment misterXCodeFragment = MisterXCodeFragment
                            .newInstance(CurrentGameInstance.getInstance().getMisterXCode());
                    misterXCodeFragment.show(fragmentManager, "FRAGMENT_MISTERX_CODE");
                }else{
                    MisterXCodeInputFragment inputFragment = new MisterXCodeInputFragment();
                    inputFragment.show(fragmentManager, "FRAGMENT_MISTERX_CODE_INPUT");
                }
            }
        });

        fab_route.setOnClickListener(view -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.map_activity_fragment_placeholder);
            if(fragment == null){
                CreateRouteFragment createRouteFragment = new CreateRouteFragment();
                createRouteFragment.show(fragmentManager, "FRAGMENT_CREATE_ROUTE");
            }
        });
        fab_stop.setImageBitmap(textAsBitmap("STOP", 36, Color.WHITE));
        fab_stop.setOnClickListener(view -> {
            FinishResultCode = Constants.MAPACTIVITY_STOP_GAME_CODE;
            backNavigateDialog.show();
        });
    }

       //method to convert your text to image
    private Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.0f); // round
        int height = (int) (baseline + paint.descent() + 0.0f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    private void setupGeofence(LatLng latLng)
    {
        int radiusKm = 0;
        switch (CurrentGameInstance.getInstance().getGameModel().getValue().getMode()) {
            case Easy:
                radiusKm = 5;
                break;
            case Normal:
                radiusKm = 10;
                break;
            case Hard:
                radiusKm = 50;
                break;
            case MisterX:
                radiusKm = 100;
                break;
        }
        viewModel.getGeofences().add(new Geofence.Builder()
                .setRequestId(CurrentGameInstance.getInstance().getGameModel().getValue().getName())
                .setCircularRegion(latLng.latitude, latLng.longitude, radiusKm * 1000)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT);
        builder.addGeofences(viewModel.getGeofences());
        GeofencingRequest geofencingRequest = builder.build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, getGeofencePendingIntent());
    }

    private PendingIntent getGeofencePendingIntent(){
        if (geofencePendingIntent == null) {
            Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
            geofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return geofencePendingIntent;
    }

    @Override
    public void onInfoWindowClick(Marker marker)
    {
        Location location;
        if((location = viewModel.getCurrentLocation().getValue()) == null)
            return;

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        TravelMode travelMode = TravelMode.driving;
        if(SphericalUtil.computeDistanceBetween(latLng, marker.getPosition()) < 30)
            travelMode = TravelMode.walking;
        final TravelMode tempTravelMode = travelMode;
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        builder.setTitle(R.string.create_route)
                .setMessage(R.string.route_alertdialog_message)
                .setIcon(R.drawable.ic_nav_routes)
                .setPositiveButton(R.string.ok, (dialogInterface, i) ->
                        viewModel.calculateRoute(marker.getPosition(), tempTravelMode))
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onMapReady(GoogleMap mMap)
    {
        this.googleMap = mMap;
        googleMap.setOnInfoWindowClickListener(this);
        googleMap.setOnMapLongClickListener(this);
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
                Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                if(location != null){
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                }
            }
        }
        viewModel.getCurrentLocation().observe(this, location -> {
            if(location == null)
                return;
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            if(geofencePendingIntent == null)
                setupGeofence(latLng);
//            googleMap.animateCamera(CameraUpdateFactory
//                    .newLatLng(latLng));
            if(routePolyline != null){
                RouteModel routeModel = viewModel.getCurrentRoute();
                if(routeModel != null){
                    boolean isOnRoute = false;
                    int closestRouteStep = 0;
                    for (int i = 0; i < routeModel.getRouteSteps().size(); i++) {
                        RouteStep routeStep = routeModel.getRouteSteps().get(i);
                        if(PolyUtil.isLocationOnPath(latLng, routeStep.getPolyLinePoints(), false, 50)){
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
            if(routeModel == null)
                return;

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
                    mapMarkers.add(GeoCoderService.getInstance()
                            .placeMarker(googleMap, latLng, BitmapDescriptorFactory.HUE_YELLOW, name, desciption));
                }else
                    mapMarkers.add(GeoCoderService.getInstance()
                            .placeMarker(googleMap, latLng, BitmapDescriptorFactory.HUE_BLUE, name, desciption));
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
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        else
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        playerList.setLayoutManager(linearLayoutManager);
        registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.str_receiver_countdown_timer));
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        unregisterReceiver(broadcastReceiver);
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

    @Override
    public void onParticipantClick(String name)
    {
        if(viewModel.getPlayerLocations().getValue().get(name) == null){
            Toast.makeText(this, getString(R.string.toast_no_location_available) + name, Toast.LENGTH_SHORT).show();
            return;
        }
        LatLng latLng = viewModel.getPlayerLocations().getValue().get(name);
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void onMapLongClick(LatLng latLng)
    {
        if(tempMarker != null)
            tempMarker.remove();
        tempMarker = GeoCoderService.getInstance()
                .placeMarker(googleMap, latLng, BitmapDescriptorFactory.HUE_RED, "Marker",getString(R.string.temporary_location));
    }

    @Override
    public void onBackPressed()
    {
        FinishResultCode = Constants.MAPACTIVITY_STOP_GAME_CODE;
        backNavigateDialog.show();
    }
}