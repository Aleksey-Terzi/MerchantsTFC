package com.aleksey.merchants.Core;

import net.minecraft.item.Item;

import com.aleksey.merchants.Items.ItemAnvilDie;
import com.aleksey.merchants.Items.ItemCoin;
import com.aleksey.merchants.Items.ItemFlan;
import com.aleksey.merchants.Items.ItemTrussel;
import com.aleksey.merchants.Items.ItemWarehouseBook;

import cpw.mods.fml.common.registry.GameRegistry;

public class ItemList
{
    public static Item WarehouseBook;
    public static Item Flan;
    public static Item Coin;
    public static Item Trussel;
    public static Item AnvilDie;
    
    public static void Setup()
    {
        WarehouseBook = new ItemWarehouseBook().setUnlocalizedName("WarehouseBook");
        Flan = new ItemFlan().setUnlocalizedName("Flan");
        Coin = new ItemCoin().setUnlocalizedName("Coin");
        Trussel = new ItemTrussel().setUnlocalizedName("Trussel");
        AnvilDie = new ItemAnvilDie().setUnlocalizedName("AnvilDie");

        GameRegistry.registerItem(WarehouseBook, WarehouseBook.getUnlocalizedName());
        GameRegistry.registerItem(Flan, Flan.getUnlocalizedName());
        GameRegistry.registerItem(Coin, Coin.getUnlocalizedName());
        GameRegistry.registerItem(Trussel, Trussel.getUnlocalizedName());
        GameRegistry.registerItem(AnvilDie, AnvilDie.getUnlocalizedName());
    }
}