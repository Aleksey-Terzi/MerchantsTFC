package com.aleksey.merchants.TileEntities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;

import com.aleksey.merchants.Helpers.PrepareTradeResult;
import com.aleksey.merchants.Helpers.WarehouseManager;
import com.bioxx.tfc.TileEntities.NetworkTileEntity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityStall extends NetworkTileEntity implements IInventory
{
    public static final int PriceCount = 5;
    public static final int ItemCount = 2 * PriceCount + 1;

    private static final byte _actionId_ClearPrices = 0;
    private static final byte _actionId_Buy = 1;

    private ItemStack[] _storage;
    private WarehouseManager _warehouse;
    private String _ownerUserName;

    public TileEntityStall()
    {
        _storage = new ItemStack[ItemCount];
        _warehouse = new WarehouseManager();
    }

    public boolean getIsWarehouseSpecified()
    {
        return _ownerUserName != null;
    }

    public int getWarehouseX()
    {
        return this.xCoord;
    }

    public int getWarehouseY()
    {
        return this.yCoord;
    }

    public int getWarehouseZ()
    {
        return this.zCoord;
    }

    public void setWarehouse(String ownerUserName)
    {
        _ownerUserName = ownerUserName;
    }

    public String getOwnerUserName()
    {
        return _ownerUserName;
    }

    public void calculateQuantitiesInWarehouse()
    {
        _warehouse.searchContainers(getWarehouseX(), getWarehouseY(), getWarehouseZ(), this.worldObj);

        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public int getQuantityInWarehouse(ItemStack itemStack)
    {
        return _warehouse.getQuantity(itemStack);
    }

    public int getContainersInWarehouse()
    {
        return _warehouse.getContainers();
    }

    public PrepareTradeResult prepareTrade(ItemStack goodStack, ItemStack payStack)
    {
        return _warehouse.prepareTrade(goodStack, payStack, this.worldObj);
    }

    public void confirmTrade()
    {
        _warehouse.confirmTrade(this.worldObj);
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
        return 64;
    }

    @Override
    public String getInventoryName()
    {
        return "gui.Stall.Title";
    }

    @Override
    public int getSizeInventory()
    {
        return ItemCount;
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

        writeStallToNBT(nbt);

        _warehouse.writeToNBT(nbt);
    }

    public void writeStallToNBT(NBTTagCompound nbt)
    {
        nbt.setString("OwnerUserName", _ownerUserName);

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

        readStallFromNBT(nbt);

        _warehouse.readFromNBT(nbt);
    }

    public void readStallFromNBT(NBTTagCompound nbt)
    {
        _ownerUserName = nbt.hasKey("OwnerUserName") ? nbt.getString("OwnerUserName"): null;

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
        _ownerUserName = nbt.hasKey("OwnerUserName") ? nbt.getString("OwnerUserName"): null;

        _warehouse.readFromNBT(nbt);

        this.worldObj.func_147479_m(xCoord, yCoord, zCoord);
    }

    @Override
    public void createInitNBT(NBTTagCompound nbt)
    {
        if(_ownerUserName != null)
            nbt.setString("OwnerUserName", _ownerUserName);

        _warehouse.writeToNBT(nbt);
    }

    @Override
    public void handleDataPacket(NBTTagCompound nbt)
    {
        if (!nbt.hasKey("Action"))
            return;

        byte action = nbt.getByte("Action");

        switch (action)
        {
            case _actionId_ClearPrices:
                actionHandlerClearPrices();
                break;
            case _actionId_Buy:
                actionHandlerBuy(nbt);
                break;
        }
    }

    @Override
    public void updateEntity()
    {
    }

    //Send action to server
    public void actionClearPrices()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setByte("Action", _actionId_ClearPrices);
        this.broadcastPacketInRange(this.createDataPacket(nbt));

        this.worldObj.func_147479_m(xCoord, yCoord, zCoord);
    }

    private void actionHandlerClearPrices()
    {
        for (int i = 0; i < _storage.length - 1; i++)
            _storage[i] = null;

        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    
    //Send action to client
    public void actionBuy(ItemStack itemStack)
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setByte("Action", _actionId_Buy);
        
        NBTTagCompound itemTag = new NBTTagCompound();
        itemStack.writeToNBT(itemTag);
        
        nbt.setTag("Item", itemTag);
        
        this.broadcastPacketInRange(this.createDataPacket(nbt));

        this.worldObj.func_147479_m(xCoord, yCoord, zCoord);
    }
    
    private void actionHandlerBuy(NBTTagCompound nbt)
    {
        NBTTagCompound itemTag = nbt.getCompoundTag("Item");
        ItemStack itemStack = ItemStack.loadItemStackFromNBT(itemTag);
        
        this.entityplayer.inventory.setItemStack(itemStack);
    }
}