package com.mpapps.clientside_dev_mrx.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.mpapps.clientside_dev_mrx.Models.Player;
import com.mpapps.clientside_dev_mrx.Services.CurrentGameInstance;

import java.util.List;

public class DetailedGameVM extends AndroidViewModel
{
    public DetailedGameVM(@NonNull Application application)
    {
        super(application);
    }

    public MutableLiveData<List<Player>> getPlayers()
    {
        return CurrentGameInstance.getInstance().getPlayers();
    }

    public MutableLiveData<String> getGameCode(){
        return CurrentGameInstance.getInstance().getGameCode();
    }
}
