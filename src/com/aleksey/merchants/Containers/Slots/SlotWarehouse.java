package com.aleksey.merchants.Containers.Slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;

import com.aleksey.merchants.Items.ItemWarehouseBook;

public class SlotWarehouse extends Slot
{
    public SlotWarehouse(IInventory iinventory, int slotIndex, int x, int y)
    {
        super(iinventory, slotIndex, x, y);
    }
    @Override
    public boolean isItemValid(ItemStack itemstack)
    {
        Item item = itemstack.getItem();
        
        return item instanceof ItemWritableBook || item instanceof ItemWarehouseBook; 
    }
}
