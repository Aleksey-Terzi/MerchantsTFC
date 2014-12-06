package com.aleksey.merchants.Helpers;

import net.minecraft.item.ItemStack;

public class ItemHelper
{
    public static final boolean areItemEquals(ItemStack itemStack1, ItemStack itemStack2)
    {
        return itemStack1.getItem() == itemStack2.getItem()
            && itemStack1.getItemDamage() == itemStack2.getItemDamage()
            //&& ItemStack.areItemStackTagsEqual(itemStack1, itemStack2)
            ;
    }
}
