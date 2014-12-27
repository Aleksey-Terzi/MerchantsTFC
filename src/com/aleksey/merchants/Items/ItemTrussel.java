package com.aleksey.merchants.Items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.aleksey.merchants.MerchantsMod;
import com.aleksey.merchants.Core.Constants;
import com.aleksey.merchants.Core.DieInfo;
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
    private IIcon[] _trusselIcons;
    private IIcon[] _trusselWithDieIcons;
    
    public ItemTrussel()
    {
        super();
        
        setMaxDamage(0);
        setCreativeTab(TFCTabs.TFCMisc);
        setHasSubtypes(true);
        
        MetaNames = new String[Constants.Dies.length];
        
        for(int i = 0; i < Constants.Dies.length; i++)
            MetaNames[i] = Constants.Dies[i].DieName;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister registerer)
    {
        _trusselIcons = new IIcon[MetaNames.length];
        _trusselWithDieIcons = new IIcon[MetaNames.length];

        for(int i = 0; i < MetaNames.length; i++)
        {
            _trusselIcons[i] = registerer.registerIcon("merchants:trussels/Trussel" + MetaNames[i]);
            _trusselWithDieIcons[i] = registerer.registerIcon("merchants:trussels/TrusselWithDie" + MetaNames[i]);
        }
        
        this.itemIcon = _trusselIcons[0];        
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconIndex(ItemStack itemStack)
    {
        return itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey(CoinHelper.TagName_Key)
                ? _trusselWithDieIcons[itemStack.getItemDamage()]
                : _trusselIcons[itemStack.getItemDamage()];
    }
    
    @Override
    public IIcon getIconFromDamage(int i)
    {
        return _trusselIcons[i];
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if(!stack.hasTagCompound())
            stack.setTagCompound(new NBTTagCompound());
        
        if(stack.getTagCompound().hasKey(CoinHelper.TagName_Key))
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
        
        if(tag != null && tag.hasKey(CoinHelper.TagName_Key))
        {
            int weight = CoinHelper.getCoinWeight(is);
            String weightText = CoinHelper.getWeightText(weight);
            
            //arraylist.add(EnumChatFormatting.GOLD + "Key: " + tag.getString(CoinHelper.TagName_Key));
            arraylist.add(EnumChatFormatting.GOLD + CoinHelper.getCoinName(is) + " " + EnumChatFormatting.GRAY + weightText);
        }
    }
}