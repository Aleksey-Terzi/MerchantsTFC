package com.aleksey.merchants.ItemBlocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import com.aleksey.merchants.Blocks.Devices.BlockStorageRack;
import com.bioxx.tfc.Items.ItemTerra;
import com.bioxx.tfc.api.Constant.Global;
import com.bioxx.tfc.api.Enums.EnumItemReach;
import com.bioxx.tfc.api.Enums.EnumSize;
import com.bioxx.tfc.api.Enums.EnumWeight;
import com.bioxx.tfc.api.Interfaces.ISize;

public class ItemStorageRack extends ItemBlock implements ISize
{
    private int _startWoodIndex;
    
    public ItemStorageRack(Block block)
    {
        super(block);
        
        setHasSubtypes(true);
        
        _startWoodIndex = ((BlockStorageRack)block).getStartWoodIndex();
    }
    
    @Override
    public String getUnlocalizedName(ItemStack itemstack)
    {
        int meta = itemstack.getItemDamage();
        int len = _startWoodIndex == 0 ? 16: Global.WOOD_ALL.length - _startWoodIndex;
        
        if(meta < 0 || meta >= len)
            meta = 0;
        
        return getUnlocalizedName() + "." + Global.WOOD_ALL[meta + _startWoodIndex];
    }
    
    @Override
    public void addInformation(ItemStack is, EntityPlayer player, List arraylist, boolean flag)
    {
        ItemTerra.addSizeInformation(is, arraylist);
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
    public int getMetadata(int i)
    {
        return i;
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
