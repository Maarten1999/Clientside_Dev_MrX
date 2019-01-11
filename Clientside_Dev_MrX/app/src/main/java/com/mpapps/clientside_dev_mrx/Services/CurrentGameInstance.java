package com.mpapps.clientside_dev_mrx.Services;

import android.arch.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.mpapps.clientside_dev_mrx.Models.GameModel;
import com.mpapps.clientside_dev_mrx.Models.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CurrentGameInstance
{
    private static CurrentGameInstance instance;

    public static void initialize(GameModel model){
        instance = new CurrentGameInstance(model);
    }
    public static CurrentGameInstance getInstance(){
        return instance;
    }

    private MutableLiveData<GameModel> gameModel;
    private MutableLiveData<List<Player>> players;
    private MutableLiveData<String> gameCode;

    private MutableLiveData<Map<String, LatLng>> playerLocations;

    private CurrentGameInstance(GameModel gameModel)
    {
        this.gameModel = new MutableLiveData<>();
        this.gameModel.postValue(gameModel);
        this.players = new MutableLiveData<>();
        this.players.postValue(new ArrayList<>());
        this.gameCode = new MutableLiveData<>();
        this.gameCode.setValue("");
        this.playerLocations = new MutableLiveData<>();
        Map<String, LatLng> playerLocs = new HashMap<>();
        for (String s : gameModel.getPlayers().keySet()) {
            playerLocs.put(s, new LatLng(51.866402 + (new Random().nextInt() / 100), 4.661810 + new Random().nextInt() / 100));
        }
        this.playerLocations.postValue(new HashMap<>());
    }

    public MutableLiveData<GameModel> getGameModel()
    {
        return gameModel;
    }

    public void setGameModel(GameModel gameModel)
    {
        this.gameModel.postValue(gameModel);
    }

    public MutableLiveData<List<Player>> getPlayers()
    {
        return players;
    }

    public void setPlayers(List<Player> players)
    {
        this.players.postValue(players);
    }

    public MutableLiveData<String> getGameCode()
    {
        return gameCode;
    }

    public void setGameCode(String gameCode)
    {
        this.gameCode.postValue(gameCode);
    }

    public MutableLiveData<Map<String, LatLng>> getPlayerLocations()
    {
        return playerLocations;
    }

    public void addPlayerLocation(String player, LatLng latLng)
    {
        Map<String, LatLng> tempLocs = playerLocations.getValue();
        tempLocs.put(player, latLng);
        playerLocations.postValue(tempLocs);
    }
}
