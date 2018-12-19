package com.mpapps.clientside_dev_mrx.Volley;

import android.content.Context;
import android.util.Log;
import android.view.textclassifier.TextLinks;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Requests {

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
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

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
}
