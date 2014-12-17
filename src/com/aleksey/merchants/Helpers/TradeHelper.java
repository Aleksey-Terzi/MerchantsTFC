package com.aleksey.merchants.Helpers;

import java.util.ArrayList;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.aleksey.merchants.Core.Point;
import com.bioxx.tfc.TFCBlocks;
import com.bioxx.tfc.Core.Metal.MetalRegistry;
import com.bioxx.tfc.Items.Pottery.ItemPotterySmallVessel;
import com.bioxx.tfc.TileEntities.TEIngotPile;
import com.bioxx.tfc.TileEntities.TELogPile;

public class TradeHelper
{
    public static void confirmTradeGoods(ItemStack goodItemStack, ArrayList<SearchTileEntity> goodList, World world)
    {
        boolean isGoodVessel = goodItemStack.getItem() instanceof ItemPotterySmallVessel;
        
        for(int i = 0; i < goodList.size(); i++)
        {
            SearchTileEntity goodTileEntity = goodList.get(i);
            
            if(goodTileEntity.TileEntity instanceof TEIngotPile)
            {
                confirmTradeGoods_Ingot(goodItemStack, goodTileEntity, world);
                continue;
            }

            IInventory inventory = (IInventory)goodTileEntity.TileEntity;
            
            openInventory(goodTileEntity.TileEntity);
            
            for(int k = 0; k < goodTileEntity.Items.size(); k++)
            {
                SearchItem goodItem = goodTileEntity.Items.get(k);
                ItemStack itemStack = inventory.getStackInSlot(goodItem.SlotIndex);

                if(!isGoodVessel && itemStack.getItem() instanceof ItemPotterySmallVessel)
                    SmallVesselHelper.decreaseItemStackQuantity(goodItem.Quantity, goodItemStack, itemStack);
                else
                {
                    ItemHelper.increaseStackQuantity(itemStack, -goodItem.Quantity);
                    
                    if(itemStack.stackSize == 0)
                        inventory.setInventorySlotContents(goodItem.SlotIndex, (ItemStack)null);
                }
            }
            
            closeInventory(goodTileEntity.TileEntity, world);
        }
    }
    
    private static void confirmTradeGoods_Ingot(ItemStack goodItemStack, SearchTileEntity goodTileEntity, World world)
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

        world.markBlockForUpdate(ingotPile.xCoord, ingotPile.yCoord, ingotPile.zCoord);
    }
    
    public static void confirmTradePays(ItemStack payItemStack, ArrayList<SearchTileEntity> payList, ArrayList<Point> containers, World world)
    {
        boolean isPayVessel = payItemStack.getItem() instanceof ItemPotterySmallVessel;
        
        for(int i = 0; i < payList.size(); i++)
        {
            SearchTileEntity payTileEntity = payList.get(i);
            
            if(payTileEntity.TileEntity instanceof TEIngotPile)
            {
                confirmTradePays_Ingot(payItemStack, payTileEntity, containers, world);
                continue;
            }
            
            IInventory inventory = (IInventory)payTileEntity.TileEntity;
            
            openInventory(payTileEntity.TileEntity);
            
            for(int k = 0; k < payTileEntity.Items.size(); k++)
            {
                SearchItem payItem = payTileEntity.Items.get(k);
                ItemStack invItemStack = inventory.getStackInSlot(payItem.SlotIndex);
                
                if(invItemStack == null)
                {
                    invItemStack = payItemStack.copy();
                    
                    ItemHelper.setStackQuantity(invItemStack, payItem.Quantity);
                    
                    inventory.setInventorySlotContents(payItem.SlotIndex, invItemStack);
                }
                else if(!isPayVessel && invItemStack.getItem() instanceof ItemPotterySmallVessel)
                    SmallVesselHelper.increaseItemStackQuantity(payItem.Quantity, payItemStack, invItemStack);
                else
                    ItemHelper.increaseStackQuantity(invItemStack, payItem.Quantity);
            }
            
            closeInventory(payTileEntity.TileEntity, world);
        }
    }
    
    private static void confirmTradePays_Ingot(ItemStack payItemStack, SearchTileEntity payTileEntity, ArrayList<Point> containers, World world)
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
            world.markBlockForUpdate(ingotPile.xCoord, ingotPile.yCoord, ingotPile.zCoord);
            
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
            
            containers.add(new Point(ingotPile.xCoord, ingotPile.yCoord, ingotPile.zCoord));
        }
    }
    
    private static void openInventory(TileEntity tileEntity)
    {
        IInventory inventory = (IInventory)tileEntity;
        Class<?> cls = tileEntity.getClass();
        
        if(cls == TELogPile.class)
            inventory.openInventory();
    }
    
    private static void closeInventory(TileEntity tileEntity, World world)
    {
        IInventory inventory = (IInventory)tileEntity;
        Class<?> cls = tileEntity.getClass();

        if(cls == TELogPile.class)
        {
            inventory.closeInventory();
        }
        
        world.markBlockForUpdate(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
    }
}
