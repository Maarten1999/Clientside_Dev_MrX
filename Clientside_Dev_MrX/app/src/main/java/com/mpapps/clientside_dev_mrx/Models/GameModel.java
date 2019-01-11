package com.mpapps.clientside_dev_mrx.Models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;
import java.util.Map;

@Entity
public class GameModel
{
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private GameMode mode;
    private Map<String, Boolean> players;
    private Date date;
    private boolean isWon;

    public GameModel(String name, GameMode mode, Map<String, Boolean> players, Date date, boolean isWon)
    {
        this.name = name;
        this.mode = mode;
        this.players = players;
        this.date = date;
        this.isWon = isWon;
    }

    @NonNull
    public int getId()
    {
        return id;
    }

    public void setId(@NonNull int id)
    {
        this.id = id;
    }

    public GameMode getMode()
    {
        return mode;
    }

    public void setMode(GameMode mode)
    {
        this.mode = mode;
    }

    public Map<String, Boolean> getPlayers()
    {
        return players;
    }

    public void setPlayers(Map<String, Boolean> players)
    {
        this.players = players;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public boolean isWon()
    {
        return isWon;
    }

    public void setWon(boolean won)
    {
        isWon = won;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}

