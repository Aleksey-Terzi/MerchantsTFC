package com.aleksey.merchants;

import com.aleksey.merchants.Core.BlockList;
import com.aleksey.merchants.Render.Blocks.RenderStall;
import com.aleksey.merchants.Render.Blocks.RenderWarehouse;

import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy
{
    public void registerRenderInformation()
    {
        RenderingRegistry.registerBlockHandler(BlockList.StallRenderId = RenderingRegistry.getNextAvailableRenderId(), new RenderStall());
        RenderingRegistry.registerBlockHandler(BlockList.WarehouseRenderId = RenderingRegistry.getNextAvailableRenderId(), new RenderWarehouse());
    }

    public boolean isRemote()
    {
        return true;
    }
}