package com.aleksey.merchants.TileEntities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;

import com.aleksey.merchants.Core.CoinInfo;
import com.aleksey.merchants.Core.Constants;
import com.aleksey.merchants.Core.DieInfo;
import com.aleksey.merchants.Core.ItemList;
import com.aleksey.merchants.Helpers.CoinHelper;
import com.aleksey.merchants.Helpers.ItemHelper;
import com.bioxx.tfc.TileEntities.NetworkTileEntity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityAnvilDie extends NetworkTileEntity implements IInventory
{
    public static final int HammerSlot = 0;
    public static final int TrusselSlot = 1;
    public static final int FlanSlot = 2;
    public static final int AnvilDieSlot = 3;
    public static final int CoinSlot = 4;
    
    private static final byte _actionId_Mint = 0;
    private static final int _maxCoinStackSize = 64;
    
    private ItemStack[] _storage;
    private int _metalWeightInHundreds;//100 = 1oz
    private int _metalMeta;
    
    public int getMetalWeightInHundreds()
    {
        return _metalWeightInHundreds;
    }
    
    public int getMetalMeta()
    {
        return _metalMeta;
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
        
        nbt.setInteger("MetalWeight", _metalWeightInHundreds);
        nbt.setInteger("MetalMeta", _metalMeta);

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
        
        _metalWeightInHundreds = nbt.getInteger("MetalWeight");
        _metalMeta = nbt.getInteger("MetalMeta");

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
        _metalWeightInHundreds = nbt.hasKey("MetalWeight") ? nbt.getInteger("MetalWeight"): 0;
        _metalMeta = nbt.hasKey("MetalMeta") ? nbt.getInteger("MetalMeta"): 0;
        
        this.worldObj.func_147479_m(xCoord, yCoord, zCoord);
    }

    @Override
    public void createInitNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("MetalWeight", _metalWeightInHundreds);
        nbt.setInteger("MetalMeta", _metalMeta);
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
        if(!canMint())
            return;

        int coinWeightIndex = CoinHelper.getCoinWeight(_storage[TrusselSlot]);
        int coinWeight = (int)(CoinHelper.getWeightOz(coinWeightIndex) * 100);
        int metalTotalWeight = getMetalTotalWeight();
        int coinQuantity = Math.min(coinWeightIndex, metalTotalWeight / coinWeight);
        
        ItemStack coinStack = _storage[CoinSlot];
        int coinStackSize = coinStack != null ? coinStack.stackSize: 0;
        
        if(coinQuantity + coinStackSize > _maxCoinStackSize)
            coinQuantity = _maxCoinStackSize - coinStackSize;
        
        if(coinStack == null)
            _storage[CoinSlot] = getResultCoin(coinQuantity);
        else
            coinStack.stackSize += coinQuantity;
        
        decrMetalTotalWeight(coinQuantity * coinWeight);
        
        damageHammer();
        
        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    public boolean canMint()
    {
        ItemStack trusselStack = _storage[TrusselSlot];
        
        if(_storage[HammerSlot] == null || trusselStack == null)
            return false;
        
        ItemStack coinStack = _storage[CoinSlot];
        ItemStack resultStack = getResultCoin(1);
        
        if(coinStack != null && (coinStack.stackSize >= _maxCoinStackSize || !ItemHelper.areItemEquals(coinStack, resultStack)))
            return false;
        
        DieInfo trusselInfo = Constants.Dies[trusselStack.getItemDamage()];
        DieInfo anvilDieInfo = Constants.Dies[_storage[AnvilDieSlot].getItemDamage()];
        CoinInfo resultInfo = Constants.Coins[resultStack.getItemDamage()];
        
        if(trusselInfo.Level <= resultInfo.Level || anvilDieInfo.Level <= resultInfo.Level)
            return false;
        
        int coinWeightIndex = CoinHelper.getCoinWeight(trusselStack);
        int coinWeight = (int)(CoinHelper.getWeightOz(coinWeightIndex) * 100);
        
        return coinWeight <= getMetalTotalWeight();
    }
    
    private void decrMetalTotalWeight(int value)
    {
        ItemStack flanStack = _storage[FlanSlot];
        
        if(flanStack != null && flanStack.getItemDamage() != _metalMeta)
        {
            _metalWeightInHundreds = 0;
            _metalMeta = flanStack.getItemDamage();
        }
        
        if(_metalWeightInHundreds >= value)
        {
            _metalWeightInHundreds -= value;
            return;
        }
        
        _metalWeightInHundreds += CoinHelper.MaxFlanWeightInHundreds;
        _metalWeightInHundreds -= value;
        
        _storage[FlanSlot] = null;
    }
    
    private int getMetalTotalWeight()
    {
        ItemStack flanStack = _storage[FlanSlot];
        int totalWeight = flanStack == null || flanStack.getItemDamage() == _metalMeta ? _metalWeightInHundreds: 0;
        
        if(flanStack != null)
            totalWeight += CoinHelper.MaxFlanWeightInHundreds;
        
        return totalWeight;
    }
    
    private ItemStack getResultCoin(int quantity)
    {
        ItemStack flanStack = _storage[FlanSlot];
        int coinMeta = flanStack != null ? flanStack.getItemDamage(): _metalMeta;
        ItemStack coinStack = new ItemStack(ItemList.Coin, quantity, coinMeta);
        
        CoinHelper.copyDie(_storage[TrusselSlot], coinStack);
        
        return coinStack;
    }
    
    private void damageHammer()
    {
        _storage[HammerSlot].setItemDamage(_storage[HammerSlot].getItemDamage() + 1);
        
        if(_storage[HammerSlot].getItemDamage() == _storage[HammerSlot].getMaxDamage())
            _storage[HammerSlot] = null;
    }
}