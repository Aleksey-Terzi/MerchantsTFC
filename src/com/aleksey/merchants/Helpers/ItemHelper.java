package com.aleksey.merchants.Helpers;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.bioxx.tfc.TileEntities.TEIngotPile;
import com.bioxx.tfc.api.Food;
import com.bioxx.tfc.api.Interfaces.IFood;

public class ItemHelper
{
    public static final boolean areItemEquals(ItemStack itemStack1, ItemStack itemStack2)
    {
        if(itemStack1.getItem() != itemStack2.getItem() || itemStack1.getItemDamage() != itemStack2.getItemDamage())
            return false;

        return itemStack1.getItem() instanceof IFood
            ? Food.areEqual(itemStack1, itemStack2)
            : ItemStack.areItemStackTagsEqual(itemStack1, itemStack2); 
    }
    
    public static final String getItemKey(ItemStack itemStack)
    {
        Item item = itemStack.getItem();
        String key = String.valueOf(Item.getIdFromItem(item)) + ":" + String.valueOf(itemStack.getItemDamage());
        
        if(!(item instanceof IFood))
            return key;
        
        key += ":"
            + (Food.isBrined(itemStack) ? "1": "0")
            + (Food.isPickled(itemStack) ? "1": "0")
            + (Food.isCooked(itemStack) ? "1": "0")
            + (Food.isDried(itemStack) ? "1": "0")
            + (Food.isSmoked(itemStack) ? "1": "0")
            + (Food.isSalted(itemStack) ? "1": "0")
            ;
        
        return key;
    }
    
    public static final int getItemStackQuantity(ItemStack itemStack)
    {
        if(itemStack.getItem() instanceof IFood)
        {
            IFood food = (IFood)itemStack.getItem();
            float foodDecay = Math.max(food.getFoodDecay(itemStack), 0);
            int quantity = (int)(food.getFoodWeight(itemStack) - foodDecay);
            
            return quantity > 0 ? quantity: 0;
        }

        return itemStack.stackSize;
    }

    public static final int getItemStackMaxQuantity(ItemStack itemStack, IInventory inventory)
    {
        Item item = itemStack.getItem();
        
        if(item instanceof IFood)
            return (int)((IFood)itemStack.getItem()).getFoodMaxWeight(itemStack);
        
        if(inventory instanceof TEIngotPile)
            return inventory.getInventoryStackLimit();
            
        return Math.min(itemStack.getMaxStackSize(), inventory.getInventoryStackLimit());
    }
    
    public static final void increaseStackQuantity(ItemStack itemStack, int quantity)
    {
        if(itemStack.getItem() instanceof IFood)
        {
            IFood food = (IFood)itemStack.getItem();
            float newQuantity = food.getFoodWeight(itemStack) + quantity;
            
            Food.setWeight(itemStack, newQuantity);
        }
        else
            itemStack.stackSize += quantity;
    }
    
    public static final void setStackQuantity(ItemStack itemStack, int quantity)
    {
        if(itemStack.getItem() instanceof IFood)
            Food.setWeight(itemStack, quantity);
        else
            itemStack.stackSize = quantity;
    }
}
