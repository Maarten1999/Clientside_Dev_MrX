package com.mpapps.clientside_dev_mrx.Services;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.mpapps.clientside_dev_mrx.R;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GeofenceTransitionsIntentService extends IntentService
{
    public GeofenceTransitionsIntentService(String name)
    {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent)
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
