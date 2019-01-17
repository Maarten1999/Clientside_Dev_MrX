package com.mpapps.clientside_dev_mrx.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GeofenceBroadcastReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent) {
        // Enqueues a JobIntentService passing the context and intent as parameters
        GeofenceTransitionsIntentService.enqueuWork(context, intent);
        //GeofencingClient geofencingClient = LocationServices.getGeofencingClient(context);
        System.out.println("GeofenceBroadcastReceiver");
    }
}
