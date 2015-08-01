package com.aleksey.merchants;

import com.aleksey.merchants.Handlers.GuiHandler;
import com.aleksey.merchants.Handlers.ServerTickHandler;
import com.aleksey.merchants.TileEntities.TileEntityAnvilDie;
import com.aleksey.merchants.TileEntities.TileEntityStall;
import com.aleksey.merchants.TileEntities.TileEntityStorageRack;
import com.aleksey.merchants.TileEntities.TileEntityWarehouse;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy
{
    public boolean isRemote()
    {
        return false;
    }

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
        GameRegistry.registerTileEntity(TileEntityStorageRack.class, "TileEntityStorageRack");
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
    
	public void registerWailaClasses()
	{
		FMLInterModComms.sendMessage("Waila", "register", "com.aleksey.merchants.WAILA.WAILAData.callbackRegister");
	}
	
	public void hideNEIItems()
	{
	}
}