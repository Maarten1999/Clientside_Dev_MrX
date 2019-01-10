package com.mpapps.clientside_dev_mrx.Models;

public class GameLevel
{
    private GameMode gameMode;
    private int gameDuration;
    private int gpsInterval;

    public GameLevel(GameMode gameMode, int gameDuration, int gpsInterval)
    {
        this.gameMode = gameMode;
        this.gameDuration = gameDuration;
        this.gpsInterval = gpsInterval;
    }

    public GameMode getGameMode()
    {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode)
    {
        this.gameMode = gameMode;
    }

    public int getGameDuration()
    {
        return gameDuration;
    }

    public void setGameDuration(int gameDuration)
    {
        this.gameDuration = gameDuration;
    }

    public int getGpsInterval()
    {
        return gpsInterval;
    }

    public void setGpsInterval(int gpsInterval)
    {
        this.gpsInterval = gpsInterval;
    }
}
