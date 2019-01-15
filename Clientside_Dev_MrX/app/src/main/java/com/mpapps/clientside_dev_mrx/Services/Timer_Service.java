package com.mpapps.clientside_dev_mrx.Services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mpapps.clientside_dev_mrx.Models.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Timer_Service extends Service
{
    private Handler handler = new Handler();
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    String strDate;
    Date date_current, date_diff;
    SharedPreferences mpref;
    SharedPreferences.Editor mEditor;

    private Timer mTimer = null;
    public static final long NOTIFY_INTERVAL = 1000;
    Intent intent;
    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mEditor = mpref.edit();
        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new CountDownTimerTask(), 5, NOTIFY_INTERVAL);
        intent = new Intent(Constants.str_receiver_countdown_timer);
    }

    class CountDownTimerTask extends TimerTask {

        @Override
        public void run()
        {
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    calendar = Calendar.getInstance();
                    simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                    strDate = simpleDateFormat.format(calendar.getTime());
                    Log.e("strDate", strDate);
                    twoDatesBetweenTime();
                }
            });
        }
    }

    public String twoDatesBetweenTime(){
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

        long int_timer = TimeUnit.MINUTES.toMillis(int_minutes);
        long long_hours = int_timer - diff;
        long diffSeconds2 = long_hours / 1000 % 60;
        long diffMinutes2 = long_hours / (60 * 1000) % 60;
        long diffHours2 = long_hours / (60 * 60 * 1000) % 24;

        if(long_hours > 0){
            String str_testing = diffHours2 + ":" + diffMinutes2 + ":" + diffSeconds2;
            fn_update(str_testing);
        }
        else {
            mEditor.putBoolean("countdown_timer_finish", true).commit();
            mTimer.cancel();
        }
        return "";
    }

    private void fn_update(String str_time){

        intent.putExtra("countdown_timer_time",str_time);
        sendBroadcast(intent);
    }
}
