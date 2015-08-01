package com.aleksey.merchants.WarehouseContainers;

import java.util.ArrayList;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;

import com.aleksey.merchants.Containers.Slots.SlotStorageRack;
import com.aleksey.merchants.Helpers.ItemHelper;
import com.aleksey.merchants.Helpers.SmallVesselHelper;
import com.aleksey.merchants.TileEntities.TileEntityStorageRack;
import com.aleksey.merchants.api.ItemSlot;
import com.aleksey.merchants.api.ItemTileEntity;
import com.aleksey.merchants.api.Point;
import com.bioxx.tfc.Containers.ContainerChestTFC;
import com.bioxx.tfc.Containers.Slots.SlotChest;
import com.bioxx.tfc.Items.Pottery.ItemPotterySmallVessel;
import com.bioxx.tfc.TileEntities.TEChest;

public class ChestContainer extends Container
{
    private static final Class<?>[] _allowedTileEntities = {
        TileEntityChest.class,
        TEChest.class,
        TileEntityStorageRack.class
    };
	
    @Override
	public boolean isValid(TileEntity tileEntity)
	{
        for(Class<?> cls : _allowedTileEntities)
        {
            if(cls.isInstance(tileEntity))
                return true;
        }
        
        return false;
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
        if(!isItemValid(tileEntity, itemStack) || !canSearchFreeSpace(tileEntity))
            return 0;
        
        int quantity;
        
        quantity = searchFreeSpace_NonEmptySlots(tileEntity, itemStack, requiredQuantity, resultList);
        
        if(quantity > 0)
            quantity = searchFreeSpace_EmptySlots(tileEntity, itemStack, quantity, resultList);
        
        return requiredQuantity - quantity;
    }
    
    @Override
    public void confirmTradeGoods(
    		World world,
    		ItemTileEntity goodTileEntity,
    		ItemStack goodItemStack
    		)
	{
        boolean isGoodVessel = goodItemStack.getItem() instanceof ItemPotterySmallVessel;
        IInventory inventory = (IInventory)goodTileEntity.TileEntity;
        
        for(int k = 0; k < goodTileEntity.Items.size(); k++)
        {
            ItemSlot goodItem = goodTileEntity.Items.get(k);
            ItemStack itemStack = inventory.getStackInSlot(goodItem.SlotIndex);

            if(!isGoodVessel && itemStack.getItem() instanceof ItemPotterySmallVessel)
                SmallVesselHelper.decreaseItemStackQuantity(goodItem.Quantity, goodItemStack, itemStack);
            else
            {
                ItemHelper.increaseStackQuantity(itemStack, -goodItem.Quantity);
                
                if(itemStack.stackSize == 0)
                    inventory.setInventorySlotContents(goodItem.SlotIndex, (ItemStack)null);
            }
        }
	}
    
    @Override
    public void confirmTradePays(
    		World world,
    		ItemTileEntity payTileEntity,
    		ItemStack payItemStack,
    		ArrayList<Point> newContainers
    		)
	{
        boolean isPayVessel = payItemStack.getItem() instanceof ItemPotterySmallVessel;
        IInventory inventory = (IInventory)payTileEntity.TileEntity;
        
        for(int k = 0; k < payTileEntity.Items.size(); k++)
        {
            ItemSlot payItem = payTileEntity.Items.get(k);
            
            if(payItem.SlotIndex < 0)
                continue;
            
            ItemStack invItemStack = inventory.getStackInSlot(payItem.SlotIndex);
            
            if(invItemStack == null)
            {
                invItemStack = payItemStack.copy();
                
                ItemHelper.setStackQuantity(invItemStack, payItem.Quantity);
                
                inventory.setInventorySlotContents(payItem.SlotIndex, invItemStack);
            }
            else if(!isPayVessel && invItemStack.getItem() instanceof ItemPotterySmallVessel)
                SmallVesselHelper.increaseItemStackQuantity(payItem.Quantity, payItemStack, invItemStack);
            else
                ItemHelper.increaseStackQuantity(invItemStack, payItem.Quantity);
        }
	}
    
    //Helper methods
    
    protected boolean isItemValid(TileEntity tileEntity, ItemStack itemStack)
    {
        IInventory inventory = (IInventory)tileEntity;
        Class<?> cls = tileEntity.getClass();
        
        if(cls == TEChest.class)
            return new SlotChest(inventory, 0, 0, 0).addItemException(ContainerChestTFC.getExceptions()).isItemValid(itemStack);
        
        if(cls == TileEntityChest.class)
            return new Slot(inventory, 0, 0, 0).isItemValid(itemStack);

        if(cls == TileEntityStorageRack.class)
            return new SlotStorageRack(inventory, 0, 0, 0).isItemValid(itemStack);

        return false;
    }
    
    private int searchFreeSpace_NonEmptySlots(TileEntity tileEntity, ItemStack itemStack, int quantity, ArrayList<ItemTileEntity> resultList)
    {
        IInventory inventory = (IInventory)tileEntity;
        int maxStackQuantity = ItemHelper.getItemStackMaxQuantity(itemStack, inventory);
        ItemTileEntity itemTileEntity = resultList.size() > 0 ? resultList.get(resultList.size() - 1): null;
        
        if(itemTileEntity != null && itemTileEntity.TileEntity != tileEntity)
        	itemTileEntity = null;
        
        for(int i = 0; i < inventory.getSizeInventory() && quantity > 0; i++)
        {
            ItemStack invItemStack = inventory.getStackInSlot(i);
            
            if(invItemStack == null)
                continue;
            
            int addQuantity = 0;
            
            if(ItemHelper.areItemEquals(itemStack, invItemStack))
                addQuantity = maxStackQuantity - ItemHelper.getItemStackQuantity(invItemStack, false);
            else if(invItemStack.getItem() instanceof ItemPotterySmallVessel)
                addQuantity = SmallVesselHelper.getFreeSpace(itemStack, invItemStack);
            
            if(addQuantity <= 0)
                continue;
            
            if(itemTileEntity == null)
                resultList.add(itemTileEntity = new ItemTileEntity(this, tileEntity));
            
            ItemSlot itemSlot = new ItemSlot(i, addQuantity);
            
            if(itemSlot.Quantity > quantity)
            	itemSlot.Quantity = quantity;                

            itemTileEntity.Items.add(itemSlot);
            
            quantity -= itemSlot.Quantity;
        }
        
        return quantity;
    }
    
    private int searchFreeSpace_EmptySlots(TileEntity tileEntity, ItemStack itemStack, int quantity, ArrayList<ItemTileEntity> resultList)
    {
        IInventory inventory = (IInventory)tileEntity;
        int maxStackQuantity = ItemHelper.getItemStackMaxQuantity(itemStack, inventory);
        ItemTileEntity itemTileEntity = resultList.size() > 0 ? resultList.get(resultList.size() - 1): null;
        
        if(itemTileEntity != null && itemTileEntity.TileEntity != tileEntity)
        	itemTileEntity = null;
        
        for(int i = 0; i < inventory.getSizeInventory() && quantity > 0; i++)
        {
            ItemStack invItemStack = inventory.getStackInSlot(i);
            
            if(invItemStack != null)
                continue;
            
            if(itemTileEntity == null)
                resultList.add(itemTileEntity = new ItemTileEntity(this, tileEntity));
            
            ItemSlot itemSlot = new ItemSlot(i, maxStackQuantity);

            if(itemSlot.Quantity > quantity)
            	itemSlot.Quantity = quantity;
            
            itemTileEntity.Items.add(itemSlot);
            
            quantity -= itemSlot.Quantity;
        }
        
        return quantity;
    }
}