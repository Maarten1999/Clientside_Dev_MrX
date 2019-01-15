package com.mpapps.clientside_dev_mrx.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;

import com.mpapps.clientside_dev_mrx.R;
import com.mpapps.clientside_dev_mrx.View.MapActivity;

public class PushNotification
{
    private static PushNotification instance;
    private NotificationManagerCompat managerCompat;
    public static final String CHANNEL_SIGHT_PASSED_ID = "geofence";

    public static PushNotification getInstance(Context context)
    {
        if (instance == null)
            instance = new PushNotification(context);
        return instance;
    }

    private PushNotification(Context context)
    {

        managerCompat = NotificationManagerCompat.from(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel sightChannel = new NotificationChannel(
                    CHANNEL_SIGHT_PASSED_ID,
                    "Geofence",
                    NotificationManager.IMPORTANCE_HIGH
            );
            sightChannel.setDescription("the notification appears when a user is near a sight with the tour running");

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(sightChannel);

        }
    }

    public void sendNotification(Context context, String gameName, String contentText){
        Intent resultIntent = new Intent(context, MapActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_SIGHT_PASSED_ID)
                .setSmallIcon(R.drawable.mister_x)
                .setContentTitle(gameName)
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(resultPendingIntent)
                .build();

        managerCompat.notify(1, notification);
    }
}
