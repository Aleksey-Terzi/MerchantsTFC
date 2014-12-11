package com.aleksey.merchants.Core;

import net.minecraft.item.Item;

import com.aleksey.merchants.Items.ItemWarehouseBook;

import cpw.mods.fml.common.registry.GameRegistry;

public class ItemList
{
    public static Item WarehouseBook;
    
    public static void Setup()
    {
        WarehouseBook = new ItemWarehouseBook().setUnlocalizedName("WarehouseBook");

        GameRegistry.registerItem(WarehouseBook, WarehouseBook.getUnlocalizedName());
    }
}
