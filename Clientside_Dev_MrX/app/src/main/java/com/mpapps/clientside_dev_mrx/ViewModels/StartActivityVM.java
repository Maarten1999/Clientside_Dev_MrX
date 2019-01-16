package com.mpapps.clientside_dev_mrx.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.mpapps.clientside_dev_mrx.Models.GameModel;
import com.mpapps.clientside_dev_mrx.Services.Room.GameModelRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class StartActivityVM extends AndroidViewModel
{
    private GameModelRepository repository;
    private LiveData<List<GameModel>> historyGames;
    private MutableLiveData<List<GameModel>> currentGames;

    public StartActivityVM(@NonNull Application application)
    {
        super(application);
        repository = new GameModelRepository(application);
        historyGames = repository.getAllGameModels();
        currentGames = new MutableLiveData<>();
        currentGames.postValue(new ArrayList<>());
    }

    public void insertGameModel(GameModel model) {
        repository.insertGameModel(model);
    }

    public LiveData<List<GameModel>> getHistoryGames()
    {
        return historyGames;
    }

    public LiveData<List<GameModel>> getCurrentGames()
    {
        return currentGames;
    }

    public void deleteCurrentGame(GameModel del){
        List<GameModel> tempCurrentGames = currentGames.getValue();
        ListIterator<GameModel> iterator = tempCurrentGames.listIterator();
        while(iterator.hasNext()) {
            GameModel temp = iterator.next();
            if (temp.getMode() == del.getMode() && temp.getDate() == del.getDate() && temp.getName().equals(del.getName())) {
                iterator.remove();
            }
        }
        currentGames.postValue(tempCurrentGames);
    }

    public void addCurrentGame(GameModel model)
    {
        List<GameModel> models = currentGames.getValue();
        models.clear();
        models.add(model);
        currentGames.postValue(models);
    }
}
