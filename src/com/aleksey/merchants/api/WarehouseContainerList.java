package com.aleksey.merchants.api;

import java.util.ArrayList;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;

public class WarehouseContainerList
{
    private static ArrayList<IWarehouseContainer> _allowedContainers;
    
    private static void init()
    {
        if(_allowedContainers == null)
            _allowedContainers = new ArrayList<IWarehouseContainer>();
    }
    
    public static void addContainer(IWarehouseContainer container)
    {
        init();
        
        _allowedContainers.add(container);
    }
    
    public static IWarehouseContainer getContainer(TileEntity tileEntity)
    {
        if(_allowedContainers == null || tileEntity == null || !(tileEntity instanceof IInventory))
            return null;
        
        for(IWarehouseContainer container : _allowedContainers)
        {
            if(container.isValid(tileEntity))
                return container;
        }
        
        return null;
    }
}
