package com.aleksey.merchants.WAILA;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.aleksey.merchants.TileEntities.TileEntityStall;

public class WAILAData implements IWailaDataProvider
{
	public static void callbackRegister(IWailaRegistrar reg)
	{
		reg.registerHeadProvider(new WAILAData(), TileEntityStall.class);
		reg.registerBodyProvider(new WAILAData(), TileEntityStall.class);
		reg.registerNBTProvider(new WAILAData(), TileEntityStall.class);
	}
	
	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return null;
	}
	
	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		TileEntity tileEntity = accessor.getTileEntity();

		if (accessor.getTileEntity() instanceof TileEntityStall)
			currenttip = stallHead(itemStack, currenttip, accessor, config);

		return currenttip;
	}
	
	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return currenttip;
	}
	
	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z)
	{
		if (te != null)
			te.writeToNBT(tag);

		return tag;
	}
	
	// Heads
	
	private List<String> stallHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		String head = currenttip.get(0);
		NBTTagCompound tag = accessor.getNBTData();
		String ownerUserName = TileEntityStall.readOwnerUserNameFromNBT(tag);

		if (ownerUserName != null)
		{
			head += " (" + ownerUserName + ")";
			currenttip.set(0, head);
		}
		
		return currenttip;
	}
}
