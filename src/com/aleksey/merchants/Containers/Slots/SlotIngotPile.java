package com.aleksey.merchants.Containers.Slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.bioxx.tfc.TileEntities.TEIngotPile;

public class SlotIngotPile extends Slot
{
    public SlotIngotPile(IInventory iinventory, int slotIndex, int x, int y)
    {
        super(iinventory, slotIndex, x, y);
    }
    
    @Override
    public boolean isItemValid(ItemStack itemStack)
    {
        return isIngot(itemStack);
    }
    
    public static boolean isIngot(ItemStack itemStack)
    {
        Item item = itemStack.getItem();
        
        for(int i = 0; i < TEIngotPile.INGOTS.length; i++)
        {
            if(item == TEIngotPile.INGOTS[i])
                return true;
        }
        
        return false;
    }
}
