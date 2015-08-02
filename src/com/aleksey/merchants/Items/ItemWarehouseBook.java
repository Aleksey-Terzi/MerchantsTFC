package com.aleksey.merchants.Items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import com.aleksey.merchants.Core.WarehouseBookInfo;
import com.bioxx.tfc.Items.ItemTerra;
import com.bioxx.tfc.api.Enums.EnumSize;
import com.bioxx.tfc.api.Enums.EnumWeight;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemWarehouseBook extends ItemTerra
{
    public ItemWarehouseBook()
    {
        super();
        
        setMaxDamage(0);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister registerer)
    {
        this.itemIcon = registerer.registerIcon("merchants:WarehouseBook");
    }
    
    @Override
    public EnumSize getSize(ItemStack is)
    {
        return EnumSize.TINY;
    }

    @Override
    public EnumWeight getWeight(ItemStack is)
    {
        return EnumWeight.LIGHT;
    }
    
    @Override
    public boolean canStack()
    {
      return false;
    }
    
    @Override
    public void addInformation(ItemStack is, EntityPlayer player, List arraylist, boolean flag)
    {
        super.addInformation(is, player, arraylist, flag);
        
        WarehouseBookInfo info = WarehouseBookInfo.readFromNBT(is.getTagCompound());
        
        if(info != null)
        {
            arraylist.add(EnumChatFormatting.GOLD + "X: " + String.valueOf(info.X));
            arraylist.add(EnumChatFormatting.GOLD + "Y: " + String.valueOf(info.Y));
            arraylist.add(EnumChatFormatting.GOLD + "Z: " + String.valueOf(info.Z));
        }
    }
}