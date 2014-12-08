package com.aleksey.merchants.Helpers;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;

import com.aleksey.merchants.Containers.Slots.SlotIngotPile;
import com.aleksey.merchants.Core.Point;
import com.bioxx.tfc.TFCBlocks;
import com.bioxx.tfc.Containers.ContainerChestTFC;
import com.bioxx.tfc.Containers.Slots.SlotChest;
import com.bioxx.tfc.Containers.Slots.SlotLogPile;
import com.bioxx.tfc.TileEntities.TEChest;
import com.bioxx.tfc.TileEntities.TEIngotPile;
import com.bioxx.tfc.TileEntities.TELogPile;

public class WarehouseManager
{
    private class PreparedItem
    {
        public int SlotIndex;
        public int Quantity;
        
        public PreparedItem(int slotIndex, int quantity)
        {
            SlotIndex = slotIndex;
            Quantity = quantity;
        }
    }
    
    private class PreparedGood
    {
        public TileEntity TileEntity;
        public ArrayList<PreparedItem> Items;
        
        public PreparedGood(TileEntity tileEntity)
        {
            TileEntity = tileEntity;
            Items = new ArrayList<PreparedItem>();
        }
    }
    
    private static final int _searchRadius = 5;
    
    private static final Class<?>[] _allowedInventories = {
        TileEntityChest.class,
        TEChest.class,
        TELogPile.class,
        //TEIngotPile.class,
    };
    
    private ArrayList<Point> _containers;
    private Hashtable<String, Integer> _quantities;
    private ItemStack _preparedGoodItem;
    private ItemStack _preparedPayItem;
    private ArrayList<PreparedGood> _preparedGoods;
    private ArrayList<PreparedGood> _preparedPays;

    public WarehouseManager()
    {
        _containers = new ArrayList<Point>();
        _quantities = new Hashtable<String, Integer>(); 
    }
    
    public int getContainers()
    {
        return _containers.size();
    }
    
    public int getQuantity(ItemStack itemStack)
    {
        String itemKey = ItemHelper.getItemKey(itemStack);
        
        return _quantities.containsKey(itemKey) ? _quantities.get(itemKey): 0;
    }
    
    public void confirmTrade(World world)
    {
        confirmTradeGoods(world);
        confirmTradePays();
    }
    
    private void confirmTradeGoods(World world)
    {
        for(int i = 0; i < _preparedGoods.size(); i++)
        {
            PreparedGood preparedGood = _preparedGoods.get(i);
            IInventory inventory = (IInventory)preparedGood.TileEntity;
            
            openInventory(preparedGood.TileEntity);
            
            for(int k = 0; k < preparedGood.Items.size(); k++)
            {
                PreparedItem preparedItem = preparedGood.Items.get(k);
                ItemStack itemStack = inventory.getStackInSlot(preparedItem.SlotIndex);
                
                ItemHelper.increaseStackQuantity(itemStack, -preparedItem.Quantity);
                
                if(itemStack.stackSize == 0)
                    inventory.setInventorySlotContents(preparedItem.SlotIndex, (ItemStack)null);
                
                inventory.markDirty();
            }
            
            closeInventory(preparedGood.TileEntity, world);
        }
        
        String key = ItemHelper.getItemKey(_preparedGoodItem);

        _quantities.put(key, _quantities.get(key) - ItemHelper.getItemStackQuantity(_preparedGoodItem));
        
        _preparedGoods = null;
    }
    
    private void confirmTradePays()
    {
        for(int i = 0; i < _preparedPays.size(); i++)
        {
            PreparedGood preparedPay = _preparedPays.get(i);
            IInventory inventory = (IInventory)preparedPay.TileEntity;
            
            for(int k = 0; k < preparedPay.Items.size(); k++)
            {
                PreparedItem preparedItem = preparedPay.Items.get(k);
                ItemStack invItemStack = inventory.getStackInSlot(preparedItem.SlotIndex);
                
                if(invItemStack == null)
                {
                    invItemStack = _preparedPayItem.copy();
                    
                    ItemHelper.setStackQuantity(invItemStack, preparedItem.Quantity);
                    
                    inventory.setInventorySlotContents(preparedItem.SlotIndex, invItemStack);
                }
                else
                    ItemHelper.increaseStackQuantity(invItemStack, preparedItem.Quantity);
                
                inventory.markDirty();
            }
        }
        
        String key = ItemHelper.getItemKey(_preparedPayItem);
        int currentQuantity = _quantities.containsKey(key) ? _quantities.get(key): 0;

        _quantities.put(key, currentQuantity + ItemHelper.getItemStackQuantity(_preparedPayItem));
        
        _preparedPays = null;
    }
    
    public PrepareTradeResult prepareTrade(ItemStack goodStack, ItemStack payStack, World world)
    {
        int goodQuantity = ItemHelper.getItemStackQuantity(goodStack);
        int payQuantity = ItemHelper.getItemStackQuantity(payStack);
        
        if(goodQuantity == 0 || getQuantity(goodStack) < goodQuantity)
            return PrepareTradeResult.NoGoods;
        
        _preparedGoods = new ArrayList<PreparedGood>();
        _preparedPays = new ArrayList<PreparedGood>();

        for(int i = 0; i < _containers.size() && (goodQuantity > 0 || payQuantity > 0); i++)
        {
            Point p = _containers.get(i); 
            TileEntity tileEntity = world.getTileEntity(p.X, p.Y, p.Z);
            
            if(tileEntity == null || !(tileEntity instanceof IInventory))
                continue;
            
            goodQuantity -= searchGoods(goodStack, goodQuantity, tileEntity);
            payQuantity -= searchPays(payStack, payQuantity, tileEntity);
        }
        
        _preparedGoodItem = goodStack.copy();
        _preparedPayItem = payStack.copy();
        
        if(goodQuantity == 0 && payQuantity == 0)
            return PrepareTradeResult.Success;
        
        return goodQuantity > 0 ? PrepareTradeResult.NoGoods: PrepareTradeResult.NoPays;
    }
    
    private int searchGoods(ItemStack itemStack, int requiredQuantity, TileEntity tileEntity)
    {
        IInventory inventory = (IInventory)tileEntity;
        PreparedGood preparedGood = null;
        int quantity = requiredQuantity;
        
        for(int i = 0; i < inventory.getSizeInventory() && quantity > 0; i++)
        {
            ItemStack invItemStack = inventory.getStackInSlot(i);
            
            if(invItemStack == null || !ItemHelper.areItemEquals(itemStack, invItemStack))
                continue;
            
            int invQuantity = ItemHelper.getItemStackQuantity(invItemStack);
            
            if(invQuantity == 0)
                continue;
            
            if(preparedGood == null)
                _preparedGoods.add(preparedGood = new PreparedGood(tileEntity));
            
            PreparedItem preparedItem = new PreparedItem(i, invQuantity < quantity ? invQuantity: quantity);
            
            preparedGood.Items.add(preparedItem);
            
            quantity -= preparedItem.Quantity;
        }
        
        return requiredQuantity - quantity;
    }
    
    private int searchPays(ItemStack itemStack, int requiredQuantity, TileEntity tileEntity)
    {
        Slot slot = getSlot(tileEntity);
        
        if(!slot.isItemValid(itemStack))
            return 0;
        
        IInventory inventory = (IInventory)tileEntity;
        PreparedGood preparedPay = null;
        int quantity = requiredQuantity;
        int maxStackQuantity = ItemHelper.getItemStackMaxQuantity(itemStack, inventory);
        
        //Add to non-empty slots
        for(int i = 0; i < inventory.getSizeInventory() && quantity > 0; i++)
        {
            ItemStack invItemStack = inventory.getStackInSlot(i);
            
            if(invItemStack == null || !ItemHelper.areItemEquals(itemStack, invItemStack))
                continue;
            
            int invQuantity = ItemHelper.getItemStackQuantity(invItemStack);
            
            if(invQuantity >= maxStackQuantity)
                continue;
            
            if(preparedPay == null)
                _preparedPays.add(preparedPay = new PreparedGood(tileEntity));
            
            PreparedItem preparedItem = new PreparedItem(i, maxStackQuantity - invQuantity);
            
            if(preparedItem.Quantity > quantity)
                preparedItem.Quantity = quantity;                

            preparedPay.Items.add(preparedItem);
            
            quantity -= preparedItem.Quantity;
        }

        //Add to empty slots
        for(int i = 0; i < inventory.getSizeInventory() && quantity > 0; i++)
        {
            ItemStack invItemStack = inventory.getStackInSlot(i);
            
            if(invItemStack != null)
                continue;
            
            if(preparedPay == null)
                _preparedPays.add(preparedPay = new PreparedGood(tileEntity));
            
            PreparedItem preparedItem = new PreparedItem(i, quantity);

            preparedPay.Items.add(preparedItem);
            
            quantity -= preparedItem.Quantity;
        }
        
        return requiredQuantity - quantity;
    }

    public void searchContainers(int warehouseX, int warehouseY, int warehouseZ, World world)
    {
        _containers.clear();
        _quantities.clear();
        
        int startX = warehouseX - _searchRadius;
        int endX = warehouseX + _searchRadius;
        int startY = warehouseY - _searchRadius;
        int endY = warehouseY + _searchRadius;
        int startZ = warehouseZ - _searchRadius;
        int endZ = warehouseZ + _searchRadius;
        
        for(int x = startX; x <= endX; x++)
        {
            for(int y = startY; y <= endY; y++)
            {
                for(int z = startZ; z <= endZ; z++)
                {
                    TileEntity tileEntity = world.getTileEntity(x, y, z);
                    
                    if(tileEntity != null && isAllowedInventory(tileEntity))
                    {
                        _containers.add(new Point(x, y, z));
                        
                        calculateQuantities((IInventory)tileEntity);
                    }
                }
            }
        }
    }

    private void calculateQuantities(IInventory inventory)
    {
        for(int i = 0; i < inventory.getSizeInventory(); i++)
        {
            ItemStack itemStack = inventory.getStackInSlot(i);
            
            if(itemStack == null)
                continue;

            int quantity = ItemHelper.getItemStackQuantity(itemStack);
            String itemKey = ItemHelper.getItemKey(itemStack);

            if(_quantities.containsKey(itemKey))
                quantity += _quantities.get(itemKey);
            
            _quantities.put(itemKey, quantity);
        }
    }
    
    public void writeToNBT(NBTTagCompound nbt)
    {
        NBTTagList containerList = new NBTTagList();
        
        for(int i = 0; i < _containers.size(); i++)
        {
            Point p = _containers.get(i); 
            
            NBTTagCompound pointTag = new NBTTagCompound();
            pointTag.setInteger("X", p.X);
            pointTag.setInteger("Y", p.Y);
            pointTag.setInteger("Z", p.Z);
            
            containerList.appendTag(pointTag);
        }
        
        nbt.setTag("Containers", containerList);
                
        NBTTagList quantityList = new NBTTagList();
        Iterator<Entry<String, Integer>> quantityIterator = _quantities.entrySet().iterator();
        
        while(quantityIterator.hasNext())
        {
            Entry<String, Integer> qty = quantityIterator.next();
            
            NBTTagCompound qtyTag = new NBTTagCompound();
            qtyTag.setString("Key", qty.getKey());
            qtyTag.setInteger("Value", qty.getValue());
            
            quantityList.appendTag(qtyTag);
        }
        
        nbt.setTag("Quantities", quantityList);
    }

    public void readFromNBT(NBTTagCompound nbt)
    {
        _containers.clear();
        _quantities.clear();
        
        if(nbt.hasKey("Containers"))
        {
            NBTTagList containerList = nbt.getTagList("Containers", 10);
            
            for(int i = 0; i < containerList.tagCount(); i++)
            {
                NBTTagCompound containerTag = containerList.getCompoundTagAt(i);
                Point p = new Point(containerTag.getInteger("X"), containerTag.getInteger("Y"), containerTag.getInteger("Z"));
                
                _containers.add(p);
            }
        }
        
        if(nbt.hasKey("Quantities"))
        {
            NBTTagList quantityList = nbt.getTagList("Quantities", 10);
            
            for(int i = 0; i < quantityList.tagCount(); i++)
            {
                NBTTagCompound qtyTag = quantityList.getCompoundTagAt(i);
                
                _quantities.put(qtyTag.getString("Key"), qtyTag.getInteger("Value"));
            }
        }
    }
    
    private void openInventory(TileEntity tileEntity)
    {
        IInventory inventory = (IInventory)tileEntity;
        Class<?> cls = tileEntity.getClass();
        
        if(cls == TELogPile.class)
            inventory.openInventory();
    }
    
    private void closeInventory(TileEntity tileEntity, World world)
    {
        IInventory inventory = (IInventory)tileEntity;
        Class<?> cls = tileEntity.getClass();

        if(cls == TELogPile.class)
        {
            inventory.closeInventory();
        }
        else if(cls == TEIngotPile.class)
        {
            ItemStack ingotPileStack = inventory.getStackInSlot(0); 
            
            if (ingotPileStack == null || ingotPileStack.stackSize < 1)
                world.setBlockToAir(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
            else
            {
                ((TEIngotPile)tileEntity).updateNeighbours();
                tileEntity.validate();
                world.addBlockEvent(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, TFCBlocks.IngotPile, 0, 0);
            }
        }
        
        world.markBlockForUpdate(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
    }
    
    private Slot getSlot(TileEntity tileEntity)
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

        return null;
    }

    private boolean isAllowedInventory(TileEntity tileEntity)
    {
        Class<?> cls = tileEntity.getClass();
        
        for(int i = 0; i < _allowedInventories.length; i++)
        {
            if(cls == _allowedInventories[i])
                return true;
        }
        
        return false;
    }
}