package com.aleksey.merchants.Helpers;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;

import com.aleksey.merchants.Core.Point;
import com.aleksey.merchants.Core.WarehouseBookInfo;
import com.aleksey.merchants.TileEntities.TileEntityWarehouse;
import com.bioxx.tfc.Items.Pottery.ItemPotterySmallVessel;
import com.bioxx.tfc.TileEntities.TEBarrel;
import com.bioxx.tfc.TileEntities.TEChest;
import com.bioxx.tfc.TileEntities.TEIngotPile;
import com.bioxx.tfc.TileEntities.TELogPile;
import com.bioxx.tfc.TileEntities.TEVessel;
import com.bioxx.tfc.api.Interfaces.IFood;

public class WarehouseManager
{
    private static final int _searchContainerRadius = 3;
    private static final int _searchWarehouseDistance = 10;
    
    private static final Class<?>[] _allowedInventories = {
        TileEntityChest.class,
        TEChest.class,
        TELogPile.class,
        TEIngotPile.class,
        TEBarrel.class,
        TEVessel.class,
    };
    
    private ArrayList<Point> _containers;
    private Hashtable<String, Integer> _quantities;
    private ItemStack _goodItemStack;
    private ItemStack _payItemStack;
    private ArrayList<SearchTileEntity> _goodList;
    private ArrayList<SearchTileEntity> _payList;

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
        TradeHelper.confirmTradeGoods(_goodItemStack, _goodList, world);
        
        String goodKey = ItemHelper.getItemKey(_goodItemStack);
        _quantities.put(goodKey, _quantities.get(goodKey) - ItemHelper.getItemStackQuantity(_goodItemStack));        
        _goodList = null;
        
        TradeHelper.confirmTradePays(_payItemStack, _payList, _containers, world);
        
        String payKey = ItemHelper.getItemKey(_payItemStack);
        int currentQuantity = _quantities.containsKey(payKey) ? _quantities.get(payKey): 0;
        _quantities.put(payKey, currentQuantity + ItemHelper.getItemStackQuantity(_payItemStack));
        _payList = null;
    }
    
    public PrepareTradeResult prepareTrade(ItemStack goodStack, ItemStack payStack, World world)
    {
        int goodQuantity = ItemHelper.getItemStackQuantity(goodStack);
        int payQuantity = ItemHelper.getItemStackQuantity(payStack);
        
        if(goodQuantity == 0 || getQuantity(goodStack) < goodQuantity)
            return PrepareTradeResult.NoGoods;
        
        _goodList = new ArrayList<SearchTileEntity>();
        _payList = new ArrayList<SearchTileEntity>();
        
        if(payStack.getItem() instanceof IFood)
        {
            for(int i = 0; i < _containers.size() && payQuantity > 0; i++)
            {
                Point p = _containers.get(i); 
                TileEntity tileEntity = world.getTileEntity(p.X, p.Y, p.Z);
                
                if(tileEntity == null || !(tileEntity instanceof IInventory))
                    continue;
                
                payQuantity -= SearchHelper.searchFreeSpaceInSmallVessels(payStack, payQuantity, tileEntity, _payList);
            }
        }

        for(int i = 0; i < _containers.size() && (goodQuantity > 0 || payQuantity > 0); i++)
        {
            Point p = _containers.get(i); 
            TileEntity tileEntity = world.getTileEntity(p.X, p.Y, p.Z);
            
            if(tileEntity == null || !(tileEntity instanceof IInventory))
                continue;
            
            if(goodQuantity > 0)
                goodQuantity -= SearchHelper.searchItems(goodStack, goodQuantity, tileEntity, _goodList);
            
            if(payQuantity > 0)
                payQuantity -= SearchHelper.searchFreeSpace(payStack, payQuantity, tileEntity, world, _payList);
        }
        
        _goodItemStack = goodStack.copy();
        _payItemStack = payStack.copy();
        
        if(goodQuantity == 0 && payQuantity == 0)
            return PrepareTradeResult.Success;
        
        return goodQuantity > 0 ? PrepareTradeResult.NoGoods: PrepareTradeResult.NoPays;
    }
    
    public boolean existWarehouse(int stallX, int stallY, int stallZ, WarehouseBookInfo info, World world)
    {
        double distance = Math.sqrt(Math.pow(info.X - stallX, 2) + Math.pow(info.Y - stallY, 2) + Math.pow(info.Z - stallZ, 2));
        
        if(distance > _searchWarehouseDistance)
            return false;
        
        TileEntity tileEntity = world.getTileEntity(info.X, info.Y, info.Z);
        
        return tileEntity instanceof TileEntityWarehouse && ((TileEntityWarehouse)tileEntity).getKey() == info.Key;
    }

    public void searchContainers(WarehouseBookInfo info, World world)
    {
        _containers.clear();
        _quantities.clear();
        
        int startX = info.X - _searchContainerRadius;
        int endX = info.X + _searchContainerRadius;
        int startY = info.Y - _searchContainerRadius;
        int endY = info.Y + _searchContainerRadius;
        int startZ = info.Z - _searchContainerRadius;
        int endZ = info.Z + _searchContainerRadius;
        
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
            
            if(itemStack.getItem() instanceof ItemPotterySmallVessel)
            {
                ItemStack[] vesselItemStacks = SmallVesselHelper.getVesselItemStacks(itemStack);
                
                if(vesselItemStacks == null
                        || vesselItemStacks[0] == null && vesselItemStacks[1] == null && vesselItemStacks[2] == null && vesselItemStacks[3] == null
                        )
                {
                    addItemStackQuantity(itemStack);
                }
                else
                {
                    for(int k = 0; k < vesselItemStacks.length; k++)
                    {
                        if(vesselItemStacks[k] != null)
                            addItemStackQuantity(vesselItemStacks[k]);
                    }
                }
            }
            else
                addItemStackQuantity(itemStack);
        }
    }
    
    private void addItemStackQuantity(ItemStack itemStack)
    {
        int quantity = ItemHelper.getItemStackQuantity(itemStack);
        String itemKey = ItemHelper.getItemKey(itemStack);

        if(_quantities.containsKey(itemKey))
            quantity += _quantities.get(itemKey);
        
        _quantities.put(itemKey, quantity);        
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

    private boolean isAllowedInventory(TileEntity tileEntity)
    {
        Class<?> cls = tileEntity.getClass();
        
        for(int i = 0; i < _allowedInventories.length; i++)
        {
            if(cls == _allowedInventories[i])
                return SearchHelper.canSearchItem(tileEntity);
        }
        
        return false;
    }
}