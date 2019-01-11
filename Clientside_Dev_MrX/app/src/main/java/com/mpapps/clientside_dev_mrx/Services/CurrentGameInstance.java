package com.mpapps.clientside_dev_mrx.Services;

import android.arch.lifecycle.MutableLiveData;

import com.mpapps.clientside_dev_mrx.Models.GameModel;
import com.mpapps.clientside_dev_mrx.Models.Player;

import java.util.List;

public class CurrentGameInstance
{
    private static CurrentGameInstance instance;

    public static void initialize(GameModel model, List<Player> players){
        instance = new CurrentGameInstance(model, players);
    }

    public static CurrentGameInstance getInstance(){
        return instance;
    }

    private GameModel gameModel;
    private MutableLiveData<List<Player>> players;
    private MutableLiveData<String> gameCode;

    private CurrentGameInstance(GameModel gameModel, List<Player> players)
    {
        this.gameModel = gameModel;
        this.players = new MutableLiveData<>();
        this.players.setValue(players);
        this.gameCode = new MutableLiveData<>();
        gameCode.setValue("");
    }

    public GameModel getGameModel()
    {
        return gameModel;
    }

    public void setGameModel(GameModel gameModel)
    {
        this.gameModel = gameModel;
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
}
