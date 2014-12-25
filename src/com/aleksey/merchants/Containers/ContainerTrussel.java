package com.aleksey.merchants.Containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.aleksey.merchants.Containers.Slots.SlotTrusselWithoutDie;
import com.aleksey.merchants.GUI.GuiTrussel;
import com.aleksey.merchants.Inventories.TrusselInventory;
import com.aleksey.merchants.Items.ItemTrussel;
import com.bioxx.tfc.Containers.ContainerTFC;
import com.bioxx.tfc.Containers.Slots.SlotOutputOnly;
import com.bioxx.tfc.Core.Player.PlayerInventory;

public class ContainerTrussel extends ContainerTFC
{
    private InventoryPlayer _inventoryplayer;
    private World _world;
    private TrusselInventory _inventory;

    public ContainerTrussel(InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        _inventoryplayer = inventoryplayer;
        _world = world;
        _inventory = new TrusselInventory();

        addSlotToContainer(new SlotTrusselWithoutDie(_inventory, 0, GuiTrussel.SrcSlotX, GuiTrussel.SlotY));
        addSlotToContainer(new SlotOutputOnly(_inventory, 1, GuiTrussel.DstSlotX, GuiTrussel.SlotY));

        PlayerInventory.buildInventoryLayout(this, inventoryplayer, 8, GuiTrussel.WindowHeight - 1 + 5, true, true);
    }
    
    @Override
    public void onContainerClosed(EntityPlayer entityplayer)
    {
        super.onContainerClosed(entityplayer);

        if (!_world.isRemote)
        {
            ItemStack itemStack1 = _inventory.getStackInSlotOnClosing(0);
            ItemStack itemStack2 = _inventory.getStackInSlotOnClosing(1);
            
            if (itemStack1 != null)
                entityplayer.entityDropItem(itemStack1, 0);
            
            if (itemStack2 != null)
                entityplayer.entityDropItem(itemStack2, 0);
        }
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

            if(slotNumber < 2)
            {
                if(!this.mergeItemStack(itemstack1, 2, this.inventorySlots.size(), true))
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
    
    public boolean copyTrussel()
    {
        ItemStack itemStack1 = _inventory.getStackInSlotOnClosing(0);
        ItemStack itemStack2 = _inventory.getStackInSlotOnClosing(1);
        
        if(itemStack1 == null || itemStack1.stackSize == 0 || itemStack2 != null)
            return false;
        
        ItemTrussel.copyDie(_inventoryplayer.getCurrentItem(), itemStack1);
        
        _inventory.setInventorySlotContents(0, null);
        _inventory.setInventorySlotContents(1, itemStack1);
        
        return true;
    }
}
