package com.aleksey.merchants.Containers;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

import com.aleksey.merchants.Containers.Slots.SlotStall;
import com.aleksey.merchants.Containers.Slots.SlotStallBook;
import com.aleksey.merchants.GUI.GuiStall;
import com.aleksey.merchants.Helpers.ItemHelper;
import com.aleksey.merchants.Helpers.PrepareTradeResult;
import com.aleksey.merchants.TileEntities.TileEntityStall;
import com.bioxx.tfc.Containers.ContainerTFC;
import com.bioxx.tfc.Containers.Slots.SlotForShowOnly;
import com.bioxx.tfc.Core.Player.PlayerInventory;
import com.bioxx.tfc.Food.ItemFoodTFC;
import com.bioxx.tfc.api.Interfaces.IFood;

public class ContainerStall extends ContainerTFC
{   
    private TileEntityStall _stall;
    private boolean _isOwnerMode;
    private ArrayList<Integer> _paySlotIndexes;
    private World _world;

    public ContainerStall(InventoryPlayer inventoryplayer, TileEntityStall stall, boolean isOwnerMode, World world, int x, int y, int z)
    {
        _stall = stall;
        _isOwnerMode = isOwnerMode;
        _world = world;

        buildLayout();

        PlayerInventory.buildInventoryLayout(this, inventoryplayer, 8, GuiStall.WindowHeight - 1 + 5, false, true);

    }

    private void buildLayout()
    {
        int y = GuiStall.TopSlotY;
        int index = 0;
        
        for(int i = 0; i < TileEntityStall.PriceCount; i++)
        {
            if(_isOwnerMode)
                addSlotToContainer(new SlotStall(_stall, index++, GuiStall.PricesSlotX, y));
            else
                addSlotToContainer(new SlotForShowOnly(_stall, index++, GuiStall.PricesSlotX, y));
            
            addSlotToContainer(new SlotStall(_stall, index++, GuiStall.GoodsSlotX, y));
            
            y += GuiStall.SlotSize;
        }
        
        if(_isOwnerMode)
            addSlotToContainer(new SlotStallBook(_stall, index, GuiStall.BookSlotX, GuiStall.BookSlotY));
    }
    
    @Override
    public void onContainerClosed(EntityPlayer entityplayer)
    {
        super.onContainerClosed(entityplayer);

        if(!_world.isRemote)
            _stall.closeInventory();
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer)
    {
        return true;
    }

    @Override
    public ItemStack transferStackInSlotTFC(EntityPlayer entityplayer, int i)
    {
        /*
        Slot slot = (Slot)inventorySlots.get(i);
        
        if(slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();

            if(i < TileEntityStall.ItemCount)
            {
                if(!this.mergeItemStack(itemstack1, TileEntityStall.ItemCount, this.inventorySlots.size(), true))
                    return null;
            }
            else
            {
                if(!this.mergeItemStack(itemstack1, 0, TileEntityStall.ItemCount, false))
                    return null;
            }

            if(itemstack1.stackSize == 0)
                slot.putStack(null);
            else
                slot.onSlotChanged();
        }
        */
        
        return null;
    }
    
    @Override
    public ItemStack slotClick(int slotNumber, int mouseButton, int key, EntityPlayer player)
    {
        //mouseButton == 0 - Left button 
        //key == 1 - Shift down

        ItemStack itemstack = null;
        InventoryPlayer inventoryplayer = player.inventory;
        ItemStack itemstack1;

        if (key != 5)
        {
            Slot slot2;
            int k1;
            ItemStack itemstack3;

            if ((key == 0 || key == 1) && (mouseButton == 0 || mouseButton == 1))
            {
                if (slotNumber == -999)
                {
                    if (inventoryplayer.getItemStack() != null && slotNumber == -999)
                    {
                        if (mouseButton == 0)
                        {
                            player.dropPlayerItemWithRandomChoice(inventoryplayer.getItemStack(), false);
                            inventoryplayer.setItemStack((ItemStack)null);
                        }

                        if (mouseButton == 1)
                        {
                            player.dropPlayerItemWithRandomChoice(inventoryplayer.getItemStack().splitStack(1), false);

                            if (inventoryplayer.getItemStack().stackSize == 0)
                            {
                                inventoryplayer.setItemStack((ItemStack)null);
                            }
                        }
                    }
                }
                else if (key == 1)
                {
                    if (slotNumber < 0)
                    {
                        return null;
                    }

                    slot2 = (Slot)this.inventorySlots.get(slotNumber);

                    if (slot2 != null && slot2.canTakeStack(player))
                    {
                        itemstack1 = this.transferStackInSlot(player, slotNumber);

                        if (itemstack1 != null)
                        {
                            Item item = itemstack1.getItem();
                            itemstack = itemstack1.copy();

                            if (slot2 != null && slot2.getStack() != null && slot2.getStack().getItem() == item)
                            {
                                this.retrySlotClick(slotNumber, mouseButton, true, player);
                            }
                        }
                    }
                }
                else
                {
                    if (slotNumber < 0)
                        return null;

                    slot2 = (Slot)this.inventorySlots.get(slotNumber);

                    if (slot2 != null)
                    {
                        itemstack1 = slot2.getStack();
                        ItemStack itemstack4 = inventoryplayer.getItemStack();

                        if (itemstack1 != null)
                            itemstack = itemstack1.copy();

                        if (itemstack1 == null)
                            putItemToEmptySlot(slot2, mouseButton, player);
                        else if (slot2.canTakeStack(player))
                            putItemToNonEmptySlot(slot2, mouseButton, player);

                        slot2.onSlotChanged();
                    }
                }
            }
        }

        return itemstack;
    }
    
    private void putItemToEmptySlot(Slot slot, int mouseButton, EntityPlayer player)
    {
        boolean isPlayerSlot = isPlayerSlot(slot.slotNumber);
        
        if(!_isOwnerMode && !isPlayerSlot)
            return;
        
        InventoryPlayer inventoryplayer = player.inventory;
        ItemStack playerItemStack = inventoryplayer.getItemStack();
        
        if (playerItemStack == null || !slot.isItemValid(playerItemStack))
            return;

        boolean isBookSlot = isBookSlot(slot.slotNumber);
        boolean isFood = !isPlayerSlot && playerItemStack.getItem() instanceof IFood;
        
        int sizeToPut = mouseButton == 0 && !isBookSlot && !isFood ? playerItemStack.stackSize : 1;

        if (sizeToPut > slot.getSlotStackLimit())
            sizeToPut = slot.getSlotStackLimit();
        
        if (sizeToPut > 0 && playerItemStack.stackSize >= sizeToPut)
        {
            ItemStack stack;
                
            if(isFood)
                stack = getFoodItemStack(playerItemStack, mouseButton == 0);
            else
            {
                stack = isBookSlot || isPlayerSlot
                    ? playerItemStack.splitStack(sizeToPut)
                    : playerItemStack.copy().splitStack(sizeToPut);
            }
            
            slot.putStack(stack);
            
            if(isBookSlot)
            {
                _stall.setOwnerUserName(player.getCommandSenderName());
                _stall.calculateQuantitiesInWarehouse();
            }
        }

        if (playerItemStack.stackSize == 0)
            inventoryplayer.setItemStack((ItemStack)null);
    }
    
    private void putItemToNonEmptySlot(Slot slot, int mouseButton, EntityPlayer player)
    {
        if(_isOwnerMode || isPlayerSlot(slot.slotNumber))
            putItemToNonEmptySlotOwner(slot, mouseButton, player);
        else
            putItemToNonEmptySlotBuyer(slot, mouseButton, player);
    }
    
    private void putItemToNonEmptySlotOwner(Slot slot, int mouseButton, EntityPlayer player)
    {
        InventoryPlayer inventoryplayer = player.inventory;
        ItemStack playerItemStack = inventoryplayer.getItemStack();
        ItemStack slotItemStack = slot.getStack();
        boolean isPlayerSlot = isPlayerSlot(slot.slotNumber);
        boolean isBookSlot = isBookSlot(slot.slotNumber);
        boolean isFood = !isPlayerSlot && slotItemStack.getItem() instanceof IFood;
        
        if (playerItemStack == null)
        {
            if(isFood)
            {
                if(mouseButton == 0)
                    slot.putStack((ItemStack)null);
                else
                    slot.putStack(splitFoodWeight(slotItemStack));
            }
            else
            {
                int sizeToGet = mouseButton == 0 ? slotItemStack.stackSize : (slotItemStack.stackSize + 1) / 2;
                
                if(sizeToGet == 0)
                    return;
        
                ItemStack itemToGet = slot.decrStackSize(sizeToGet);
        
                if(isPlayerSlot || isBookSlot)
                    inventoryplayer.setItemStack(itemToGet);
                
                if(isBookSlot)
                    _stall.setOwnerUserName(null);
                
                if (slotItemStack.stackSize == 0)
                    slot.putStack((ItemStack)null);
        
                if(isPlayerSlot || isBookSlot)
                    slot.onPickupFromSlot(player, inventoryplayer.getItemStack());
            }
            
            return;
        }

        if (isBookSlot || !slot.isItemValid(playerItemStack))
            return;

        if (ItemHelper.areItemEquals(slotItemStack, playerItemStack))
        {
            if(isFood)
                addFoodWeight(slotItemStack, playerItemStack, mouseButton == 0);
            else
            {
                int sizeToPut = mouseButton == 0 && !isBookSlot && !isFood ? playerItemStack.stackSize : 1;

                if (sizeToPut > slot.getSlotStackLimit() - slotItemStack.stackSize)
                    sizeToPut = slot.getSlotStackLimit() - slotItemStack.stackSize;
    
                if (sizeToPut > playerItemStack.getMaxStackSize() - slotItemStack.stackSize)
                    sizeToPut = playerItemStack.getMaxStackSize() - slotItemStack.stackSize;
                
                if(sizeToPut == 0)
                    return;

                if(isPlayerSlot)
                {
                    playerItemStack.splitStack(sizeToPut);
        
                    if (playerItemStack.stackSize == 0)
                        inventoryplayer.setItemStack((ItemStack)null);
                }

                slotItemStack.stackSize += sizeToPut;
            }
        }
        else if (isPlayerSlot)
        {
            slot.putStack(playerItemStack);
            inventoryplayer.setItemStack(slotItemStack);
        }
        else
        {
            if(playerItemStack.getItem() instanceof IFood)
                slot.putStack(getFoodItemStack(playerItemStack, true));
            else
                slot.putStack(playerItemStack.copy());
        }
    }
    
    private ItemStack splitFoodWeight(ItemStack itemStack)
    {
        IFood food = (IFood)itemStack.getItem();
        
        float newWeight;
        
        newWeight = food.getFoodWeight(itemStack) / 2;
        newWeight = 10 * (int)(newWeight / 10);
        
        if(newWeight == 0)
            return null;
        
        ItemFoodTFC.createTag(itemStack, newWeight);
        
        return itemStack;
    }
    
    private ItemStack addFoodWeight(ItemStack slotItemStack, ItemStack playerItemStack, boolean isAll)
    {
        IFood food = (IFood)slotItemStack.getItem();
        
        float playerWeight;
        
        if(isAll)
        {
            playerWeight = food.getFoodWeight(playerItemStack);
            playerWeight = 10 * (int)(playerWeight / 10);
            
            if(playerWeight == 0)
                playerWeight = 10;
        }
        else
            playerWeight = 10;
        
        float newSlotWeight = food.getFoodWeight(slotItemStack) + playerWeight;
        
        if(newSlotWeight > food.getFoodMaxWeight(slotItemStack))
            newSlotWeight = food.getFoodMaxWeight(slotItemStack);
        
        ItemFoodTFC.createTag(slotItemStack, newSlotWeight);
        
        return slotItemStack;
    }
    
    private ItemStack getFoodItemStack(ItemStack srcItemStack, boolean isAll)
    {
        float weight;
        
        if(isAll)
        {
            IFood food = (IFood)srcItemStack.getItem();

            weight = food.getFoodWeight(srcItemStack);
            weight = 10 * (int)(weight / 10);
            
            if(weight == 0)
                weight = 10;
        }
        else
            weight = 10;
        
        ItemStack resultItemStack = srcItemStack.copy();
        
        ItemFoodTFC.createTag(resultItemStack, weight);
        
        return resultItemStack;
    }
    
    private void putItemToNonEmptySlotBuyer(Slot slot, int mouseButton, EntityPlayer player)
    {
        if(player.worldObj.isRemote)
            return;
        
        int priceSlotIndex = getPriceSlotIndex(slot.getSlotIndex());
        
        if(priceSlotIndex < 0)
            return;
        
        InventoryPlayer inventoryplayer = player.inventory;
        ItemStack playerItemStack = inventoryplayer.getItemStack();
        ItemStack goodItemStack = slot.getStack();
        ItemStack payItemStack = _stall.getStackInSlot(priceSlotIndex);
        
        if (playerItemStack == null)
        {
            if(!preparePayAndTrade(goodItemStack, payItemStack, player))
               return;
            
            confirmPay(payItemStack, inventoryplayer);
            
            _stall.confirmTrade();
            
            ItemStack newItemStack = goodItemStack.copy();
            
            if(newItemStack.getItem() instanceof IFood)
                ItemFoodTFC.createTag(newItemStack, ((IFood)newItemStack.getItem()).getFoodWeight(newItemStack));

            inventoryplayer.setItemStack(newItemStack);
            
            player.worldObj.markBlockForUpdate(_stall.xCoord, _stall.yCoord, _stall.zCoord);
            
            player.onUpdate();

            _stall.actionBuy(inventoryplayer.getItemStack());

            return;
        }
        
        int goodQuantity = ItemHelper.getItemStackQuantity(goodItemStack);
        
        if (!slot.isItemValid(playerItemStack)
            || !ItemHelper.areItemEquals(goodItemStack, playerItemStack)
            || goodQuantity + ItemHelper.getItemStackQuantity(playerItemStack) > ItemHelper.getItemStackMaxQuantity(playerItemStack, inventoryplayer)
            || !preparePayAndTrade(goodItemStack, payItemStack, player)
            )
        {
            return;
        }
        
        confirmPay(payItemStack, inventoryplayer);

        _stall.confirmTrade();
        
        ItemHelper.increaseStackQuantity(playerItemStack, goodQuantity);
        
        player.worldObj.markBlockForUpdate(_stall.xCoord, _stall.yCoord, _stall.zCoord);
        
        player.onUpdate();

        _stall.actionBuy(inventoryplayer.getItemStack());
    }
    
    private boolean preparePayAndTrade(ItemStack goodItemStack, ItemStack payItemStack, EntityPlayer player)
    {
        if(!preparePay(payItemStack, player.inventory))
        {
            player.addChatComponentMessage(new ChatComponentTranslation("gui.Stall.NoPays", new Object[0]));
            return false;
        }
        
        PrepareTradeResult result = _stall.prepareTrade(goodItemStack, payItemStack);
        
        if(result == PrepareTradeResult.Success)
            return true;
        
        if(result == PrepareTradeResult.NoGoods)
            player.addChatComponentMessage(new ChatComponentTranslation("gui.Stall.NoGoods", new Object[0]));
        else
            player.addChatComponentMessage(new ChatComponentTranslation("gui.Stall.NoPaysSpace", new Object[0]));

        return false;
    }
    
    private boolean preparePay(ItemStack payItemStack, InventoryPlayer inventoryplayer)
    {
        if(payItemStack == null)
            return false;
        
        int quantity = ItemHelper.getItemStackQuantity(payItemStack);
        
        if(quantity == 0)
            return false;
        
        _paySlotIndexes = new ArrayList<Integer>();
        
        for(int i = 0; i < inventoryplayer.getSizeInventory() && quantity > 0; i++)
        {
            ItemStack invItemStack = inventoryplayer.getStackInSlot(i);
            
            if(invItemStack == null || !ItemHelper.areItemEquals(payItemStack, invItemStack))
                continue;
            
            int invQuantity = ItemHelper.getItemStackQuantity(invItemStack);
            
            if(invQuantity == 0)
                continue;
            
            _paySlotIndexes.add(i);
            
            quantity -= invQuantity;
        }
        
        return quantity <= 0;
    }
    
    private void confirmPay(ItemStack payItemStack, InventoryPlayer inventoryplayer)
    {
        int quantity = ItemHelper.getItemStackQuantity(payItemStack);
        
        for(int i = 0; i < _paySlotIndexes.size(); i++)
        {
            int slotIndex = _paySlotIndexes.get(i);
            ItemStack invItemStack = inventoryplayer.getStackInSlot(slotIndex);
            int invQuantity = ItemHelper.getItemStackQuantity(invItemStack);
            
            int sizeToGet = invQuantity > quantity ? quantity: invQuantity;
            
            ItemHelper.increaseStackQuantity(invItemStack, -sizeToGet);
            
            if(invItemStack.stackSize == 0)
                inventoryplayer.setInventorySlotContents(slotIndex, (ItemStack)null);
            
            inventoryplayer.markDirty();
            
            quantity -= sizeToGet;
        }
        
        _paySlotIndexes = null;
    }
    
    private int getPriceSlotIndex(int goodSlotIndex)
    {
        for(int i = 0; i < TileEntityStall.GoodsSlotIndexes.length; i++)
        {
            if(TileEntityStall.GoodsSlotIndexes[i] == goodSlotIndex)
                return TileEntityStall.PricesSlotIndexes[i];
        }
        
        return -1;
    }
    
    private boolean isPlayerSlot(int slotNumber)
    {
        return _isOwnerMode
            ? slotNumber >= TileEntityStall.ItemCount
            : slotNumber >= TileEntityStall.ItemCount - 1;
    }

    private boolean isBookSlot(int slotNumber)
    {
        return _isOwnerMode && slotNumber == TileEntityStall.ItemCount - 1; 
    }
    
    public boolean isGoodsSlot(int slotNumber)
    {
        return slotNumber < 2 * TileEntityStall.PriceCount && slotNumber % 2 == 1; 
    }
 }