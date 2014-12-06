package com.aleksey.merchants.Helpers;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.bioxx.tfc.api.Food;
import com.bioxx.tfc.api.Interfaces.IFood;

public class ItemHelper
{
    public static final boolean areItemEquals(ItemStack itemStack1, ItemStack itemStack2)
    {
        if(itemStack1.getItem() != itemStack2.getItem() || itemStack1.getItemDamage() != itemStack2.getItemDamage())
            return false;

        if(itemStack1.getItem() instanceof IFood)
            return Food.areEqual(itemStack1, itemStack2);
        
        return ItemStack.areItemStackTagsEqual(itemStack1, itemStack2); 
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
}
