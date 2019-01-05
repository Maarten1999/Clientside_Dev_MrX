package com.mpapps.clientside_dev_mrx.Services.Room;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mpapps.clientside_dev_mrx.Models.GameMode;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;

public class Converters
{
    @TypeConverter
    public static Date toDate(Long value)
    {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long fromDate(Date value){
        return value == null ? null : value.getTime();
    }

    @TypeConverter
    public static Map<String, Boolean> toMap(String value){
        Type mapType = new TypeToken<Map<String, Boolean>>(){}.getType();
        return new Gson().fromJson(value, mapType);
    }

    @TypeConverter
    public static String toStringMap(Map<String, Boolean> map){
        Gson gson = new Gson();
        return gson.toJson(map);
    }

    @TypeConverter
    public static GameMode toGameMode(String value){
        return GameMode.valueOf(value);
    }

    @TypeConverter
    public static String fromGameMode(GameMode mode){
        return mode.name();
    }


}
