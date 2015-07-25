package com.aleksey.merchants;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.aleksey.merchants.Core.BlockList;
import com.aleksey.merchants.Core.ItemList;
import com.aleksey.merchants.Render.Blocks.RenderAnvilDie;
import com.aleksey.merchants.Render.Blocks.RenderStall;
import com.aleksey.merchants.Render.Blocks.RenderWarehouse;
import com.aleksey.merchants.TESR.TESRStall;
import com.aleksey.merchants.TileEntities.TileEntityStall;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Loader;

public class ClientProxy extends CommonProxy
{
    @Override
    public boolean isRemote()
    {
        return true;
    }

    @Override
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
    
	@Override
	public void hideNEIItems()
	{
		String mod = "NotEnoughItems";
		
		if (Loader.isModLoaded(mod))
		{
			codechicken.nei.api.API.hideItem(new ItemStack(ItemList.Coin, 1, OreDictionary.WILDCARD_VALUE));
			codechicken.nei.api.API.hideItem(new ItemStack(ItemList.WarehouseBook));
			
			for(int i = 0; i < BlockList.AnvilDies.length; i++)
				codechicken.nei.api.API.hideItem(new ItemStack(BlockList.AnvilDies[i], 1, OreDictionary.WILDCARD_VALUE));
		}
	}
}