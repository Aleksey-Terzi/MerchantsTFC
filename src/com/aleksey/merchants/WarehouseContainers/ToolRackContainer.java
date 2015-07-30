package com.aleksey.merchants.WarehouseContainers;

import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.tileentity.TileEntity;

import com.bioxx.tfc.Items.Tools.ItemProPick;
import com.bioxx.tfc.Items.Tools.ItemSpindle;
import com.bioxx.tfc.Items.Tools.ItemWeapon;
import com.bioxx.tfc.TileEntities.TEToolRack;

public class ToolRackContainer extends ChestContainer
{
    @Override
    public boolean isValid(TileEntity tileEntity)
    {
        return tileEntity.getClass() == TEToolRack.class;
    }
    
    //Helper methods
    
    @Override
    protected boolean isItemValid(TileEntity tileEntity, ItemStack itemStack)
    {
        Class<?> cls = tileEntity.getClass();
        
        if(cls != TEToolRack.class)
            return false;
        
        Item item = itemStack.getItem();
        
        return item instanceof ItemTool ||
            item instanceof ItemWeapon ||
            item instanceof ItemHoe ||
            item instanceof ItemProPick ||
            item instanceof ItemBow ||
            item instanceof ItemSword ||
            item instanceof ItemAxe ||
            item instanceof ItemSpade ||
            item instanceof ItemShears ||
            item instanceof ItemSpindle
            ;
    }
}