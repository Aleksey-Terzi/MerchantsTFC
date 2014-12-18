package com.aleksey.merchants.Helpers;

import java.util.ArrayList;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;

import com.aleksey.merchants.Containers.Slots.SlotIngotPile;
import com.bioxx.tfc.Containers.ContainerChestTFC;
import com.bioxx.tfc.Containers.Slots.SlotChest;
import com.bioxx.tfc.Containers.Slots.SlotLogPile;
import com.bioxx.tfc.Items.Pottery.ItemPotterySmallVessel;
import com.bioxx.tfc.TileEntities.TEBarrel;
import com.bioxx.tfc.TileEntities.TEChest;
import com.bioxx.tfc.TileEntities.TEIngotPile;
import com.bioxx.tfc.TileEntities.TELogPile;
import com.bioxx.tfc.TileEntities.TEVessel;
import com.bioxx.tfc.api.Food;
import com.bioxx.tfc.api.Enums.EnumSize;
import com.bioxx.tfc.api.Interfaces.IFood;

public class SearchHelper
{
    public static boolean canSearchItem(TileEntity tileEntity)
    {
        if(!(tileEntity instanceof TEBarrel))
            return true;
        
        TEBarrel barrel = (TEBarrel)tileEntity;
        
        if(!barrel.getSealed())
            return true;
        
        ItemStack itemStack = barrel.getInputStack();
        
        return barrel.getFluidLevel() > 0
                && itemStack != null
                && (itemStack.getItem() instanceof IFood)
                && Food.isPickled(itemStack);
    }
    
    private static boolean canSearchFreeSpace(TileEntity tileEntity)
    {
        if(!(tileEntity instanceof TEBarrel))
            return true;
        
        TEBarrel barrel = (TEBarrel)tileEntity;
        
        return !barrel.getSealed() && barrel.getFluidLevel() == 0;
    }
    
    public static int searchItems(ItemStack itemStack, int requiredQuantity, TileEntity tileEntity, ArrayList<SearchTileEntity> searchList)
    {
        if(!canSearchItem(tileEntity))
            return 0;
        
        IInventory inventory = (IInventory)tileEntity;
        SearchTileEntity searchTileEntity = null;
        int quantity = requiredQuantity;
        
        for(int i = 0; i < inventory.getSizeInventory() && quantity > 0; i++)
        {
            ItemStack invItemStack = inventory.getStackInSlot(i);
            
            if(invItemStack == null)
                continue;
            
            int invQuantity = 0;
            
            if(ItemHelper.areItemEquals(itemStack, invItemStack))
                invQuantity = ItemHelper.getItemStackQuantity(invItemStack);
            else if(invItemStack.getItem() instanceof ItemPotterySmallVessel)
                invQuantity = SmallVesselHelper.getItemStackQuantity(itemStack, invItemStack);
            
            if(invQuantity <= 0)
                continue;
            
            if(searchTileEntity == null)
                searchList.add(searchTileEntity = new SearchTileEntity(tileEntity));
            
            SearchItem searchItem = new SearchItem(i, invQuantity < quantity ? invQuantity: quantity);
            
            searchTileEntity.Items.add(searchItem);
            
            quantity -= searchItem.Quantity;
        }
        
        return requiredQuantity - quantity;
    }

    public static int searchFreeSpaceInSmallVessels(ItemStack itemStack, int requiredQuantity, TileEntity tileEntity, ArrayList<SearchTileEntity> searchList)
    {
        if(!canSearchFreeSpace(tileEntity))
            return 0;
        
        IInventory inventory = (IInventory)tileEntity;
        SearchTileEntity searchTileEntity = searchList.size() > 0 ? searchList.get(searchList.size() - 1): null;
        
        if(searchTileEntity != null && searchTileEntity.TileEntity != tileEntity)
            searchTileEntity = null;
        
        int quantity = requiredQuantity;
        
        for(int i = 0; i < inventory.getSizeInventory() && quantity > 0; i++)
        {
            ItemStack invItemStack = inventory.getStackInSlot(i);
            
            if(invItemStack == null || !(invItemStack.getItem() instanceof ItemPotterySmallVessel))
                continue;
            
            int addQuantity = SmallVesselHelper.getFreeSpace(itemStack, invItemStack);
            
            if(addQuantity <= 0)
                continue;
            
            if(searchTileEntity == null)
                searchList.add(searchTileEntity = new SearchTileEntity(tileEntity));
            
            SearchItem searchItem = new SearchItem(i, addQuantity);
            
            if(searchItem.Quantity > quantity)
                searchItem.Quantity = quantity;                

            searchTileEntity.Items.add(searchItem);
            
            quantity -= searchItem.Quantity;
        }
        
        return requiredQuantity - quantity;
    }
    
    public static int searchFreeSpace(ItemStack itemStack, int requiredQuantity, TileEntity tileEntity, World world, ArrayList<SearchTileEntity> searchList)
    {
        if(!getSlot(tileEntity).isItemValid(itemStack) || !canSearchFreeSpace(tileEntity))
            return 0;
        
        int quantity;
        
        if(tileEntity instanceof TEIngotPile)
            quantity = searchFreeSpace_Ingot(itemStack, requiredQuantity, tileEntity, world, searchList);
        else
        {
            quantity = searchFreeSpace_NonEmptySlots(itemStack, requiredQuantity, tileEntity, searchList);
            quantity = searchFreeSpace_EmptySlots(itemStack, quantity, tileEntity, searchList);
        }
        
        return requiredQuantity - quantity;
    }
    
    private static int searchFreeSpace_Ingot(ItemStack itemStack, int quantity, TileEntity tileEntity, World world, ArrayList<SearchTileEntity> searchList)
    {
        IInventory inventory = (IInventory)tileEntity;
        ItemStack invItemStack = inventory.getStackInSlot(0);
        
        if(invItemStack != null && !ItemHelper.areItemEquals(itemStack, invItemStack))
            return quantity;
        
        SearchTileEntity searchTileEntity = searchList.size() > 0 ? searchList.get(searchList.size() - 1): null;
        
        if(searchTileEntity != null && searchTileEntity.TileEntity != tileEntity)
            searchTileEntity = null;
        
        int maxStackQuantity = ItemHelper.getItemStackMaxQuantity(itemStack, inventory);
        int invQuantity = invItemStack != null ? ItemHelper.getItemStackQuantity(invItemStack): 0;
        
        int addQuantity = quantity + invQuantity <= maxStackQuantity || world.isAirBlock(tileEntity.xCoord, tileEntity.yCoord + 1, tileEntity.zCoord)
                ? quantity
                : maxStackQuantity - invQuantity;
        
        if(addQuantity > 0)
        {
            if(searchTileEntity == null)
                searchList.add(searchTileEntity = new SearchTileEntity(tileEntity));
            
            searchTileEntity.Items.add(new SearchItem(0, addQuantity));
            
            quantity -= addQuantity;
        }
        
        return quantity;
    }
    
    private static int searchFreeSpace_NonEmptySlots(ItemStack itemStack, int quantity, TileEntity tileEntity, ArrayList<SearchTileEntity> searchList)
    {
        IInventory inventory = (IInventory)tileEntity;
        int maxStackQuantity = ItemHelper.getItemStackMaxQuantity(itemStack, inventory);
        SearchTileEntity searchTileEntity = searchList.size() > 0 ? searchList.get(searchList.size() - 1): null;
        
        if(searchTileEntity != null && searchTileEntity.TileEntity != tileEntity)
            searchTileEntity = null;
        
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
            
            if(searchTileEntity == null)
                searchList.add(searchTileEntity = new SearchTileEntity(tileEntity));
            
            SearchItem searchItem = new SearchItem(i, addQuantity);
            
            if(searchItem.Quantity > quantity)
                searchItem.Quantity = quantity;                

            searchTileEntity.Items.add(searchItem);
            
            quantity -= searchItem.Quantity;
        }
        
        return quantity;
    }
    
    private static int searchFreeSpace_EmptySlots(ItemStack itemStack, int quantity, TileEntity tileEntity, ArrayList<SearchTileEntity> searchList)
    {
        IInventory inventory = (IInventory)tileEntity;
        SearchTileEntity searchTileEntity = searchList.size() > 0 ? searchList.get(searchList.size() - 1): null;
        
        if(searchTileEntity != null && searchTileEntity.TileEntity != tileEntity)
            searchTileEntity = null;
        
        for(int i = 0; i < inventory.getSizeInventory() && quantity > 0; i++)
        {
            ItemStack invItemStack = inventory.getStackInSlot(i);
            
            if(invItemStack != null)
                continue;
            
            if(searchTileEntity == null)
                searchList.add(searchTileEntity = new SearchTileEntity(tileEntity));
            
            SearchItem searchItem = new SearchItem(i, quantity);

            searchTileEntity.Items.add(searchItem);
            
            quantity -= searchItem.Quantity;
        }
        
        return quantity;
    }
    
    private static Slot getSlot(TileEntity tileEntity)
    {
        IInventory inventory = (IInventory)tileEntity;
        Class<?> cls = tileEntity.getClass();
        
        if(cls == TEChest.class)
            return new SlotChest(inventory, 0, 0, 0).addItemException(ContainerChestTFC.getExceptions());
        
        if(cls == TileEntityChest.class)
            return new Slot(inventory, 0, 0, 0);
        
        if(cls == TELogPile.class)
            return new SlotLogPile(null, inventory, 0, 0, 0);
        
        if(cls == TEIngotPile.class)
            return new SlotIngotPile(inventory, 0, 0, 0);
        
        if(cls == TEBarrel.class)
            return new SlotChest(inventory, 0, 0, 0).setSize(EnumSize.LARGE).addItemException(ContainerChestTFC.getExceptions());

        if(cls == TEVessel.class)
            return new SlotChest(inventory, 0, 0, 0).setSize(EnumSize.MEDIUM).addItemException(ContainerChestTFC.getExceptions());

        return null;
    }
}
