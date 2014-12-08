package com.aleksey.merchants.Containers.Slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.bioxx.tfc.Items.ItemIngot;

public class SlotIngotPile extends Slot
{
    public SlotIngotPile(IInventory iinventory, int slotIndex, int x, int y)
    {
        super(iinventory, slotIndex, x, y);
    }
    
    @Override
    public boolean isItemValid(ItemStack itemstack)
    {   
        return itemstack.getItem() instanceof ItemIngot;
    }
}
