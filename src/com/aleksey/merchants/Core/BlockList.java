package com.aleksey.merchants.Core;

import net.minecraft.block.Block;

import com.aleksey.merchants.Blocks.Devices.BlockStall;
import com.aleksey.merchants.ItemBlocks.ItemStall;

import cpw.mods.fml.common.registry.GameRegistry;

public class BlockList
{
    public static int StallRenderId;
    
    public static Block Stall;
    
    public static void registerBlocks()
    {
        GameRegistry.registerBlock(Stall, ItemStall.class, Stall.getUnlocalizedName().substring(5));
    }
    
    public static void loadBlocks()
    {
        Stall = new BlockStall().setBlockName("Stall").setHardness(2);
    }
}
