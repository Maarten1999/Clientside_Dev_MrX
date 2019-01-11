package com.mpapps.clientside_dev_mrx.Services;

import android.text.Html;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
import com.mpapps.clientside_dev_mrx.Models.RouteModel;
import com.mpapps.clientside_dev_mrx.Models.RouteStep;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonDecoder
{
    public static RouteModel parseRoute(JSONObject response){
        try {
            JSONObject leg = response.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0);
            int distanceM = leg.getJSONObject("distance").getInt("value");
            int durationSec = leg.getJSONObject("duration").getInt("value");
            String endAddress = leg.getString("end_address");
            String startAddress = leg.getString("start_address");
            LatLng endLocation = new LatLng(leg.getJSONObject("end_location").getDouble("lat"),
                    leg.getJSONObject("end_location").getDouble("lng"));
            LatLng startLocation = new LatLng(leg.getJSONObject("start_location").getDouble("lat"),
                    leg.getJSONObject("start_location").getDouble("lng"));

            RouteModel routeModel = new RouteModel(distanceM, durationSec, endAddress, endLocation, startLocation, startAddress);
            ArrayList<RouteStep> routeSteps = new ArrayList<>();

            JSONArray steps = leg.getJSONArray("steps");
            for (int i = 0; i < steps.length(); i++) {
                JSONObject step = steps.getJSONObject(i);

                int distM = step.getJSONObject("distance").getInt("value");
                int durSec = step.getJSONObject("duration").getInt("value");
                LatLng endLoc = new LatLng(step.getJSONObject("end_location").getDouble("lat"),
                        step.getJSONObject("end_location").getDouble("lng"));
                LatLng startLoc = new LatLng(step.getJSONObject("start_location").getDouble("lat"),
                        step.getJSONObject("start_location").getDouble("lng"));
                String instruction = Html.escapeHtml(step.getString("html_instructions"));
                List<LatLng> points = PolyUtil.decode(step.getJSONObject("polyline").getString("points"));
                RouteStep routeStep = new RouteStep(distM, durSec, startLoc, endLoc, instruction, points);
                routeSteps.add(routeStep);
            }
            routeModel.setRouteSteps(routeSteps);
            return routeModel;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
