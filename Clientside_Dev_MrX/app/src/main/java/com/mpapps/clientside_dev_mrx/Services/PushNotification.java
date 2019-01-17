package com.mpapps.clientside_dev_mrx.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
    private static final String CHANNEL_ID = "channel_01";

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

    public void sendNotification(Context context, String notificationDetails) {
        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }

        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(context, MapActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MapActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        // Define the notification settings.
        builder.setSmallIcon(R.mipmap.ic_launcher)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.ic_launcher))
                .setColor(Color.RED)
                .setContentTitle(notificationDetails)
                .setContentText("Click notification to return to app")
                .setContentIntent(notificationPendingIntent);

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }
}
