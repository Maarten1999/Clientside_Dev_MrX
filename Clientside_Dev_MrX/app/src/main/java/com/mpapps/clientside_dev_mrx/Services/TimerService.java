package com.mpapps.clientside_dev_mrx.Services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mpapps.clientside_dev_mrx.Models.Constants;
import com.mpapps.clientside_dev_mrx.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class TimerService extends Service {
    private Handler handler = new Handler();
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    String strDate;
    Date date_current, date_diff;
    SharedPreferences mpref;
    SharedPreferences.Editor mEditor;
    int count = 0;
    String misterX;
    String username;
    String gamecode;

    private Timer mTimer = null;
    public static final long NOTIFY_INTERVAL = 1000;
    Intent intent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mpref = getSharedPreferences(getString(R.string.sharedPreferences),Context.MODE_PRIVATE);
        mEditor = mpref.edit();
        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new CountDownTimerTask(), 5, NOTIFY_INTERVAL);
        intent = new Intent(Constants.str_receiver_countdown_timer);

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getApplicationContext().getString(R.string.sharedPreferences), Context.MODE_PRIVATE);
        gamecode = sharedPref.getString("GameCode", "");
        username = sharedPref.getString("displayname", "");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("games").child(gamecode);
        databaseReference.child("misterX").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                misterX = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    class CountDownTimerTask extends TimerTask {

        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    calendar = Calendar.getInstance();
                    simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                    strDate = simpleDateFormat.format(calendar.getTime());
                    Log.e("strDate", strDate);
                    twoDatesBetweenTime();
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    public String twoDatesBetweenTime() {
        try {
            date_current = simpleDateFormat.parse(strDate);
        } catch (ParseException e) {
        }

        try {
            date_diff = simpleDateFormat.parse(mpref.getString("data", ""));


        } catch (ParseException e) {

        }


        long diff = date_current.getTime() - date_diff.getTime();

        int int_minutes = mpref.getInt("minutes", 0);
        int interval = mpref.getInt("count", 0);
        long interval_mili = TimeUnit.MINUTES.toMillis(interval);
        long int_timer = TimeUnit.MINUTES.toMillis(int_minutes);
        long long_hours = int_timer - diff;
        long diffSeconds2 = long_hours / 1000 % 60;
        long diffMinutes2 = long_hours / (60 * 1000) % 60;
        long diffHours2 = long_hours / (60 * 60 * 1000) % 24;

        long temp = int_timer - count * interval_mili;
        if (temp > long_hours) {
            count++;
            if (username.equals(misterX)) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("games").child(gamecode);
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                } else {
                    Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    if (location != null)
                        databaseReference.child("location").setValue(location);
                }
            }


        }


        String str_testing = diffHours2 + ":" + diffMinutes2 + ":" + diffSeconds2;
        if (long_hours >= 0) {
//            if(long_hours == 0){
//                mEditor.putBoolean("countdown_timer_finish", true).commit();
//                mTimer.cancel();
//            }
            fn_update(str_testing, false);
        } else {
            mEditor.putBoolean("countdown_timer_finish", true).commit();
            fn_update(str_testing, true);
            mTimer.cancel();
        }
        return "";
    }

    private void fn_update(String str_time, boolean isFinished) {

        intent.putExtra("countdown_timer_time", str_time);
        intent.putExtra("countdown_timer_finished", isFinished);
        sendBroadcast(intent);
    }
}
