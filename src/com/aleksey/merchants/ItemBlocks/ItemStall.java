package com.aleksey.merchants.ItemBlocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.bioxx.tfc.Items.ItemTerra;
import com.bioxx.tfc.api.Enums.EnumItemReach;
import com.bioxx.tfc.api.Enums.EnumSize;
import com.bioxx.tfc.api.Enums.EnumWeight;
import com.bioxx.tfc.api.Interfaces.ISize;

public class ItemStall extends ItemBlock implements ISize
{
    public ItemStall(Block block)
    {
        super(block);
    }
    
    @Override
    public void addInformation(ItemStack is, EntityPlayer player, List arraylist, boolean flag)
    {
        ItemTerra.addSizeInformation(is, arraylist);
        readFromItemNBT(is.getTagCompound(), arraylist);
    }
    
    private void readFromItemNBT(NBTTagCompound nbt, List arraylist)
    {
        if(nbt == null)
            return;

        if(!nbt.hasKey("Items"))
            return;

        NBTTagList itemList = nbt.getTagList("Items", 10);
        
        if(itemList == null)
            return;
        
        int itemCount = itemList.tagCount();
        
        if(itemCount > 0)
            arraylist.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("gui.Stall.HasPrices"));
    }
    
    @Override
    public int getItemStackLimit(ItemStack is)
    {
        return this.getSize(null).stackSize * getWeight(null).multiplier;
    }
    
    @Override
    public boolean canStack()
    {
        return true;
    }
    
    @Override
    public EnumSize getSize(ItemStack is)
    {
        return EnumSize.LARGE;
    }

    @Override
    public EnumWeight getWeight(ItemStack is)
    {
        return EnumWeight.HEAVY;
    }
    
    @Override
    public EnumItemReach getReach(ItemStack is)
    {
        return EnumItemReach.SHORT;
    }
}
