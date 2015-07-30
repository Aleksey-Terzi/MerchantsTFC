package com.aleksey.merchants.api;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract interface IWarehouseContainer
{
	//returns - true if tile entity could be used as container for trade
	public abstract boolean isValid(TileEntity tileEntity);
	
	//Search goods item
	//
	//tileEntity - tile entity where to search item
	//itemStack - item to find
	//requiredQuantity - quantity to find
	//resultList - put here result
	//
	//returns - quantity of found items
	public abstract int searchItems(
	        TileEntity tileEntity,
	        ItemStack itemStack,
	        int requiredQuantity,
	        ArrayList<ItemTileEntity> resultList
	        );
		
	//Search free space to put payment
	//
	//tileEntity - tile entity where to search free space
	//itemStack - item to put into the tile entity
	//requiredQuantity - quantity to put into the tile entity
	//maxY - max allowed Y, used by extendable containers (like ingot pile or log pile)
	//resultList - put here result
	//
	//returns - quantity of items for which free space is found
    public abstract int searchFreeSpace(
            World world,
            TileEntity tileEntity,
            ItemStack itemStack,
            int requiredQuantity,
            int maxY,
            ArrayList<ItemTileEntity> resultList
            );
    
    //Search free space in small vessels to put payment
    //
    //tileEntity - tile entity where to search free space
    //itemStack - item to put into the tile entity
    //requiredQuantity - quantity to put into the tile entity
    //resultList - put here result
    //
    //returns - quantity of items for which free space is found
    public abstract int searchFreeSpaceInSmallVessels(
            TileEntity tileEntity,
            ItemStack itemStack,
            int requiredQuantity,
            ArrayList<ItemTileEntity> resultList
            );
    
    //Takes goods from the container
    public abstract void confirmTradeGoods(
    		World world,
    		ItemTileEntity goodTileEntity,
    		ItemStack goodItemStack
    		);
    
    //Put payment into the container
    //
    //newContainers - coordinates of new containers if such were added (for example as for ingot piles and log piles)
    public abstract void confirmTradePays(
    		World world,
    		ItemTileEntity payTileEntity,
    		ItemStack payItemStack,
    		ArrayList<Point> newContainers
    		);
}