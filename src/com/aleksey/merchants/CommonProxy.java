package com.aleksey.merchants;

import com.aleksey.merchants.Handlers.ServerTickHandler;
import com.aleksey.merchants.Handlers.GuiHandler;
import com.aleksey.merchants.TileEntities.TileEntityAnvilDie;
import com.aleksey.merchants.TileEntities.TileEntityStall;
import com.aleksey.merchants.TileEntities.TileEntityWarehouse;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy
{
    public void registerRenderInformation()
    {
    }
    
    public void registerTileEntities()
    {
        registerServerTileEntities();
        registerCommonTileEntities();
    }
    
    protected void registerServerTileEntities()
    {
        GameRegistry.registerTileEntity(TileEntityStall.class, "TileEntityStall");
    }
    
    protected void registerCommonTileEntities()
    {
        GameRegistry.registerTileEntity(TileEntityWarehouse.class, "TileEntityWarehouse");
        GameRegistry.registerTileEntity(TileEntityAnvilDie.class, "TileEntityAnvilDie");
    }

    public void registerGuiHandler()
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(MerchantsMod.instance, new GuiHandler());
    }
        
    public void registerTickHandler()
    {
        FMLCommonHandler.instance().bus().register(new ServerTickHandler());
    }

    public boolean isRemote()
    {
        return false;
    }
}