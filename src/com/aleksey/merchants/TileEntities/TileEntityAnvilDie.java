package com.aleksey.merchants.TileEntities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;

import com.aleksey.merchants.Core.ItemList;
import com.aleksey.merchants.Core.WarehouseBookInfo;
import com.bioxx.tfc.TileEntities.NetworkTileEntity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityAnvilDie extends NetworkTileEntity implements IInventory
{
    private static final byte _actionId_Mint = 0;
    
    private ItemStack[] _storage;
    private int _metalWeight;//100 = 1oz
    
    public int getMetalWeight()
    {
        return _metalWeight;
    }
    
    public TileEntityAnvilDie()
    {
        _storage = new ItemStack[5];
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
        return "gui.AnvilDie.Title";
    }

    @Override
    public int getSizeInventory()
    {
        return _storage.length;
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
        if (!ItemStack.areItemStacksEqual(_storage[i], is))
        {
            _storage[i] = is;
        }
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
        
        nbt.setInteger("MetalWeight", _metalWeight);

        NBTTagList itemList = new NBTTagList();

        for (int i = 0; i < _storage.length; i++)
        {
            if (_storage[i] != null)
            {
                NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setByte("Slot", (byte) i);

                _storage[i].writeToNBT(itemTag);

                itemList.appendTag(itemTag);
            }
        }

        nbt.setTag("Items", itemList);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        
        _metalWeight = nbt.getInteger("MetalWeight");

        NBTTagList itemList = nbt.getTagList("Items", 10);

        for (int i = 0; i < itemList.tagCount(); i++)
        {
            NBTTagCompound itemTag = itemList.getCompoundTagAt(i);
            byte byte0 = itemTag.getByte("Slot");

            if (byte0 >= 0 && byte0 < _storage.length)
                setInventorySlotContents(byte0, ItemStack.loadItemStackFromNBT(itemTag));
        }
    }

    @Override
    public void handleInitPacket(NBTTagCompound nbt)
    {
        _metalWeight = nbt.hasKey("MetalWeight") ? nbt.getInteger("MetalWeight"): 0;
        
        this.worldObj.func_147479_m(xCoord, yCoord, zCoord);
    }

    @Override
    public void createInitNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("MetalWeight", _metalWeight);
    }

    @Override
    public void handleDataPacket(NBTTagCompound nbt)
    {
        if (!nbt.hasKey("Action"))
            return;

        byte action = nbt.getByte("Action");

        switch (action)
        {
            case _actionId_Mint:
                actionHandlerMint();
                break;
        }
    }

    @Override
    public void updateEntity()
    {
    }
    
    //Send action to server
    public void actionMint()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setByte("Action", _actionId_Mint);
        this.broadcastPacketInRange(this.createDataPacket(nbt));

        this.worldObj.func_147479_m(xCoord, yCoord, zCoord);
    }

    private void actionHandlerMint()
    {
        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
}
