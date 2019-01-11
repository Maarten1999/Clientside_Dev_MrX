package com.mpapps.clientside_dev_mrx.Services.Room;

import android.app.Application;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.mpapps.clientside_dev_mrx.Models.GameModel;

@Database(entities = {GameModel.class}, version = 2)
@TypeConverters({Converters.class})
public abstract class DatabaseService extends RoomDatabase
{
    private volatile static DatabaseService instance;
    public abstract GameModelDao gameModelDao();

    public static synchronized DatabaseService getInstance(Application application){
          if(instance == null){
              instance = Room.databaseBuilder(application, DatabaseService.class, "gamemodel-database")
                      .fallbackToDestructiveMigration()
                      .build();
          }
          return instance;
    }
}
