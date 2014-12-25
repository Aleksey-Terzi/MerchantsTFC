package com.aleksey.merchants.Items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.aleksey.merchants.MerchantsMod;
import com.aleksey.merchants.Handlers.GuiHandler;
import com.aleksey.merchants.Helpers.CoinHelper;
import com.bioxx.tfc.Core.TFCTabs;
import com.bioxx.tfc.Items.ItemTerra;
import com.bioxx.tfc.api.Enums.EnumSize;
import com.bioxx.tfc.api.Enums.EnumWeight;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemTrussel extends ItemTerra
{
    public static final String TagName_Key = "Key";
    public static final String TagName_Name = "Name";
    public static final String TagName_Weight = "Weight";
    public static final String TagName_Die = "Die";
    
    private IIcon _iconTrussel;
    private IIcon _iconTrusselWithDie;
    
    public ItemTrussel()
    {
        super();
        
        setMaxDamage(0);
        setCreativeTab(TFCTabs.TFCMisc);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister registerer)
    {
        _iconTrussel = registerer.registerIcon("merchants:Trussel");
        _iconTrusselWithDie = registerer.registerIcon("merchants:TrusselWithDie");
        
        this.itemIcon = _iconTrussel;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconIndex(ItemStack itemStack)
    {
        return itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey(TagName_Key)
                ? _iconTrusselWithDie
                : _iconTrussel;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if(!stack.hasTagCompound())
            stack.setTagCompound(new NBTTagCompound());
        
        if(stack.getTagCompound().hasKey(TagName_Key))
            player.openGui(MerchantsMod.instance, GuiHandler.GuiTrussel, player.worldObj, 0, 0, 0);
        else
            player.openGui(MerchantsMod.instance, GuiHandler.GuiTrusselCreate, player.worldObj, 0, 0, 0);

        return stack;
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
      return false;
    }
    
    @Override
    public void addInformation(ItemStack is, EntityPlayer player, List arraylist, boolean flag)
    {
        super.addInformation(is, player, arraylist, flag);
        
        NBTTagCompound tag = is.getTagCompound();
        
        if(tag != null && tag.hasKey(TagName_Key))
        {
            int weight = tag.getInteger(TagName_Weight);
            String weightText = CoinHelper.getWeightText(weight);
            
            arraylist.add(EnumChatFormatting.GOLD + "Key: " + tag.getString(TagName_Key));
            arraylist.add(EnumChatFormatting.GOLD + tag.getString(TagName_Name) + " " + EnumChatFormatting.GRAY + weightText);
        }
    }
    
    public static String getTrusselName(ItemStack itemStack)
    {
        return itemStack.getTagCompound().getString(TagName_Name);
    }
    
    public static int getTrusselWeight(ItemStack itemStack)
    {
        return itemStack.getTagCompound().getInteger(TagName_Weight);
    }

    public static byte[] getTrusselDie(ItemStack itemStack)
    {
        return itemStack.getTagCompound().getByteArray(TagName_Die);
    }

    public static void copyDie(ItemStack srcStack, ItemStack dstStack)
    {
        if(!dstStack.hasTagCompound())
            dstStack.setTagCompound(new NBTTagCompound());
        
        NBTTagCompound srcTag = srcStack.getTagCompound();
        NBTTagCompound dstTag = dstStack.getTagCompound();
        
        dstTag.setString(TagName_Key, srcTag.getString(TagName_Key));
        dstTag.setString(TagName_Name, srcTag.getString(TagName_Name));
        dstTag.setInteger(TagName_Weight, srcTag.getInteger(TagName_Weight));
        dstTag.setByteArray(TagName_Die, srcTag.getByteArray(TagName_Die));
    }
}