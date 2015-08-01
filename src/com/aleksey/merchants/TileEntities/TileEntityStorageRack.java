package com.aleksey.merchants.TileEntities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;

import com.aleksey.merchants.Core.ItemList;
import com.bioxx.tfc.Core.TFC_Core;
import com.bioxx.tfc.TileEntities.NetworkTileEntity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityStorageRack extends NetworkTileEntity implements IInventory
{
    private ItemStack[] _storage;
    
    public TileEntityStorageRack()
    {
        _storage = new ItemStack[1];
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
        return bb;
    }

    @Override
    public void closeInventory()
    {
    }

    @Override
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

    @Override
    public int getInventoryStackLimit()
    {
        return 1;
    }

    @Override
    public String getInventoryName()
    {
        return "gui.StorageRack.Title";
    }

    @Override
    public int getSizeInventory()
    {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int i)
    {
        return _storage[i];
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i)
    {
        return _storage[i];
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer)
    {
        return false;
    }

    @Override
    public void openInventory()
    {
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack is)
    {
        _storage[i] = is;
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack)
    {
        return false;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);

        //Items
        NBTTagList itemList = new NBTTagList();

        for (int i = 0; i < _storage.length; i++)
        {
            ItemStack itemStack = _storage[i];
            
            if(itemStack == null)
                itemStack = new ItemStack(ItemList.Flan, 0, 0);//Workaround for synchronization bug
            
            if (itemStack != null)
            {
                NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setByte("Slot", (byte) i);

                itemStack.writeToNBT(itemTag);

                itemList.appendTag(itemTag);
            }
        }

        nbt.setTag("Items", itemList);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        NBTTagList itemList = nbt.getTagList("Items", 10);

        for (int i = 0; i < itemList.tagCount(); i++)
        {
            NBTTagCompound itemTag = itemList.getCompoundTagAt(i);
            byte byte0 = itemTag.getByte("Slot");

            if (byte0 >= 0 && byte0 < _storage.length)
            {
                ItemStack itemStack = ItemStack.loadItemStackFromNBT(itemTag);
                
                if(itemStack.stackSize == 0)
                    itemStack = null;
                
                setInventorySlotContents(byte0, itemStack);
            }
        }
    }

    @Override
    public void handleInitPacket(NBTTagCompound nbt)
    {
        readFromNBT(nbt);
    }

    @Override
    public void handleDataPacket(NBTTagCompound nbt)
    {
        readFromNBT(nbt);
    }

    @Override
    public void createDataNBT(NBTTagCompound nbt)
    {
        writeToNBT(nbt);
    }

    @Override
    public void createInitNBT(NBTTagCompound nbt)
    {
        writeToNBT(nbt);
    }

    @Override
    public void updateEntity()
    {
        TFC_Core.handleItemTicking(this, worldObj, xCoord, yCoord, zCoord);
    }
}