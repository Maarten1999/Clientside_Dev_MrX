package com.mpapps.clientside_dev_mrx.Services;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GeoCoderService
{
    private static volatile GeoCoderService instance;

    private GeoCoderService() {
    }

    public static GeoCoderService getInstance() {
        if (instance == null) {
            instance = new GeoCoderService();
        }
        return instance;
    }
    /**
     * places a marker in the specified color at the given location. Title and description are not mandatory.
     *
     * @param location
     * @param markerColor
     * @param title
     * @param description
     */
    public Marker placeMarker(@NonNull GoogleMap map, @NonNull LatLng location, @NonNull float markerColor, @Nullable String title, @Nullable String description) {
        Marker marker;
        if (title == null && description != null) {
            marker = map.addMarker(new MarkerOptions().position(location));
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(markerColor));
            try {
                Log.e("MarkerError", "No title so this description won't be displayed!");
                throw new Exception("No title so this description won't be displayed!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (title != null && description == null) {
            marker = map.addMarker(new MarkerOptions().position(location).title(title));
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(markerColor));
        }
        if (title == null && description == null) {
            marker = map.addMarker(new MarkerOptions().position(location));
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(markerColor));
        } else {
            marker = map.addMarker(new MarkerOptions().position(location).title(title).snippet(description));
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(markerColor));
        }
        return marker;
    }
}

