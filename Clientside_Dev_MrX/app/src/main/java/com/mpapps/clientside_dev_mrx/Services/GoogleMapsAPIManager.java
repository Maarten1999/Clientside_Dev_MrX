package com.mpapps.clientside_dev_mrx.Services;

import android.Manifest;
import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.mpapps.clientside_dev_mrx.Models.RouteModel;
import com.mpapps.clientside_dev_mrx.Models.TravelMode;
import com.mpapps.clientside_dev_mrx.Volley.Requests;

public class GoogleMapsAPIManager
{
    private static GoogleMapsAPIManager instance;

    public static GoogleMapsAPIManager getInstance(Application application){
        if(instance == null)
            instance = new GoogleMapsAPIManager(application);
        return instance;

    }

    private Application application;
    private MutableLiveData<Location> userCurrentLocation;
    private MutableLiveData<RouteModel> userCurrentRoute;
    private LocationManager locationManager;
    private LocationListener locationListenerGPS, locationListenerNetwork;

    private GoogleMapsAPIManager(Application application)
    {
        this.application = application;

        userCurrentLocation = new MutableLiveData<>();
        userCurrentRoute = new MutableLiveData<>();
        locationManager = (LocationManager) application.getSystemService(Context.LOCATION_SERVICE);
        locationListenerGPS = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                userCurrentLocation.postValue(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle)
            {
            }

            @Override
            public void onProviderEnabled(String s)
            {

            }

            @Override
            public void onProviderDisabled(String s)
            {

            }
        };

        locationListenerNetwork = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                userCurrentLocation.postValue(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle)
            {

            }

            @Override
            public void onProviderEnabled(String s)
            {

            }

            @Override
            public void onProviderDisabled(String s)
            {

            }
        };
        startLocationChanges();
    }

    public void startLocationChanges()
    {
        if (ActivityCompat.checkSelfPermission(application, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(application, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 20, locationListenerGPS);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 20, locationListenerNetwork);
        }
    }

    public MutableLiveData<Location> getUserCurrentLocation()
    {
        return userCurrentLocation;
    }

    public MutableLiveData<RouteModel> getUserCurrentRoute(){
        return userCurrentRoute;
    }

    public void calculateRoute(LatLng dest, TravelMode travelMode){
        Location currentLoc = null;
        if (ActivityCompat.checkSelfPermission(application, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(application, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            while (currentLoc == null) {
                if(userCurrentLocation.getValue() == null)
                    currentLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                else
                    currentLoc = userCurrentLocation.getValue();
            }
        }

        LatLng currentLocLatLng = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());
        Requests.getRouteRequest(application, currentLocLatLng, dest, travelMode,
                response ->
                {
                    RouteModel routeModel = JsonDecoder.parseRoute(response);
                    if(routeModel != null)
                        userCurrentRoute.postValue(routeModel);
                }, error -> {
                    Log.i("Route", "Failed");
                });
    }
}
