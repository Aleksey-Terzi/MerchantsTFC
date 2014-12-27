package com.aleksey.merchants.Core;

public class Constants
{
    private static final int _whiteColor = 0xffffff;
    private static final int _blackColor = 0;
    
    public static final CoinInfo[] Coins = new CoinInfo[] {
        new CoinInfo("Bismuth", _whiteColor, "item.Bismuth Ingot", 0),
        new CoinInfo("Tin", _blackColor, "item.Tin Ingot", 0),
        new CoinInfo("Zinc", _blackColor, "item.Zinc Ingot", 0),
        new CoinInfo("Copper", _blackColor, "item.Copper Ingot", 1),
        new CoinInfo("Bronze", _whiteColor, "item.Bronze Ingot", 2),
        new CoinInfo("BismuthBronze", _whiteColor, "item.Bismuth Bronze Ingot", 2),
        new CoinInfo("BlackBronze", _whiteColor, "item.Black Bronze Ingot", 2),
        new CoinInfo("Brass", _blackColor, "item.Brass Ingot", 2),
        new CoinInfo("Lead", _whiteColor, "item.Lead Ingot", 2),
        new CoinInfo("Gold", _blackColor, "item.Gold Ingot", 2),
        new CoinInfo("RoseGold", _blackColor, "item.Rose Gold Ingot", 2),
        new CoinInfo("Silver", _blackColor, "item.Silver Ingot", 2),
        new CoinInfo("SterlingSilver", _blackColor, "item.Sterling Silver Ingot", 2),
        new CoinInfo("Platinum", _blackColor, "item.Platinum Ingot", 3),
        new CoinInfo("WroughtIron", _blackColor, "item.Wrought Iron Ingot", 3),
        new CoinInfo("Nickel", _whiteColor, "item.Nickel Ingot", 4),
        new CoinInfo("Steel", _whiteColor, "item.Steel Ingot", 4),
        new CoinInfo("BlackSteel", _whiteColor, "item.Black Steel Ingot", 5),
    };
    
    public static final DieInfo[] Dies = new DieInfo[] {
        new DieInfo("Copper", "item.Copper Ingot", "item.Copper Double Ingot", 1),
        new DieInfo("Bronze", "item.Bronze Ingot", "item.Bronze Double Ingot", 2),
        new DieInfo("BismuthBronze", "item.Bismuth Bronze Ingot", "item.Bismuth Bronze Double Ingot", 2),
        new DieInfo("BlackBronze", "item.Black Bronze Ingot", "item.Black Bronze Double Ingot", 2),
        new DieInfo("Brass", "item.Brass Ingot", "item.Brass Double Ingot", 2),
        new DieInfo("Lead", "item.Lead Ingot", "item.Lead Double Ingot", 2),
        new DieInfo("Gold", "item.Gold Ingot", "item.Gold Double Ingot", 2),
        new DieInfo("RoseGold", "item.Rose Gold Ingot", "item.Rose Gold Double Ingot", 2),
        new DieInfo("Silver", "item.Silver Ingot", "item.Silver Double Ingot", 2),
        new DieInfo("SterlingSilver", "item.Sterling Silver Ingot", "item.Sterling Silver Double Ingot", 2),
        new DieInfo("Platinum", "item.Platinum Ingot", "item.Platinum Double Ingot", 3),
        new DieInfo("WroughtIron", "item.Wrought Iron Ingot", "item.Wrought Iron Double Ingot", 3),
        new DieInfo("Nickel", "item.Nickel Ingot", "item.Nickel Double Ingot", 4),
        new DieInfo("Steel", "item.Steel Ingot", "item.Steel Double Ingot", 4),
        new DieInfo("BlackSteel", "item.Black Steel Ingot", "item.Black Steel Double Ingot", 5),
        new DieInfo("BlueSteel", "item.Black Steel Ingot", "item.Black Steel Double Ingot", 6),
        new DieInfo("RedSteel", "item.Black Steel Ingot", "item.Black Steel Double Ingot", 6),
    };
}