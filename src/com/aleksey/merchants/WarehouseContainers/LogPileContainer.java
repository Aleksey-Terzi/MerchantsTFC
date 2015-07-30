package com.aleksey.merchants.WarehouseContainers;

import java.util.ArrayList;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.aleksey.merchants.api.ItemSlot;
import com.aleksey.merchants.api.ItemTileEntity;
import com.aleksey.merchants.api.Point;
import com.bioxx.tfc.Containers.Slots.SlotLogPile;
import com.bioxx.tfc.TileEntities.TELogPile;
import com.bioxx.tfc.api.TFCBlocks;

public class LogPileContainer extends ChestContainer
{
    @Override
	public boolean isValid(TileEntity tileEntity)
	{
    	return tileEntity.getClass() == TELogPile.class;
	}
	
    @Override
    public int searchFreeSpace(
            World world,
            TileEntity tileEntity,
            ItemStack itemStack,
            int requiredQuantity,
            int maxY,
            ArrayList<ItemTileEntity> resultList
            )
    {
        if(!isItemValid(tileEntity, itemStack))
            return 0;

        int freeSpace = super.searchFreeSpace(
                world,
                tileEntity,
                itemStack,
                requiredQuantity,
                maxY,
                resultList
                );
    	
    	if(freeSpace == requiredQuantity)
    		return freeSpace;
    	
    	int quantity = searchFreeSpace_LogPile(world, tileEntity, itemStack, requiredQuantity - freeSpace, maxY, resultList);
        
        return requiredQuantity - quantity;
    }
    
    @Override
    public void confirmTradeGoods(
    		World world,
    		ItemTileEntity goodTileEntity,
    		ItemStack goodItemStack
    		)
	{
        IInventory inventory = (IInventory)goodTileEntity.TileEntity;
        
        inventory.openInventory();
        
        super.confirmTradeGoods(world, goodTileEntity, goodItemStack);
        
        inventory.closeInventory();
	}
    
    @Override
    public void confirmTradePays(
    		World world,
    		ItemTileEntity payTileEntity,
    		ItemStack payItemStack,
    		ArrayList<Point> newContainers
    		)
	{
        IInventory inventory = (IInventory)payTileEntity.TileEntity;
        
        inventory.openInventory();
        
        super.confirmTradePays(world, payTileEntity, payItemStack, newContainers);
        
        for(int k = 0; k < payTileEntity.Items.size(); k++)
        {
            ItemSlot payItem = payTileEntity.Items.get(k);
            
            if(payItem.SlotIndex < 0)
                confirmTradePays_NewLogPile(world, payTileEntity, payItemStack, payItem, newContainers);
        }
        
        inventory.closeInventory();
	}
    
    //Helper methods
    
    @Override
    protected boolean isItemValid(TileEntity tileEntity, ItemStack itemStack)
    {
        IInventory inventory = (IInventory)tileEntity;
        Class<?> cls = tileEntity.getClass();
        
        if(cls == TELogPile.class)
            return new SlotLogPile(null, inventory, 0, 0, 0).isItemValid(itemStack);

        return false;
    }
    
    private int searchFreeSpace_LogPile(
            World world,
            TileEntity tileEntity,
            ItemStack itemStack,
            int quantity,
            int maxY,
            ArrayList<ItemTileEntity> resultList
            )
    {
        if(tileEntity.yCoord == maxY || !world.isAirBlock(tileEntity.xCoord, tileEntity.yCoord + 1, tileEntity.zCoord))
            return quantity;
        
        ItemTileEntity itemTileEntity = resultList.size() > 0 ? resultList.get(resultList.size() - 1): null;
        
        if(itemTileEntity != null && itemTileEntity.TileEntity != tileEntity)
            itemTileEntity = null;
        
        if(itemTileEntity == null)
            resultList.add(itemTileEntity = new ItemTileEntity(this, tileEntity));
            
        itemTileEntity.Items.add(new ItemSlot(-1, quantity));
            
        return 0;
    }
    
    private void confirmTradePays_NewLogPile(
            World world,
            ItemTileEntity payTileEntity,
            ItemStack payItemStack,
            ItemSlot payItem,
            ArrayList<Point> containers
            )
    {
        int x = payTileEntity.TileEntity.xCoord;
        int y = payTileEntity.TileEntity.yCoord + 1;
        int z = payTileEntity.TileEntity.zCoord;
        
        world.setBlock(x, y, z, TFCBlocks.LogPile, 0, 3);
        
        Item item = payItemStack.getItem();
        
        TELogPile logPile = (TELogPile)world.getTileEntity(x, y, z);
        IInventory inventory = (IInventory)logPile;
        int quantity = payItem.Quantity;
        
        for(int i = 0; i < inventory.getSizeInventory() && quantity > 0; i++)
        {
            int addQuantity = quantity < inventory.getInventoryStackLimit() ? quantity: inventory.getInventoryStackLimit();
            
            inventory.setInventorySlotContents(i, new ItemStack(item, addQuantity, payItemStack.getItemDamage()));
            
            quantity -= addQuantity;
        }
        
        world.markBlockForUpdate(x, y, z);
        
        containers.add(new Point(x, y, z));
    }
}