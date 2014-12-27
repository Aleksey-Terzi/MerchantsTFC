package com.aleksey.merchants.Core;

public class DieInfo
{
    public String DieName;
    public String TrusselMetalName;
    public String AnvilDieMetalName;
    public int Level;
    
    public DieInfo(String dieName, String trusselMetalName, String anvilDieMetalName, int level)
    {
        DieName = dieName;
        TrusselMetalName = trusselMetalName;
        AnvilDieMetalName = anvilDieMetalName;
        Level = level;
    }
}
