package com.aleksey.merchants.Core;

public class CoinInfo
{
    public String CoinName;
    public int DieColor;
    public String MetalName;
    public int Level;
    
    public CoinInfo(String coinName, int dieColor, String metalName, int level)
    {
        CoinName = coinName;
        DieColor = dieColor;
        MetalName = metalName;
        Level = level;
    }
}
