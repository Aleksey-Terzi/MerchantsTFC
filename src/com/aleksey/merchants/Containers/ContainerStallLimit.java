package com.aleksey.merchants.Containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.aleksey.merchants.GUI.GuiStallLimit;
import com.aleksey.merchants.TileEntities.TileEntityStall;
import com.bioxx.tfc.Containers.ContainerTFC;
import com.bioxx.tfc.Containers.Slots.SlotForShowOnly;
import com.bioxx.tfc.Core.Player.PlayerInventory;

public class ContainerStallLimit extends ContainerTFC
{
    public ContainerStallLimit(InventoryPlayer inventoryplayer, TileEntityStall stall, World world, int x, int y, int z)
    {
        addSlotToContainer(new SlotForShowOnly(stall, stall.getActivePriceSlotIndex(), GuiStallLimit.PriceSlotX, GuiStallLimit.SlotY));
        addSlotToContainer(new SlotForShowOnly(stall, stall.getActiveGoodSlotIndex(), GuiStallLimit.GoodSlotX, GuiStallLimit.SlotY));

        PlayerInventory.buildInventoryLayout(this, inventoryplayer, 8, GuiStallLimit.WindowHeight - 1 + 5, false, true);
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer)
    {
        return true;
    }

    @Override
    public ItemStack transferStackInSlotTFC(EntityPlayer entityplayer, int slotNumber)
    {
        if(slotNumber < 2)
            return null;
        
        Slot slot = (Slot)inventorySlots.get(slotNumber);
        
        if(slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();

            if(!this.mergeItemStack(itemstack1, 2, this.inventorySlots.size(), true))
                return null;

            if(itemstack1.stackSize == 0)
                slot.putStack(null);
            else
                slot.onSlotChanged();
        }
        
        return null;
    }
}
