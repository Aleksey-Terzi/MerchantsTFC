package com.aleksey.merchants;

import com.aleksey.merchants.Core.BlockList;
import com.aleksey.merchants.Render.Blocks.RenderAnvilDie;
import com.aleksey.merchants.Render.Blocks.RenderStall;
import com.aleksey.merchants.Render.Blocks.RenderWarehouse;
import com.aleksey.merchants.TESR.TESRStall;
import com.aleksey.merchants.TileEntities.TileEntityStall;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy
{
    public void registerRenderInformation()
    {
        RenderingRegistry.registerBlockHandler(BlockList.StallRenderId = RenderingRegistry.getNextAvailableRenderId(), new RenderStall());
        RenderingRegistry.registerBlockHandler(BlockList.WarehouseRenderId = RenderingRegistry.getNextAvailableRenderId(), new RenderWarehouse());
        RenderingRegistry.registerBlockHandler(BlockList.AnvilDieRenderId = RenderingRegistry.getNextAvailableRenderId(), new RenderAnvilDie());
    }
    
    @Override
    public void registerTileEntities()
    {
        registerCommonTileEntities();
        
        ClientRegistry.registerTileEntity(TileEntityStall.class, "TileEntityStall", new TESRStall());
    }

    public boolean isRemote()
    {
        return true;
    }
}