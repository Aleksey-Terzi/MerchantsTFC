package com.aleksey.merchants.Inventories;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class TrusselInventory implements IInventory
{
    private ItemStack[] _storage;
    
    public TrusselInventory()
    {
        _storage = new ItemStack[2];
    }
    
    public int getSizeInventory()
    {
        return _storage.length;
    }

    public ItemStack getStackInSlot(int i)
    {
        return _storage[i];
    }

    public ItemStack decrStackSize(int i, int j)
    {
        if (_storage[i] != null)
        {
            if (_storage[i].stackSize <= j)
            {
                ItemStack is = _storage[i];
                _storage[i] = null;
                return is;
            }
            
            ItemStack isSplit = _storage[i].splitStack(j);
            
            if (_storage[i].stackSize == 0)
                _storage[i] = null;
            
            return isSplit;
        }
        else
        {
            return null;
        }
    }

    public ItemStack getStackInSlotOnClosing(int i)
    {
        return _storage[i];
    }

    public void setInventorySlotContents(int i, ItemStack is)
    {
        if (!ItemStack.areItemStacksEqual(_storage[i], is))
        {
            _storage[i] = is;
        }
    }

    public String getInventoryName()
    {
        return null;
    }

    public boolean hasCustomInventoryName()
    {
        return false;
    }

    public int getInventoryStackLimit()
    {
        return 1;
    }

    public void markDirty()
    {
        
    }

    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return true;
    }

    public void openInventory()
    {
        
    }

    public void closeInventory()
    {
        
    }

    public boolean isItemValidForSlot(int i, ItemStack is)
    {
        return false;
    }
}
