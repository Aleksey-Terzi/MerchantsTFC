package com.aleksey.merchants.Core;

import net.minecraft.item.Item;

import com.aleksey.merchants.Items.ItemCoin;
import com.aleksey.merchants.Items.ItemTrussel;
import com.aleksey.merchants.Items.ItemWarehouseBook;

import cpw.mods.fml.common.registry.GameRegistry;

public class ItemList
{
    public static Item WarehouseBook;
    public static Item Coin;
    public static Item Trussel;
    
    public static void Setup()
    {
        WarehouseBook = new ItemWarehouseBook().setUnlocalizedName("WarehouseBook");
        Coin = new ItemCoin().setUnlocalizedName("Coin");
        Trussel = new ItemTrussel().setUnlocalizedName("Trussel");

        GameRegistry.registerItem(WarehouseBook, WarehouseBook.getUnlocalizedName());
        GameRegistry.registerItem(Coin, Coin.getUnlocalizedName());
        GameRegistry.registerItem(Trussel, Trussel.getUnlocalizedName());
    }
}
