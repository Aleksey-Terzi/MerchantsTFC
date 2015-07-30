package com.aleksey.merchants.WarehouseContainers;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import com.bioxx.tfc.Containers.ContainerChestTFC;
import com.bioxx.tfc.Containers.Slots.SlotChest;
import com.bioxx.tfc.TileEntities.TEBarrel;
import com.bioxx.tfc.TileEntities.TEVessel;
import com.bioxx.tfc.api.Food;
import com.bioxx.tfc.api.Enums.EnumSize;
import com.bioxx.tfc.api.Interfaces.IFood;

public class BarrelContainer extends ChestContainer
{
    @Override
    public boolean isValid(TileEntity tileEntity)
    {
        Class<?> cls = tileEntity.getClass(); 
        
        return cls == TEBarrel.class
                || cls == TEVessel.class;
    }
    
    protected boolean canSearchItem(TileEntity tileEntity)
    {
        TEBarrel barrel = (TEBarrel)tileEntity;
        
        if(!barrel.getSealed())
            return true;
        
        ItemStack itemStack = barrel.getInputStack();
        
        return barrel.getFluidLevel() > 0
                && itemStack != null
                && (itemStack.getItem() instanceof IFood)
                && Food.isPickled(itemStack);
    }
    
    protected boolean canSearchFreeSpace(TileEntity tileEntity)
    {
        TEBarrel barrel = (TEBarrel)tileEntity;
        
        return !barrel.getSealed() && barrel.getFluidLevel() == 0;
    }

    
    //Helper methods
    
    @Override
    protected boolean isItemValid(TileEntity tileEntity, ItemStack itemStack)
    {
        IInventory inventory = (IInventory)tileEntity;
        Class<?> cls = tileEntity.getClass();
        
        if(cls == TEBarrel.class)
            return new SlotChest(inventory, 0, 0, 0).setSize(EnumSize.LARGE).addItemException(ContainerChestTFC.getExceptions()).isItemValid(itemStack);

        if(cls == TEVessel.class)
            return new SlotChest(inventory, 0, 0, 0).setSize(EnumSize.MEDIUM).addItemException(ContainerChestTFC.getExceptions()).isItemValid(itemStack);

        return false;
    }
}
