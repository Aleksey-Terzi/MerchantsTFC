package com.aleksey.merchants.Core;

import net.minecraft.block.Block;

import com.aleksey.merchants.Blocks.Devices.BlockStall;
import com.aleksey.merchants.Blocks.Devices.BlockWarehouse;
import com.aleksey.merchants.ItemBlocks.ItemStall;
import com.aleksey.merchants.ItemBlocks.ItemWarehouse;

import cpw.mods.fml.common.registry.GameRegistry;

public class BlockList
{
    public static int StallRenderId;
    public static int WarehouseRenderId;
    
    public static Block Stall;
    public static Block Warehouse;
    
    public static void registerBlocks()
    {
        GameRegistry.registerBlock(Stall, ItemStall.class, Stall.getUnlocalizedName().substring(5));
        GameRegistry.registerBlock(Warehouse, ItemWarehouse.class, Warehouse.getUnlocalizedName().substring(5));
    }
    
    public static void loadBlocks()
    {
        Stall = new BlockStall().setBlockName("Stall").setHardness(2);
        Warehouse = new BlockWarehouse().setBlockName("Warehouse").setHardness(2);
    }
}
