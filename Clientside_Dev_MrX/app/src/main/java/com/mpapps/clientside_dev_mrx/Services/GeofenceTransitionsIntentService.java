package com.mpapps.clientside_dev_mrx.Services;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.mpapps.clientside_dev_mrx.R;

import java.util.List;

public class GeofenceTransitionsIntentService extends JobIntentService
{
    public static void enqueuWork(Context context, Intent intent){
        enqueueWork(context, GeofenceTransitionsIntentService.class, 573, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent)
    {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        List<Geofence> triggeredGeofences = geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence : triggeredGeofences) {
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                PushNotification.getInstance(getApplicationContext())
                        .sendNotification(getApplicationContext(), geofence.getRequestId(), this.getString(R.string.notification_text_enter_geofence));
            } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                PushNotification.getInstance(getApplicationContext())
                        .sendNotification(getApplicationContext(), geofence.getRequestId(), this.getString(R.string.notification_text_exit_geofence));
            }
        }
    }
}
