package com.aleksey.merchants.Core;

import net.minecraft.block.Block;

import com.aleksey.merchants.Blocks.Devices.BlockCustomAnvilDie;
import com.aleksey.merchants.Blocks.Devices.BlockCustomAnvilDie2;
import com.aleksey.merchants.Blocks.Devices.BlockStall;
import com.aleksey.merchants.Blocks.Devices.BlockStorageRack;
import com.aleksey.merchants.Blocks.Devices.BlockWarehouse;
import com.aleksey.merchants.ItemBlocks.ItemStall;
import com.aleksey.merchants.ItemBlocks.ItemStorageRack;
import com.aleksey.merchants.ItemBlocks.ItemWarehouse;
import com.bioxx.tfc.api.Constant.Global;

import cpw.mods.fml.common.registry.GameRegistry;

public class BlockList
{
    public static int StallRenderId;
    public static int WarehouseRenderId;
    public static int AnvilDieRenderId;
    public static int StorageRackRenderId;
    
    public static Block[] Stalls;
    public static Block Warehouse;
    public static Block Warehouse2;
    public static Block StorageRack;
    public static Block StorageRack2;
    public static Block[] AnvilDies;
    
    public static void registerBlocks()
    {
        for(int i = 0; i < Global.WOOD_ALL.length; i++)
            GameRegistry.registerBlock(Stalls[i], ItemStall.class, Stalls[i].getUnlocalizedName().substring(5));
        
        GameRegistry.registerBlock(Warehouse, ItemWarehouse.class, Warehouse.getUnlocalizedName().substring(5));
        GameRegistry.registerBlock(Warehouse2, ItemWarehouse.class, Warehouse2.getUnlocalizedName().substring(5));
        GameRegistry.registerBlock(StorageRack, ItemStorageRack.class, StorageRack.getUnlocalizedName().substring(5));
        GameRegistry.registerBlock(StorageRack2, ItemStorageRack.class, StorageRack2.getUnlocalizedName().substring(5));
        
        for(int i = 0; i < AnvilDies.length; i++)
            GameRegistry.registerBlock(AnvilDies[i], AnvilDies[i].getUnlocalizedName().substring(5));
    }
    
    public static void loadBlocks()
    {
        Stalls = new Block[Global.WOOD_ALL.length];
        
        for(int i = 0; i < Global.WOOD_ALL.length; i++)
        {
            String name = "Stall";
            
            if(i > 0)
                name += "." + Global.WOOD_ALL[i];
                    
            Stalls[i] = new BlockStall(i).setBlockName(name).setHardness(2);
        }
        
        Warehouse = new BlockWarehouse(0).setBlockName("Warehouse").setHardness(2);
        Warehouse2 = new BlockWarehouse(16).setBlockName("Warehouse2").setHardness(2);
        StorageRack = new BlockStorageRack(0).setBlockName("StorageRack").setHardness(2);
        StorageRack2 = new BlockStorageRack(16).setBlockName("StorageRack2").setHardness(2);
        
        AnvilDies = new Block[Constants.Dies.length * 2];
        
        int index = 0;
        
        for(int i = 0; i < Constants.Dies.length; i++)
        {
            DieInfo info = Constants.Dies[i];
            String name = "AnvilDie." + info.DieName;
            
            AnvilDies[index++] = new BlockCustomAnvilDie(info).setBlockName(name);
            AnvilDies[index++] = new BlockCustomAnvilDie2(info).setBlockName(name + "2");
        }
    }
}
