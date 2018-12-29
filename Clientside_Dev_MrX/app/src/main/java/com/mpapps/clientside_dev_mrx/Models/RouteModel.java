package com.mpapps.clientside_dev_mrx.Models;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class RouteModel
{
    private int distanceMeter;
    private int durationSec;
    private String endAddress;
    private LatLng endLocation;
    private LatLng startLocation;
    private String startAddress;
    private List<RouteStep> routeSteps;

    public RouteModel(int distanceMeter, int durationSec, String endAddress, LatLng endLocation,
                      LatLng startLocation, String startAddress)
    {
        this.distanceMeter = distanceMeter;
        this.durationSec = durationSec;
        this.endAddress = endAddress;
        this.endLocation = endLocation;
        this.startLocation = startLocation;
        this.startAddress = startAddress;
    }

    public int getDistanceMeter()
    {
        return distanceMeter;
    }

    public int getDurationSec()
    {
        return durationSec;
    }

    public String getEndAddress()
    {
        return endAddress;
    }

    public LatLng getEndLocation()
    {
        return endLocation;
    }

    public LatLng getStartLocation()
    {
        return startLocation;
    }

    public String getStartAddress()
    {
        return startAddress;
    }

    public List<RouteStep> getRouteSteps()
    {
        return routeSteps;
    }

    public void setRouteSteps(List<RouteStep> routeSteps)
    {
        this.routeSteps = routeSteps;
    }
}
