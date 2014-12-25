package com.aleksey.merchants.Items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;

import com.bioxx.tfc.Core.TFCTabs;
import com.bioxx.tfc.Items.ItemTerra;
import com.bioxx.tfc.api.Enums.EnumSize;
import com.bioxx.tfc.api.Enums.EnumWeight;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemFlan extends ItemTerra
{
    public ItemFlan()
    {
        super();
        
        setMaxDamage(0);
        setCreativeTab(TFCTabs.TFCMisc);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister registerer)
    {
        this.itemIcon = registerer.registerIcon("merchants:Flan");
    }
    
    @Override
    public EnumSize getSize(ItemStack is)
    {
        return EnumSize.SMALL;
    }

    @Override
    public EnumWeight getWeight(ItemStack is)
    {
        return EnumWeight.MEDIUM;
    }
    
    @Override
    public boolean canStack()
    {
      return true;
    }
}