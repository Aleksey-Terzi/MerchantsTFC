package com.aleksey.merchants.api;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;

public class ItemTileEntity
{
	public IWarehouseContainer Container;
    public TileEntity TileEntity;
    public ArrayList<ItemSlot> Items;
    
    public ItemTileEntity(IWarehouseContainer container, TileEntity tileEntity)
    {
    	Container = container;
        TileEntity = tileEntity;
        Items = new ArrayList<ItemSlot>();
    }

}
