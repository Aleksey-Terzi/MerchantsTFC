package com.aleksey.merchants.Containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.aleksey.merchants.Containers.Slots.SlotWarehouse;
import com.aleksey.merchants.GUI.GuiWarehouse;
import com.aleksey.merchants.TileEntities.TileEntityWarehouse;
import com.bioxx.tfc.Containers.ContainerTFC;
import com.bioxx.tfc.Core.Player.PlayerInventory;

public class ContainerWarehouse extends ContainerTFC
{
    private TileEntityWarehouse _warehouse;
    private World _world;

    public ContainerWarehouse(InventoryPlayer inventoryplayer, TileEntityWarehouse warehouse, World world, int x, int y, int z)
    {
        _warehouse = warehouse;
        _world = world;

        addSlotToContainer(new SlotWarehouse(_warehouse, 0, GuiWarehouse.SlotX, GuiWarehouse.SlotY));

        PlayerInventory.buildInventoryLayout(this, inventoryplayer, 8, GuiWarehouse.WindowHeight - 1 + 5, false, true);
    }
    
    @Override
    public void onContainerClosed(EntityPlayer entityplayer)
    {
        super.onContainerClosed(entityplayer);

        if(!_world.isRemote)
            _warehouse.closeInventory();
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