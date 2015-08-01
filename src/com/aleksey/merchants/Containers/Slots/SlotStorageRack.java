package com.aleksey.merchants.Containers.Slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.bioxx.tfc.Items.ItemBlocks.ItemAnvil;
import com.bioxx.tfc.Items.ItemBlocks.ItemBarrels;
import com.bioxx.tfc.Items.ItemBlocks.ItemBellows;
import com.bioxx.tfc.Items.ItemBlocks.ItemCrucible;
import com.bioxx.tfc.Items.ItemBlocks.ItemLargeVessel;
import com.bioxx.tfc.Items.ItemBlocks.ItemWoodDoor;

public class SlotStorageRack extends Slot
{
    public SlotStorageRack(IInventory iinventory, int slotIndex, int x, int y)
    {
        super(iinventory, slotIndex, x, y);
    }
    @Override
    public boolean isItemValid(ItemStack itemstack)
    {
        Item item = itemstack.getItem();
        
        return item instanceof ItemAnvil
                || item instanceof ItemBellows
                || item instanceof ItemCrucible
                || item instanceof ItemLargeVessel
                || item instanceof ItemBarrels
                || item instanceof ItemWoodDoor
                ; 
    }
}
