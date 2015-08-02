package com.aleksey.merchants.Core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MerchantsTabs extends CreativeTabs
{
    public static MerchantsTabs MainTab = new MerchantsTabs("Merchants");
    
    private ItemStack _itemStack;

    public MerchantsTabs(String par2Str)
    {
        super(par2Str);
    }
    
    public MerchantsTabs(String par2Str, int icon)
    {
        super(par2Str);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Item getTabIconItem()
    {
        return _itemStack.getItem();
    }

    @Override
    public ItemStack getIconItemStack()
    {
        return _itemStack;
    }

    public void setTabIconItemStack(ItemStack itemStack)
    {
        _itemStack = itemStack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getTranslatedTabLabel()
    {
        return StatCollector.translateToLocal("itemGroup." + this.getTabLabel());
    }
}
