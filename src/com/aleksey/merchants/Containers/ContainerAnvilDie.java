package com.aleksey.merchants.Containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.aleksey.merchants.Containers.Slots.SlotFlan;
import com.aleksey.merchants.Containers.Slots.SlotTrussel;
import com.aleksey.merchants.GUI.GuiAnvilDie;
import com.aleksey.merchants.TileEntities.TileEntityAnvilDie;
import com.bioxx.tfc.Containers.ContainerTFC;
import com.bioxx.tfc.Containers.Slots.SlotAnvilHammer;
import com.bioxx.tfc.Containers.Slots.SlotForShowOnly;
import com.bioxx.tfc.Containers.Slots.SlotOutputOnly;
import com.bioxx.tfc.Core.Player.PlayerInventory;

public class ContainerAnvilDie extends ContainerTFC
{
    private TileEntityAnvilDie _anvilDie;
    private World _world;

    public ContainerAnvilDie(InventoryPlayer inventoryplayer, TileEntityAnvilDie anvilDie, World world, int x, int y, int z)
    {
        _anvilDie = anvilDie;
        _world = world;

        addSlotToContainer(new SlotAnvilHammer(inventoryplayer.player, _anvilDie, 0, GuiAnvilDie.HammerSlotX, GuiAnvilDie.HammerSlotY));
        addSlotToContainer(new SlotTrussel(_anvilDie, 1, GuiAnvilDie.TrusselSlotX, GuiAnvilDie.TrusselSlotY));
        addSlotToContainer(new SlotFlan(_anvilDie, 2, GuiAnvilDie.FlanSlotX, GuiAnvilDie.FlanSlotY));
        addSlotToContainer(new SlotForShowOnly(_anvilDie, 3, GuiAnvilDie.AnvilDieSlotX, GuiAnvilDie.AnvilDieSlotY));
        addSlotToContainer(new SlotOutputOnly(_anvilDie, 4, GuiAnvilDie.CoinSlotX, GuiAnvilDie.CoinSlotY));

        PlayerInventory.buildInventoryLayout(this, inventoryplayer, 8, GuiAnvilDie.WindowHeight - 1 + 5, false, true);
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer)
    {
        return true;
    }

    @Override
    public ItemStack transferStackInSlotTFC(EntityPlayer entityplayer, int slotNumber)
    {
        Slot slot = (Slot)inventorySlots.get(slotNumber);
        
        if(slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();

            if(slotNumber < 5)
            {
                if(!this.mergeItemStack(itemstack1, 5, this.inventorySlots.size(), true))
                    return null;
            }
            else
            {
                if(!this.mergeItemStack(itemstack1, 0, 5, false))
                    return null;
            }

            if(itemstack1.stackSize == 0)
                slot.putStack(null);
            else
                slot.onSlotChanged();
        }
        
        return null;
    }
}
