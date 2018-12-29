package com.mpapps.clientside_dev_mrx.Models;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class RouteStep
{
    private int distanceMeter;
    private int durationSec;
    private LatLng startLocation;
    private LatLng endLocation;
    private String instruction;
    private List<LatLng> polyLinePoints;

    public RouteStep(int distanceMeter, int durationSec, LatLng startLocation, LatLng endLocation, String instruction, List<LatLng> polyLinePoints)
    {
        this.distanceMeter = distanceMeter;
        this.durationSec = durationSec;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.instruction = instruction;
        this.polyLinePoints = polyLinePoints;
    }

    public int getDistanceMeter()
    {
        return distanceMeter;
    }

    public int getDurationSec()
    {
        return durationSec;
    }

    public LatLng getStartLocation()
    {
        return startLocation;
    }

    public LatLng getEndLocation()
    {
        return endLocation;
    }

    public String getInstruction()
    {
        return instruction;
    }

    public List<LatLng> getPolyLinePoints()
    {
        return polyLinePoints;
    }
}
