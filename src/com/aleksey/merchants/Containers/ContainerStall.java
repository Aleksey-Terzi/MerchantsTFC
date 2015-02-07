package com.aleksey.merchants.Containers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.aleksey.merchants.Containers.Slots.SlotStall;
import com.aleksey.merchants.Containers.Slots.SlotStallBook;
import com.aleksey.merchants.GUI.GuiStall;
import com.aleksey.merchants.Helpers.ItemHelper;
import com.aleksey.merchants.Helpers.PrepareTradeResult;
import com.aleksey.merchants.Items.ItemWarehouseBook;
import com.aleksey.merchants.TileEntities.TileEntityStall;
import com.bioxx.tfc.Containers.ContainerTFC;
import com.bioxx.tfc.Containers.Slots.SlotForShowOnly;
import com.bioxx.tfc.Core.Player.PlayerInventory;
import com.bioxx.tfc.Food.ItemFoodTFC;
import com.bioxx.tfc.api.Interfaces.IFood;

//slotClick is based on https://github.com/Mr-J/AdvancedBackpackMod/blob/master/unrelated/slotClick%2BComments%2BRename%2BHelpers.java.txt

public class ContainerStall extends ContainerTFC
{   
    private TileEntityStall _stall;
    private boolean _isOwnerMode;
    private ArrayList<Integer> _paySlotIndexes;
    private World _world;
    
    /*
     * slotClick fields
     * 
     * these 3 variables are private in container, so we can not access them here
     * we mirror and rename these locally here
     * private int field_94536_g = 0;
     * seems to hold the state of the dragged multislot placement of an itemstack
     * renamed to distributeState
     * 0 = not started
     * 1 = currently placing
     * 2 = drag operation done, place into the slots
     * private final Set field_94537_h = new HashSet();  
    */
    private int _pressedKeyInRange = -1;
    private int _distributeState = 0;
    private final Set _distributeSlotSet = new HashSet();

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
    public ItemStack transferStackInSlotTFC(EntityPlayer entityplayer, int slotNumber)
    {
        if(_isOwnerMode)
            return transferStackInSlotTFC_OwnerMode(entityplayer, slotNumber);
        else
            return transferStackInSlotTFC_BuyerMode(entityplayer, slotNumber);
    }
    
    private ItemStack transferStackInSlotTFC_OwnerMode(EntityPlayer entityplayer, int slotNumber)
    {
        Slot slot = (Slot)inventorySlots.get(slotNumber);
        
        if(slot == null || !slot.getHasStack())
            return null;

        ItemStack itemstack1 = slot.getStack();

        if(slotNumber < TileEntityStall.ItemCount)
        {
            if(isBookSlot(slotNumber))
            {
                if(this.mergeItemStack(itemstack1, TileEntityStall.ItemCount, this.inventorySlots.size(), true))
                {
                    _stall.setOwner(null);
                    
                    _world.markBlockForUpdate(_stall.xCoord, _stall.yCoord, _stall.zCoord);
                }
                else
                    return null;
            }
            else
                itemstack1.stackSize = 0;
        }
        else
        {
            int bookSlotIndex = getBookSlotIndex();
            Slot bookSlot = (Slot)this.inventorySlots.get(bookSlotIndex);
            
            if(!(itemstack1.getItem() instanceof ItemWarehouseBook) || bookSlot.getStack() != null)
                return null;
            
            bookSlot.putStack(itemstack1.splitStack(1));
            
            if(!entityplayer.worldObj.isRemote)
            {
                _stall.setOwner(entityplayer);
                _stall.calculateQuantitiesInWarehouse();
                
                _world.markBlockForUpdate(_stall.xCoord, _stall.yCoord, _stall.zCoord);
            }
            
            entityplayer.onUpdate();
        }

        if(itemstack1.stackSize == 0)
            slot.putStack(null);
        else
            slot.onSlotChanged();
        
        return null;
    }
    
    private ItemStack transferStackInSlotTFC_BuyerMode(EntityPlayer entityplayer, int slotNumber)
    {
        if(entityplayer.worldObj.isRemote || slotNumber >= TileEntityStall.ItemCount)
            return null;
        
        Slot slot = (Slot)inventorySlots.get(slotNumber);
        
        if(slot == null || !slot.getHasStack())
            return null;

        ItemStack goodItemStack = slot.getStack();
        
        int goodSlotIndex = slot.getSlotIndex();
        int priceSlotIndex = getPriceSlotIndex(goodSlotIndex);
        ItemStack payItemStack = _stall.getStackInSlot(priceSlotIndex);
        
        InventoryPlayer inventoryPlayer = entityplayer.inventory;
        
        if(!preparePayAndTrade(goodSlotIndex, goodItemStack, payItemStack, entityplayer))
            return null;
        
        ArrayList<Integer> slotIndexes = new ArrayList<Integer>();
        
        if(!prepareTransferGoods(goodItemStack, inventoryPlayer, slotIndexes))
            return null;

        confirmPay(payItemStack, inventoryPlayer);
         
         _stall.confirmTrade();
         
         confirmTransferGoods(goodItemStack, inventoryPlayer, slotIndexes);

         entityplayer.worldObj.markBlockForUpdate(_stall.xCoord, _stall.yCoord, _stall.zCoord);
         
         entityplayer.onUpdate();

        return null;
    }
    
    /** 
     * the slotClick method (from net.minecraft.inventory.Container.java)
     * handles every click performed on a container, this happens normally 
     * in a gui
     * 
     * @param targetSlotID the ID of the clicked slot, the slotClick method is performed on this slot 
     * @param mouseButtonPressed the pressed mouse button when slotClick was invoked, notice that this has not to be the "real" mouse
     * @param flag a range of flag indicating different things 
     * @param player the player performing the click
     * 
     * values for mouseButtonPressed:
     * 0 = left button clicked
     * 1 = right button clicked
     * 2 = middle (third) button clicked / also left button pressed & hold (only with item@cursor)
     * 6 = right button pressed & hold
     * 
     * values for flag:
     * 0 = standard single click
     * 1 = single click + shift modifier
     * 2 = hotbar key is pressed (keys 0-9)
     * 3 = click with the middle button
     * 4 = click outside of the current gui window
     * 5 = button pressed & hold with the cursor holding an itemstack
     * 6 = double left click
     **/
    @Override
    public ItemStack slotClick(int targetSlotID, int mouseButtonPressed, int flag, EntityPlayer player)
    {
        ItemStack returnStack = null;
        InventoryPlayer inventoryplayer = player.inventory;
        //kind of a multipurpose variable
        int sizeOrID;
        ItemStack movedItemStack;        

        /*
         * PART 1: DRAGGED DISTRIBUTION
         * This is a special case where the itemStack the mouseCursor currently holds
         * is distributed over several fields of a container, which is only 
         * done if the a mouse button is pressed and hold (flag == 5)
         */
        if (flag == 5)   
        {
            int currentDistributeState = _distributeState;
            _distributeState = checkForPressedButton(mouseButtonPressed);
            
            /*
             * if distributeState is neither 1 nor 2 AND
             * currentDistributeState != distributestate 
             * then reset the distributestate and the distributeSlotSet
             */
            if ((currentDistributeState != 1 || _distributeState != 2) && currentDistributeState != _distributeState)
            {
                this.resetDistributionVariables();
            }
            /*
             * else if the player current hold nothing 
             * on his mouse cursor (no stack picked up)
             */
            else if (inventoryplayer.getItemStack() == null)
            {
                this.resetDistributionVariables();
            }
            else if (_distributeState == 0)
            {
                _pressedKeyInRange = checkForPressedButton2(mouseButtonPressed);
                
                //true for 0 or 1
                if (checkValue(_pressedKeyInRange))             
                {
                    _distributeState = 1;
                    _distributeSlotSet.clear();
                }
                else
                {
                    this.resetDistributionVariables();
                }
            }
            else if (_distributeState == 1)
            {
                //get the slot for which the click is performed
                Slot currentTargetSlot = (Slot)this.inventorySlots.get(targetSlotID);
                
                if (currentTargetSlot != null && 
                stackFitsInSlot(currentTargetSlot, inventoryplayer.getItemStack(), true) &&             
                currentTargetSlot.isItemValid(inventoryplayer.getItemStack()) &&
                inventoryplayer.getItemStack().stackSize > _distributeSlotSet.size())
                
                {
                    /*
                     * add the slot to the set
                     * (to which the itemstack will be distributed)
                     */
                    _distributeSlotSet.add(currentTargetSlot);
                }
            }
            else if (_distributeState == 2)
            {
                if (!_distributeSlotSet.isEmpty())
                {
                    putItemToDistributeSlotSet(mouseButtonPressed, player);
                }
                
                this.resetDistributionVariables();
            }
            else
            {
                this.resetDistributionVariables();
            }
        }
        else if (_distributeState != 0)
        {
            this.resetDistributionVariables();
        }
        /*
         * PART 2: NORMAL SLOTCLICK
         * this part handles all other slotClicks which do
         * not distribute an itemstack over several slots
         */
        else
        {
            /*
             *multipurpose variable, mostly used for holding
             *the number of items to be transfered, if used 
             *otherwise it will be commented seperately 
             */
            Slot targetSlotCopy;
            
            int transferItemCount;
            ItemStack targetSlotItemStack;

            /*
             *only for a standard or shift click AND 
             *a left or right button click
             */
            if ((flag == 0 || flag == 1) && (mouseButtonPressed == 0 || mouseButtonPressed == 1))
            {
                //if the targetSlotID is not valid
                if (targetSlotID == -999)
                {
                    if (inventoryplayer.getItemStack() != null && targetSlotID == -999)
                    {
                        /*
                         * on leftclick drop the complete itemstack from the inventory
                         * on rightclick drop a single item from the itemstack
                         */
                        if (mouseButtonPressed == 0)
                        {
                            player.dropPlayerItemWithRandomChoice(inventoryplayer.getItemStack(), false);
                            inventoryplayer.setItemStack((ItemStack)null);
                        }

                        if (mouseButtonPressed == 1)
                        {
                            player.dropPlayerItemWithRandomChoice(inventoryplayer.getItemStack().splitStack(1), false);

                            if (inventoryplayer.getItemStack().stackSize == 0)
                            {
                                inventoryplayer.setItemStack((ItemStack)null);
                            }
                        }
                    }
                }
                //if a click with shift modifier is performed (clicking while holding down shift)
                else if (flag == 1)
                {
                    //for an invalid slot return null
                    if (targetSlotID < 0)
                    {
                        return null;
                    }
                    targetSlotCopy = (Slot)this.inventorySlots.get(targetSlotID);
                    
                    //if targetSlotCopy is not null and the stack inside the slot can be moved
                    if (targetSlotCopy != null && targetSlotCopy.canTakeStack(player))
                    {
                        //transfer the picked up stack to targetSlotID in the targetinventory
                        movedItemStack = this.transferStackInSlot(player, targetSlotID);
                        //if the movedItemStack was not transferred completely
                        if (movedItemStack != null)
                        {
                            //here used to store an ID
                            Item movedItem = movedItemStack.getItem();
                            //set the return value to the rest
                            returnStack = movedItemStack.copy();

                            if (targetSlotCopy != null && targetSlotCopy.getStack() != null && targetSlotCopy.getStack().getItem() == movedItem)
                            {
                                //retry with the shift-click 
                                this.retrySlotClick(targetSlotID, mouseButtonPressed, true, player);
                            }
                        }
                    }
                }
                //if a click with NO shift modifier is performed
                else
                {
                    if (targetSlotID < 0)
                    {
                        return null;
                    }
                    targetSlotCopy = (Slot)this.inventorySlots.get(targetSlotID);
                    /*
                     * if the target slot is not empty
                     * save its itemstack
                     * save the itemstack to be transferred in cursorItemStack
                     */
                    if (targetSlotCopy != null)
                    {
                        /*
                         * movedItemStack is here used to store the target Slot stack
                         * instead of the currently moved itemstack
                         */
                        movedItemStack = targetSlotCopy.getStack();
                        ItemStack cursorItemStack = inventoryplayer.getItemStack();
                        /*
                         * if the targetSlot contains an itemstack,
                         * exchange it with the currently picked up stack
                         */
                        if (movedItemStack != null)
                        {
                            returnStack = movedItemStack.copy();
                        }

                        //if the target slot is empty
                        if (movedItemStack == null)
                        {
                            this.putItemToEmptySlot(targetSlotCopy, mouseButtonPressed, player);
                        }
                        /*
                         * if the target slot is not empty AND
                         * if the stack in the target slot can be moved (always true in Container.java)
                         */
                        else if (targetSlotCopy.canTakeStack(player))
                        {
                            this.putItemToNonEmptySlot(targetSlotCopy, mouseButtonPressed, player);
                        }

                        //update the target slot
                        targetSlotCopy.onSlotChanged();
                    }
                }
            }
            /*
             * if a hotbar key is pressed (flag == 2)
             */
            else if (flag == 2 && mouseButtonPressed >= 0 && mouseButtonPressed < 9)
            {

            }
            /*
             * if the pressed mouse button is the middle button and
             * the player is in creative mode and
             * has currently no stack in his hand and
             * the target slot is greater/equal to zero
             */
            else if (flag == 3 && player.capabilities.isCreativeMode && inventoryplayer.getItemStack() == null && targetSlotID >= 0)
            {

            }
            /*
             * if the player clicks outside of the gui and
             * he has an itemstack in his hands and
             * and the targetslot is greater/equal to zero
             */
            else if (flag == 4 && inventoryplayer.getItemStack() == null && targetSlotID >= 0)
            {
                targetSlotCopy = (Slot)this.inventorySlots.get(targetSlotID);
                
                /*
                 * if there is a stack in the targetslot
                 * moveItemStack size is 1 if leftclick or the stacksize of targetSlotCopy if rightclicked
                 * update targetSlotCopy
                 * drop movedItemStack at players position
                 */
                if (targetSlotCopy != null && targetSlotCopy.getHasStack())
                {
                    movedItemStack = targetSlotCopy.decrStackSize(mouseButtonPressed == 0 ? 1 : targetSlotCopy.getStack().stackSize);
                    targetSlotCopy.onPickupFromSlot(player, movedItemStack);
                    player.dropPlayerItemWithRandomChoice(movedItemStack, false);
                }
            }
            /*
             * if the player performs a double leftclick and
             * the targetslot is greater/equal to zero
             */
            else if (flag == 6 && targetSlotID >= 0)
            {

            }
        }
        //return any remains of the operation
        return returnStack;
    }
    
    /*
     * need to overwrite the container.java method to call
     * the modified slotClick instead of the container method
     */
    @Override
    protected void retrySlotClick(int targetSlotID, int mouseButtonPressed, boolean flag, EntityPlayer entity)
    {
        //a retry of slotClick with flag 1 (shift click)
        this.slotClick(targetSlotID, mouseButtonPressed, 1, entity);
    }
    
    /**
     * This is a renamed version of the method
     * func_94532_c in net.minecraft.inventory.Container.java
     * this is not needed but i dont like non-sense method names
     * 
     * @param mouseButtonPressed can be {0,1,2,6} from what i observed
     * @return 2 if mouseButtonPressed is 6, 0 else
     * 
     * if there are other values possible for mouseButtonPressed
     * the method returns the following:
     * 0  -> 0
     * 1  -> 1
     * 2  -> 2
     * 3  -> 3
     * for values over 3 the assignments restarts from the top 
     * (so 4 is 0, 5 is 1...)
     */
    public static int checkForPressedButton(int mouseButtonPressed)
    {
        return mouseButtonPressed & 3;
    }
    
    /**
     * This is a renamed version of the method 
     * func_94533_d in net.minecaft.inventory.Container.java
     * this is not needed but i dont like non-sense method names 
     */
    protected void resetDistributionVariables()
    {
        _distributeState = 0;
        _distributeSlotSet.clear();
    }
    
   
    /**
     * This is a renamed version of the method
     * func_94529_b in net.minecraft.inventory.Container.java
     * this is not needed but i dont like non-sense method names
     * 
     * @param mouseButtonPressed can be {0,1,2,6} from what i observed
     * @return 1 if mouseButtonPressed is 6, 0 else
     * 
     * if there are other values possible for mouseButtonPressed
     * the method returns the following:
     * 0-3  -> 0
     * 4-7  -> 1
     * 8-11 -> 2
     * 12+  -> 3
     */
    public static int checkForPressedButton2(int mouseButtonPressed)
    {
        return mouseButtonPressed >> 2 & 3;
    }
    
    /**
     * This is a renamed version of the method 
     * func_94528_d in net.minecraft.inventory.Container.java
     * this is not needed but i dont like non-sense method names
     * 
     * @param value
     * @return
     */
    public static boolean checkValue(int value)
    {
        return value == 0 || value == 1;
    }
    
    /**
     * This is a renamed version of the method
     * func_94527_a in net.minecraft.inventory.Container.java
     * this is not needed but i dont like non-sense method names
     * 
     * The method return a bool if a given itemstack fits into
     * a given slot, the bool input argument rules if the size of
     * the stack matters or not
     * 
     * @param slot is the target slot
     * @param itemStack is the itemstack which should fit into slot
     * @param sizeMatters rules if the size of itemstack matters
     * @return true if the stack fits
     */
    public static boolean stackFitsInSlot(Slot slot, ItemStack itemStack, boolean sizeMatters)
    {
        boolean flag1 = slot == null || !slot.getHasStack();

        if (slot != null && slot.getHasStack() && itemStack != null && itemStack.isItemEqual(slot.getStack()) && ItemStack.areItemStackTagsEqual(slot.getStack(), itemStack))
        {
            int i = sizeMatters ? 0 : itemStack.stackSize;
            flag1 |= slot.getStack().stackSize + i <= itemStack.getMaxStackSize();
        }

        return flag1;
    }
    
    /**
     * This is a renamed version of the method 
     * func_94525_a in net.minecraft.inventory.Container.java
     * this is not needed but i dont like non-sense method names
     * 
     * @param slotSet is the set of slots for the current distribution
     * @param stackSizeSelector is the number which is added to the current processed stack 
     * @param stackToResize is stack that will be placed in the processed slot
     * @param currentSlotStackSize is the size of the itemstack in the current slot
     */
    public static void setSlotStack(Set slotSet, int stackSizeSelector, ItemStack stackToResize, int currentSlotStackSize)
    {
        switch (stackSizeSelector)
        {
            case 0:
                stackToResize.stackSize = MathHelper.floor_float((float)stackToResize.stackSize / (float)slotSet.size());
                break;
            case 1:
                stackToResize.stackSize = 1;
        }

        stackToResize.stackSize += currentSlotStackSize;
    }
    
    private void putItemToDistributeSlotSet(int mouseButton, EntityPlayer player)
    {
        if(_distributeSlotSet.size() == 1)
        {
            Slot slot = (Slot)_distributeSlotSet.iterator().next();
            
            if(!isPlayerSlot(slot.slotNumber))
            {
                if(slot.getStack() == null)
                    putItemToEmptySlot(slot, _pressedKeyInRange, player);
                else
                    putItemToNonEmptySlot(slot, _pressedKeyInRange, player);
                
                return;
            }
        }
        
        InventoryPlayer inventoryplayer = player.inventory;
        ItemStack playerItemStack = inventoryplayer.getItemStack().copy();        
        int size = inventoryplayer.getItemStack().stackSize;
        
        Iterator iterator = _distributeSlotSet.iterator();
        
        while (iterator.hasNext())
        {
            Slot currentSlotOfSet = (Slot)iterator.next();
            
            if(!isPlayerSlot(currentSlotOfSet.slotNumber))
                continue;
            
            if (currentSlotOfSet != null
                    && stackFitsInSlot(currentSlotOfSet, inventoryplayer.getItemStack(), true)
                    && currentSlotOfSet.isItemValid(inventoryplayer.getItemStack())
                    && inventoryplayer.getItemStack().stackSize >= _distributeSlotSet.size()
                    )
            {
                ItemStack targetSlotNewStack = playerItemStack.copy();
                int currentSlotStackSize = currentSlotOfSet.getHasStack() ? currentSlotOfSet.getStack().stackSize : 0;
                
                setSlotStack(_distributeSlotSet, _pressedKeyInRange, targetSlotNewStack, currentSlotStackSize);
                
                if (targetSlotNewStack.stackSize > targetSlotNewStack.getMaxStackSize())
                    targetSlotNewStack.stackSize = targetSlotNewStack.getMaxStackSize();

                if (targetSlotNewStack.stackSize > currentSlotOfSet.getSlotStackLimit())
                    targetSlotNewStack.stackSize = currentSlotOfSet.getSlotStackLimit();

                size -= targetSlotNewStack.stackSize - currentSlotStackSize;
                
                currentSlotOfSet.putStack(targetSlotNewStack);
            }
        }

        //set the stacksize of the picked up stack to the rest number
        playerItemStack.stackSize = size;

        if (playerItemStack.stackSize <= 0)
            playerItemStack = null;

        inventoryplayer.setItemStack(playerItemStack);
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
                _stall.setOwner(player);
                _stall.calculateQuantitiesInWarehouse();
                
                _world.markBlockForUpdate(_stall.xCoord, _stall.yCoord, _stall.zCoord);
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
                {
                    _stall.setOwner(null);
                    
                    _world.markBlockForUpdate(_stall.xCoord, _stall.yCoord, _stall.zCoord);
                }
                
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
        
        int goodSlotIndex = slot.getSlotIndex();
        int priceSlotIndex = getPriceSlotIndex(goodSlotIndex);
        
        if(priceSlotIndex < 0)
            return;
        
        InventoryPlayer inventoryplayer = player.inventory;
        ItemStack playerItemStack = inventoryplayer.getItemStack();
        ItemStack goodItemStack = slot.getStack();
        ItemStack payItemStack = _stall.getStackInSlot(priceSlotIndex);
        
        if (playerItemStack == null)
        {
            if(!preparePayAndTrade(goodSlotIndex, goodItemStack, payItemStack, player))
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
            || !preparePayAndTrade(goodSlotIndex, goodItemStack, payItemStack, player)
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
    
    private boolean preparePayAndTrade(int goodSlotIndex, ItemStack goodItemStack, ItemStack payItemStack, EntityPlayer player)
    {
        if(!preparePay(payItemStack, player.inventory))
        {
            player.addChatComponentMessage(new ChatComponentTranslation("gui.Stall.Message.NoPays", new Object[0]));
            return false;
        }
        
        PrepareTradeResult result = _stall.prepareTrade(goodSlotIndex, goodItemStack, payItemStack);
        
        if(result == PrepareTradeResult.Success)
            return true;
        
        if(result == PrepareTradeResult.NoGoods)
            player.addChatComponentMessage(new ChatComponentTranslation("gui.Stall.Message.NoGoods", new Object[0]));
        else
            player.addChatComponentMessage(new ChatComponentTranslation("gui.Stall.Message.NoPaysSpace", new Object[0]));

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
    
    private void confirmTransferGoods(ItemStack itemStack, InventoryPlayer inventoryPlayer, ArrayList<Integer> slotIndexes)
    {
        IInventory inventory = (IInventory)inventoryPlayer;
        int requiredQuantity = ItemHelper.getItemStackQuantity(itemStack);
        int maxStackQuantity = ItemHelper.getItemStackMaxQuantity(itemStack, inventory);
        
        for(int i = 0; i < slotIndexes.size(); i++)
        {
            int slotIndex = slotIndexes.get(i);
            ItemStack invItemStack = inventory.getStackInSlot(slotIndex);
            
            if(invItemStack == null)
            {
                invItemStack = itemStack.copy();
                
                ItemHelper.setStackQuantity(invItemStack, requiredQuantity);
                
                inventory.setInventorySlotContents(slotIndex, invItemStack);
                
                requiredQuantity = 0;
            }
            else
            {
                int invQuantity = ItemHelper.getItemStackQuantity(invItemStack);
                int quantity = Math.min(requiredQuantity, maxStackQuantity - invQuantity);
                
                ItemHelper.increaseStackQuantity(invItemStack, quantity);
                
                requiredQuantity -= quantity;
            }
        }
    }
    
    private boolean prepareTransferGoods(ItemStack itemStack, InventoryPlayer inventoryPlayer, ArrayList<Integer> slotIndexes)
    {
        int requiredQuantity = ItemHelper.getItemStackQuantity(itemStack);
        
        int quantity = searchTransferGoods_NonEmptySlots(itemStack, requiredQuantity, inventoryPlayer, slotIndexes);
        quantity = searchTransferGoods_emptySlots(itemStack, quantity, inventoryPlayer, slotIndexes);

        return quantity == 0;
    }
    
    private int searchTransferGoods_NonEmptySlots(ItemStack itemStack, int quantity, InventoryPlayer inventoryPlayer, ArrayList<Integer> slotIndexes)
    {
        IInventory inventory = (IInventory)inventoryPlayer;
        int maxStackQuantity = ItemHelper.getItemStackMaxQuantity(itemStack, inventory);
        
        for(int i = 0; i < inventory.getSizeInventory() && quantity > 0; i++)
        {
            ItemStack invItemStack = inventory.getStackInSlot(i);
            
            if(invItemStack == null || !ItemHelper.areItemEquals(itemStack, invItemStack))
                continue;
            
            int invQuantity = ItemHelper.getItemStackQuantity(invItemStack);
            
            if(invQuantity >= maxStackQuantity)
                continue;
            
            int preparedQuantity = maxStackQuantity - invQuantity;
            
            if(preparedQuantity > quantity)
                preparedQuantity = quantity;                

            slotIndexes.add(i);
            
            quantity -= preparedQuantity;
        }
        
        return quantity;
    }
    
    private int searchTransferGoods_emptySlots(ItemStack itemStack, int quantity, InventoryPlayer inventoryPlayer, ArrayList<Integer> slotIndexes)
    {
        IInventory inventory = (IInventory)inventoryPlayer;
        
        for(int i = 0; i < inventory.getSizeInventory() && quantity > 0; i++)
        {
            ItemStack invItemStack = inventory.getStackInSlot(i);
            
            if(invItemStack != null)
                continue;
            
            slotIndexes.add(i);
            
            quantity = 0;
        }
        
        return quantity;
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

    private int getBookSlotIndex()
    {
        return TileEntityStall.ItemCount - 1; 
    }

    public boolean isGoodsSlot(int slotNumber)
    {
        return slotNumber < 2 * TileEntityStall.PriceCount && slotNumber % 2 == 1; 
    }
 }