package com.aleksey.merchants.Handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.aleksey.merchants.Containers.ContainerStall;
import com.aleksey.merchants.Containers.ContainerTrussel;
import com.aleksey.merchants.Containers.ContainerWarehouse;
import com.aleksey.merchants.GUI.GuiStall;
import com.aleksey.merchants.GUI.GuiTrussel;
import com.aleksey.merchants.GUI.GuiTrusselCreate;
import com.aleksey.merchants.GUI.GuiWarehouse;
import com.aleksey.merchants.TileEntities.TileEntityStall;
import com.aleksey.merchants.TileEntities.TileEntityWarehouse;

import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
    public static final int GuiOwnerStall = 0;
    public static final int GuiBuyerStall = 1;
    public static final int GuiWarehouse = 2;
    public static final int GuiTrusselCreate = 3;
    public static final int GuiTrussel = 4;
    
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) 
    {
        TileEntity te = world.getTileEntity(x, y, z);

        switch(id)
        {
            case GuiOwnerStall:
                return new ContainerStall(player.inventory, (TileEntityStall)te, true, world, x, y, z);
            case GuiBuyerStall:
                return new ContainerStall(player.inventory, (TileEntityStall)te, false, world, x, y, z);
            case GuiWarehouse:
                return new ContainerWarehouse(player.inventory, (TileEntityWarehouse)te, world, x, y, z);
            case GuiTrusselCreate:
                return null;
            case GuiTrussel:
                return new ContainerTrussel(player.inventory, world, x, y, z);
            default:
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity te;
        
        try
        {
            te = world.getTileEntity(x, y, z);
        }
        catch(Exception e)
        {
            te = null;
        }

        switch(id)
        {
            case GuiOwnerStall:
                return new GuiStall(player.inventory, (TileEntityStall)te, true, world, x, y, z);
            case GuiBuyerStall:
                return new GuiStall(player.inventory, (TileEntityStall)te, false, world, x, y, z);
            case GuiWarehouse:
                return new GuiWarehouse(player.inventory, (TileEntityWarehouse)te, world, x, y, z);
            case GuiTrusselCreate:
                return new GuiTrusselCreate(player.inventory, world);
            case GuiTrussel:
                return new GuiTrussel(player.inventory, world, x, y, z);
            default:
                return null;
        }
    }
}