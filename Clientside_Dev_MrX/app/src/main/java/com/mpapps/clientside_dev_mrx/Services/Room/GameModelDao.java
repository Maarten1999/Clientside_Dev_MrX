package com.mpapps.clientside_dev_mrx.Services.Room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.mpapps.clientside_dev_mrx.Models.GameModel;

import java.util.List;

@Dao
public interface GameModelDao
{
    @Query("select * from GameModel")
    LiveData<List<GameModel>> loadAllGameModels();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertGameModel(GameModel model);

    @Delete
    void deleteGameModel(GameModel model);
}
