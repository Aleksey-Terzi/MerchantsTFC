package com.aleksey.merchants;

import com.aleksey.merchants.Core.BlockList;
import com.aleksey.merchants.Render.Blocks.RenderStall;

import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy
{
    public void registerRenderInformation()
    {
        RenderingRegistry.registerBlockHandler(BlockList.StallRenderId = RenderingRegistry.getNextAvailableRenderId(), new RenderStall());
    }

    public boolean isRemote()
    {
        return true;
    }
}