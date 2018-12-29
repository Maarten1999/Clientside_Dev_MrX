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
    private LocationManager locationManager;
    private LocationListener locationListener;

    private GoogleMapsAPIManager(Application application)
    {
        this.application = application;

        userCurrentLocation = new MutableLiveData<>();
        locationManager = (LocationManager) application.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                userCurrentLocation.setValue(location);
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
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 2000, 20, locationListener);
        }
    }

    public MutableLiveData<Location> getUserCurrentLocation()
    {
        return userCurrentLocation;
    }
}
