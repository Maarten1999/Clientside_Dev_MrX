package com.mpapps.clientside_dev_mrx.Models;

public class Player
{
    private String initials;
    private String name;
    private boolean isMisterX;

    public Player(String initials, String name, boolean isMisterX)
    {
        this.initials = initials;
        this.name = name;
        this.isMisterX = isMisterX;
    }

    public boolean isMisterX()
    {
        return isMisterX;
    }

    public void setMisterX(boolean misterX)
    {
        isMisterX = misterX;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getInitials()
    {
        return initials;
    }

    public void setInitials(String initials)
    {
        this.initials = initials;
    }
}
