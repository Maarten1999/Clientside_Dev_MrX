package com.mpapps.clientside_dev_mrx.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.mpapps.clientside_dev_mrx.Models.RouteModel;
import com.mpapps.clientside_dev_mrx.Models.TravelMode;
import com.mpapps.clientside_dev_mrx.Services.CurrentGameInstance;
import com.mpapps.clientside_dev_mrx.Services.GoogleMapsAPIManager;

import java.util.Map;

public class MapActivityVM extends AndroidViewModel
{
    private GoogleMapsAPIManager mapsApiManager;
    private CurrentGameInstance currentGameInstance;

    private LiveData<Location> currentLocation;

    private RouteModel currentRoute;
    private LatLng currentDestination;
    private TravelMode currentTravelMode;

    public MapActivityVM(@NonNull Application application)
    {
        super(application);
        mapsApiManager = GoogleMapsAPIManager.getInstance(application);
        currentGameInstance = CurrentGameInstance.getInstance();
    }

    public LiveData<Location> getCurrentLocation(){
        if(currentLocation == null)
            currentLocation = mapsApiManager.getUserCurrentLocation();
        return currentLocation;
    }

    public LiveData<RouteModel> getRouteModel(){
        return mapsApiManager.getUserCurrentRoute();
    }

    public MutableLiveData<Map<String, LatLng>> getPlayerLocations(){
        return currentGameInstance.getPlayerLocations();
    }

    public void addPlayerLocation(String playerName, LatLng pos){
        currentGameInstance.addPlayerLocation(playerName, pos);
    }

    public void calculateRoute(LatLng dest, TravelMode travelMode){
        mapsApiManager.calculateRoute(dest, travelMode);
    }

    public boolean reCalculateRoute()
    {
        if(currentDestination != null && currentTravelMode != null){
            mapsApiManager.calculateRoute(currentDestination, currentTravelMode);
            return true;
        }
        return false;
    }
    public RouteModel getCurrentRoute()
    {
        return currentRoute;
    }

    public void setCurrentRoute(RouteModel currentRoute)
    {
        this.currentRoute = currentRoute;
    }
}
