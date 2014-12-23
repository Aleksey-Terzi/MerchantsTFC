package com.aleksey.merchants.Helpers;

public class CoinHelper
{
    public static String getOz(int weightIndex)
    {
        switch(weightIndex)
        {
            case 1:
                return "50";
            case 20:
                return "2.5";
            case 50:
                return "1";
            case 100:
                return "0.5";
            case 200:
                return "0.25";
            default:
                return "";
        }
    }
}
