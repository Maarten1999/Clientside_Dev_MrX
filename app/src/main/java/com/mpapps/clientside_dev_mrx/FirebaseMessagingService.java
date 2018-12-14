package com.mpapps.clientside_dev_mrx;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        Log.d("MESSAGING_TOKEN", "Refreshed token: " + token);
        createNotificationChannel();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("MESSAGE_RECEIVED", "From: " + remoteMessage.getFrom());
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.channel_id))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Test")
                .setContentText(remoteMessage.getData().toString())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);

        notificationManagerCompat.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_id);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.channel_id), name, importance);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void newRequestQueue() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://fcm.googleapis.com/fcm/notification";

        // Request a string response from the provided URL.
        CustomJsonArrayRequest request = new CustomJsonArrayRequest(
                method, requestUrl, requestBody,
                response -> listener.ChangeRequestReceived(response),
                error ->
                {
                    error.printStackTrace();
                    Log.i("VolleyService", "Volley error");
                }
        );
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}