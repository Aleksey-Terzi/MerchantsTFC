package com.aleksey.merchants.Containers.Slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotTrusselCopy extends Slot
{
    public SlotTrusselCopy(IInventory iinventory, int slotIndex, int x, int y)
    {
        super(iinventory, slotIndex, x, y);
    }
    @Override
    public boolean isItemValid(ItemStack itemstack)
    {       
        return false;
    }
}
