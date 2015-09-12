package com.aleksey.merchants.Items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;

import com.aleksey.merchants.Core.Constants;
import com.aleksey.merchants.Core.MerchantsTabs;
import com.aleksey.merchants.Helpers.CoinHelper;
import com.aleksey.merchants.Render.Items.CoinItemRenderer;
import com.bioxx.tfc.Items.ItemTerra;
import com.bioxx.tfc.api.Enums.EnumSize;
import com.bioxx.tfc.api.Enums.EnumWeight;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCoin extends ItemTerra
{
    public ItemCoin()
    {
        super();
        
        setMaxDamage(0);
        setCreativeTab(MerchantsTabs.MainTab);
        setHasSubtypes(true);
        
        metaNames = new String[Constants.Coins.length];
        
        for(int i = 0; i < Constants.Coins.length; i++)
            metaNames[i] = Constants.Coins[i].CoinName;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister registerer)
    {
        metaIcons = new IIcon[metaNames.length];

        for(int i = 0; i < metaNames.length; i++)
            metaIcons[i] = registerer.registerIcon("merchants:coins/Coin" + metaNames[i]);
        
        this.itemIcon = metaIcons[0];
        
        MinecraftForgeClient.registerItemRenderer(this, new CoinItemRenderer());
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
      return true;
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