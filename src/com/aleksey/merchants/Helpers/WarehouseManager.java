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
import net.minecraft.world.World;

import com.aleksey.merchants.Core.WarehouseBookInfo;
import com.aleksey.merchants.TileEntities.TileEntityWarehouse;
import com.aleksey.merchants.WarehouseContainers.BarrelContainer;
import com.aleksey.merchants.WarehouseContainers.ChestContainer;
import com.aleksey.merchants.WarehouseContainers.IngotPileContainer;
import com.aleksey.merchants.WarehouseContainers.LogPileContainer;
import com.aleksey.merchants.WarehouseContainers.ToolRackContainer;
import com.aleksey.merchants.api.IWarehouseContainer;
import com.aleksey.merchants.api.ItemTileEntity;
import com.aleksey.merchants.api.Point;
import com.bioxx.tfc.Items.Pottery.ItemPotterySmallVessel;
import com.bioxx.tfc.api.Interfaces.IFood;

public class WarehouseManager
{
    private static final int _searchContainerRadius = 3;
    private static final int _searchWarehouseDistance = 10;

    private static ArrayList<IWarehouseContainer> _allowedContainers;
    
    private ArrayList<Point> _containerLocations;
    private Hashtable<String, Integer> _quantities;
    private ItemStack _goodItemStack;
    private ItemStack _payItemStack;
    private ArrayList<ItemTileEntity> _goodList;
    private ArrayList<ItemTileEntity> _payList;
    
    public static void init()
    {
        if(_allowedContainers != null)
            return;
        
        _allowedContainers = new ArrayList<IWarehouseContainer>();
        _allowedContainers.add(new ChestContainer());
        _allowedContainers.add(new LogPileContainer());
        _allowedContainers.add(new IngotPileContainer());
        _allowedContainers.add(new ToolRackContainer());
        _allowedContainers.add(new BarrelContainer());
    }

    public WarehouseManager()
    {
        _containerLocations = new ArrayList<Point>();
        _quantities = new Hashtable<String, Integer>(); 
    }
    
    public int getContainers()
    {
        return _containerLocations.size();
    }
    
    public int getQuantity(ItemStack itemStack)
    {
        String itemKey = ItemHelper.getItemKey(itemStack);
        
        return _quantities.containsKey(itemKey) ? _quantities.get(itemKey): 0;
    }
    
    public void confirmTrade(World world)
    {
        confirmTradeGoods(world);
        
        String goodKey = ItemHelper.getItemKey(_goodItemStack);
        _quantities.put(goodKey, _quantities.get(goodKey) - ItemHelper.getItemStackQuantity(_goodItemStack));        
        _goodList = null;
        
        confirmTradePays(world);
        
        String payKey = ItemHelper.getItemKey(_payItemStack);
        int currentQuantity = _quantities.containsKey(payKey) ? _quantities.get(payKey): 0;
        _quantities.put(payKey, currentQuantity + ItemHelper.getItemStackQuantity(_payItemStack));
        _payList = null;
    }
    
    private void confirmTradeGoods(World world)
    {
        for(int i = 0; i < _goodList.size(); i++)
        {
            ItemTileEntity goodTileEntity = _goodList.get(i);
            
            goodTileEntity.Container.confirmTradeGoods(world, goodTileEntity, _goodItemStack);
            
            world.markBlockForUpdate(goodTileEntity.TileEntity.xCoord, goodTileEntity.TileEntity.yCoord, goodTileEntity.TileEntity.zCoord);
        }
    }
    
    private void confirmTradePays(World world)
    {
        for(int i = 0; i < _payList.size(); i++)
        {
            ItemTileEntity payTileEntity = _payList.get(i);
            
            payTileEntity.Container.confirmTradePays(world, payTileEntity, _payItemStack, _containerLocations);
            
            world.markBlockForUpdate(payTileEntity.TileEntity.xCoord, payTileEntity.TileEntity.yCoord, payTileEntity.TileEntity.zCoord);
        }
    }
    
    public PrepareTradeResult prepareTrade(ItemStack goodStack, ItemStack payStack, WarehouseBookInfo info, World world)
    {
        int goodQuantity = ItemHelper.getItemStackQuantity(goodStack);
        int payQuantity = ItemHelper.getItemStackQuantity(payStack);
        
        if(goodQuantity == 0 || getQuantity(goodStack) < goodQuantity)
            return PrepareTradeResult.NoGoods;
        
        _goodList = new ArrayList<ItemTileEntity>();
        _payList = new ArrayList<ItemTileEntity>();
        
        if(payStack.getItem() instanceof IFood)
        {
            for(int i = 0; i < _containerLocations.size() && payQuantity > 0; i++)
            {
                Point p = _containerLocations.get(i); 
                TileEntity tileEntity = world.getTileEntity(p.X, p.Y, p.Z);
                IWarehouseContainer container = getContainer(tileEntity);
                
                if(container != null)
                    payQuantity -= container.searchFreeSpaceInSmallVessels(tileEntity, payStack, payQuantity, _payList);
            }
        }
        
        int extendLimitY = info.Y + _searchContainerRadius;

        for(int i = 0; i < _containerLocations.size() && (goodQuantity > 0 || payQuantity > 0); i++)
        {
            Point p = _containerLocations.get(i); 
            TileEntity tileEntity = world.getTileEntity(p.X, p.Y, p.Z);
            IWarehouseContainer container = getContainer(tileEntity);
            
            if(container == null)
                continue;
            
            if(goodQuantity > 0)
                goodQuantity -= container.searchItems(tileEntity, goodStack, goodQuantity, _goodList);
            
            if(payQuantity > 0)
                payQuantity -= container.searchFreeSpace(world, tileEntity, payStack, payQuantity, extendLimitY, _payList);
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

    public void searchContainerLocations(WarehouseBookInfo info, World world)
    {
        _containerLocations.clear();
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
                    
                    if(getContainer(tileEntity) != null)
                    {
                        _containerLocations.add(new Point(x, y, z));
                        
                        calculateQuantities((IInventory)tileEntity);
                    }
                }
            }
        }
    }
    
    private static IWarehouseContainer getContainer(TileEntity tileEntity)
    {
        if(tileEntity == null || !(tileEntity instanceof IInventory))
            return null;
        
        for(IWarehouseContainer container : _allowedContainers)
        {
            if(container.isValid(tileEntity))
                return container;
        }
        
        return null;
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
        
        for(int i = 0; i < _containerLocations.size(); i++)
        {
            Point p = _containerLocations.get(i); 
            
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
        _containerLocations.clear();
        _quantities.clear();
        
        if(nbt.hasKey("Containers"))
        {
            NBTTagList containerList = nbt.getTagList("Containers", 10);
            
            for(int i = 0; i < containerList.tagCount(); i++)
            {
                NBTTagCompound containerTag = containerList.getCompoundTagAt(i);
                Point p = new Point(containerTag.getInteger("X"), containerTag.getInteger("Y"), containerTag.getInteger("Z"));
                
                _containerLocations.add(p);
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
}
