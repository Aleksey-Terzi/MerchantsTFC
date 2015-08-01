package com.aleksey.merchants.Containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.aleksey.merchants.Containers.Slots.SlotStorageRack;
import com.aleksey.merchants.GUI.GuiStorageRack;
import com.aleksey.merchants.TileEntities.TileEntityStorageRack;
import com.bioxx.tfc.Containers.ContainerTFC;
import com.bioxx.tfc.Core.Player.PlayerInventory;

public class ContainerStorageRack extends ContainerTFC
{
    private TileEntityStorageRack _storageRack;
    private World _world;

    public ContainerStorageRack(InventoryPlayer inventoryplayer, TileEntityStorageRack storageRack, World world, int x, int y, int z)
    {
        _storageRack = storageRack;
        _world = world;

        addSlotToContainer(new SlotStorageRack(_storageRack, 0, GuiStorageRack.SlotX, GuiStorageRack.SlotY));

        PlayerInventory.buildInventoryLayout(this, inventoryplayer, 8, GuiStorageRack.WindowHeight - 1 + 5, false, true);
    }
    
    @Override
    public void onContainerClosed(EntityPlayer entityplayer)
    {
        super.onContainerClosed(entityplayer);

        if(!_world.isRemote)
            _storageRack.closeInventory();
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

            if(slotNumber < 1)
            {
                if(!this.mergeItemStack(itemstack1, 1, this.inventorySlots.size(), true))
                    return null;
            }
            else
            {
                if(!this.mergeItemStack(itemstack1, 0, 1, false))
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
