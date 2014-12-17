package com.aleksey.merchants.Helpers;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;

public class SearchTileEntity
{
    public TileEntity TileEntity;
    public ArrayList<SearchItem> Items;
    
    public SearchTileEntity(TileEntity tileEntity)
    {
        TileEntity = tileEntity;
        Items = new ArrayList<SearchItem>();
    }
}
