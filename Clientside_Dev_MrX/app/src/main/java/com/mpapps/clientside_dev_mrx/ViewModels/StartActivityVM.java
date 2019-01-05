package com.mpapps.clientside_dev_mrx.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.mpapps.clientside_dev_mrx.Models.GameMode;
import com.mpapps.clientside_dev_mrx.Models.GameModel;
import com.mpapps.clientside_dev_mrx.Services.Room.GameModelRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StartActivityVM extends AndroidViewModel
{
    private GameModelRepository repository;
    private LiveData<List<GameModel>> gameModels;

    public StartActivityVM(@NonNull Application application)
    {
        super(application);
        repository = new GameModelRepository(application);
        gameModels = repository.getAllGameModels();
    }

    public void insertGameModel(GameModel model) {
        repository.insertGameModel(model);
    }

    public LiveData<List<GameModel>> getGameModels()
    {
        return gameModels;
    }
}
