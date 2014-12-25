package com.aleksey.merchants;

import com.aleksey.merchants.Handlers.GuiHandler;
import com.aleksey.merchants.TileEntities.TileEntityAnvilDie;
import com.aleksey.merchants.TileEntities.TileEntityStall;
import com.aleksey.merchants.TileEntities.TileEntityWarehouse;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy
{
    public void registerRenderInformation()
    {
    }
    
    public void registerTileEntities()
    {
        GameRegistry.registerTileEntity(TileEntityStall.class, "TileEntityStall");
        GameRegistry.registerTileEntity(TileEntityWarehouse.class, "TileEntityWarehouse");
        GameRegistry.registerTileEntity(TileEntityAnvilDie.class, "TileEntityAnvilDie");
    }
    
    public void registerGuiHandler()
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(MerchantsMod.instance, new GuiHandler());
    }

    public boolean isRemote()
    {
        return false;
    }
}