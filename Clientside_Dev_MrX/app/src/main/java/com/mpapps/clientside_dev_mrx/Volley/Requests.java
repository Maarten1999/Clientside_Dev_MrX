package com.mpapps.clientside_dev_mrx.Volley;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mpapps.clientside_dev_mrx.Models.TravelMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Requests {

    static String groupKey;

    public static void createMessagingGroup(String notificationKey, List<String> registrationIds, Context context) {
        JSONObject jsonBody = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        for (String string : registrationIds)
            jsonArray.put(string);

        try {
            jsonBody.put("operation", "create");
            jsonBody.put("notification_key_name", notificationKey);
            jsonBody.put("registration_ids", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, "https://fcm.googleapis.com/fcm/notification", jsonBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("RESPONSE", response.toString());
                        try {
                           groupKey = (String) response.get("notification_key");

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                            SharedPreferences sharedPref = context.getSharedPreferences("com.mpapps.clientside_dev_mrx.sharedPreferences", Context.MODE_PRIVATE);
                            String gamecode = sharedPref.getString("GameCode", "");

                            reference.child("games").child(gamecode).child("notificationKey").setValue(groupKey);

                           Log.d("groupkey", groupKey);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("VOLLEY_FAILURE", error.toString());
                        Log.d("dd", error.networkResponse.statusCode+"");
                        

                    }
                }
                ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("project_id", "95898840156");
                params.put("Authorization", "key=AAAAFlQELFw:APA91bHwtgNBqtImeRWe6CFFu_ynXHaIcJz_VD_jUBVtOF4dvHliDev3-vR-hQ-N9Dt2XhXWyMhUcVKRmuHjp9U9G4Tsr0uN_Bxpj28r7A_NC8S8N_g10LtDU7WvXcfCH_c1nmuAcLSj");

                return params;
            }
        };

        // Access the RequestQueue through your singleton class.
        VolleyRequestQueue.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public static void getRouteRequest(Context context, LatLng start, LatLng dest, TravelMode travelMode, Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError){
        String url = getRouteUrl(start, dest, travelMode, context);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                onSuccess,
                onError
        );
        VolleyRequestQueue.getInstance(context).addToRequestQueue(request);
    }

    private static String getRouteUrl(LatLng startLocation, LatLng endLocation, TravelMode travelMode, Context context)
    {
        String str_origin = "origin=" + startLocation.latitude + "," + startLocation.longitude;
        String str_dest = "destination=" + endLocation.latitude + "," + endLocation.longitude;

        String trafficMode = "mode=" + travelMode.toString();

        Resources res = context.getResources();
        Configuration conf = res.getConfiguration();
        Locale myLocale = conf.locale;
        String language = myLocale.getLanguage();
        if(language == "")
            language = "en";
        language = "language=" + language;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + trafficMode + "&" + language;

        // Building the url to the web service
        String url3 = "https://maps.moviecast.io/directions?" + parameters + "&key=cb0dd035-f9e5-47d6-a4ad-d613bd1c6d1d";
        return url3;
    }
}
