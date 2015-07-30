package com.aleksey.merchants.WarehouseContainers;

import java.util.ArrayList;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.aleksey.merchants.Helpers.ItemHelper;
import com.aleksey.merchants.api.ItemSlot;
import com.aleksey.merchants.api.ItemTileEntity;
import com.aleksey.merchants.api.Point;
import com.bioxx.tfc.Core.Metal.MetalRegistry;
import com.bioxx.tfc.TileEntities.TEIngotPile;
import com.bioxx.tfc.api.TFCBlocks;

public class IngotPileContainer extends Container
{
    @Override
    public boolean isValid(TileEntity tileEntity)
    {
        return tileEntity.getClass() == TEIngotPile.class;
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
        if(!isItemValid(tileEntity, itemStack))
            return 0;
        
        int quantity = requiredQuantity;
        
        IInventory inventory = (IInventory)tileEntity;
        ItemStack invItemStack = inventory.getStackInSlot(0);
        
        if(invItemStack != null && !ItemHelper.areItemEquals(itemStack, invItemStack))
            return 0;
        
        ItemTileEntity itemTileEntity = resultList.size() > 0 ? resultList.get(resultList.size() - 1): null;
        
        if(itemTileEntity != null && itemTileEntity.TileEntity != tileEntity)
            itemTileEntity = null;
        
        int maxStackQuantity = ItemHelper.getItemStackMaxQuantity(itemStack, inventory);
        int invQuantity = invItemStack != null ? ItemHelper.getItemStackQuantity(invItemStack): 0;
        
        int addQuantity = quantity + invQuantity <= maxStackQuantity
                    || tileEntity.yCoord < maxY
                        && world.isAirBlock(tileEntity.xCoord, tileEntity.yCoord + 1, tileEntity.zCoord)
                ? quantity
                : maxStackQuantity - invQuantity;
        
        if(addQuantity > 0)
        {
            if(itemTileEntity == null)
                resultList.add(itemTileEntity = new ItemTileEntity(this, tileEntity));
            
            itemTileEntity.Items.add(new ItemSlot(0, addQuantity));
            
            quantity -= addQuantity;
        }
        
        return requiredQuantity - quantity;
    }
    
    @Override
    public void confirmTradeGoods(
            World world,
            ItemTileEntity goodTileEntity,
            ItemStack goodItemStack
            )
    {
        TEIngotPile ingotPile = (TEIngotPile)goodTileEntity.TileEntity;
        IInventory inventory = (IInventory)ingotPile;
        int quantity = ItemHelper.getItemStackQuantity(goodItemStack);
        
        if (inventory.getStackInSlot(0).stackSize < quantity)
            return;
        
        ingotPile.injectContents(0, -quantity);

        world.notifyBlockOfNeighborChange(ingotPile.xCoord, ingotPile.yCoord + 1, ingotPile.zCoord, TFCBlocks.IngotPile);

        if (inventory.getStackInSlot(0).stackSize < 1)
            world.setBlockToAir(ingotPile.xCoord, ingotPile.yCoord, ingotPile.zCoord);
    }
    
    @Override
    public void confirmTradePays(
            World world,
            ItemTileEntity payTileEntity,
            ItemStack payItemStack,
            ArrayList<Point> newContainers
            )
    {
        TEIngotPile ingotPile = (TEIngotPile)payTileEntity.TileEntity;
        IInventory inventory = (IInventory)ingotPile;
        ItemStack ingotPileStack = inventory.getStackInSlot(0);
        int totalQuantity = payTileEntity.Items.get(0).Quantity;
        
        int currentQuantity = ingotPileStack.stackSize + totalQuantity > inventory.getInventoryStackLimit()
            ? inventory.getInventoryStackLimit() - ingotPileStack.stackSize
            : totalQuantity;
            
        if(currentQuantity > 0)
        {
            ingotPile.injectContents(0, currentQuantity);
            ingotPile.validate();
    
            world.addBlockEvent(ingotPile.xCoord, ingotPile.yCoord, ingotPile.zCoord, TFCBlocks.IngotPile, 0, 0);
            
            totalQuantity -= currentQuantity;
        }
        
        if (totalQuantity > 0)
        {
            world.setBlock(ingotPile.xCoord, ingotPile.yCoord + 1, ingotPile.zCoord, TFCBlocks.IngotPile, 0, 0x2);
            
            Item item = payItemStack.getItem();
            
            ingotPile = (TEIngotPile)world.getTileEntity(ingotPile.xCoord, ingotPile.yCoord + 1, ingotPile.zCoord);
            
            ((IInventory)ingotPile).setInventorySlotContents(0, new ItemStack(item, totalQuantity, 0));
            
            ingotPile.setType(MetalRegistry.instance.getMetalFromItem(item).Name);
            
            world.markBlockForUpdate(ingotPile.xCoord, ingotPile.yCoord, ingotPile.zCoord);
            
            newContainers.add(new Point(ingotPile.xCoord, ingotPile.yCoord, ingotPile.zCoord));
        }
    }
    
    //Helper methods
    
    private static boolean isItemValid(TileEntity tileEntity, ItemStack itemStack)
    {
        Class<?> cls = tileEntity.getClass();
        
        if(cls != TEIngotPile.class)
            return false;
        
        Item item = itemStack.getItem();
        
        for(int i = 0; i < TEIngotPile.INGOTS.length; i++)
        {
            if(item == TEIngotPile.INGOTS[i])
                return true;
        }
        
        return false;
    }
}
