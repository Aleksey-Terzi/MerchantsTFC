package com.aleksey.merchants.Blocks.Devices;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.aleksey.merchants.MerchantsMod;
import com.aleksey.merchants.Core.BlockList;
import com.aleksey.merchants.Core.MerchantsTabs;
import com.aleksey.merchants.Handlers.GuiHandler;
import com.aleksey.merchants.TileEntities.TileEntityStorageRack;
import com.bioxx.tfc.Blocks.BlockTerraContainer;
import com.bioxx.tfc.api.TFCBlocks;
import com.bioxx.tfc.api.Constant.Global;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockStorageRack extends BlockTerraContainer
{
    private int _startWoodIndex;
    public int getStartWoodIndex()
    {
        return _startWoodIndex;
    }

    public BlockStorageRack(int startWoodIndex)
    {
        super(Material.wood);
        this.setCreativeTab(MerchantsTabs.MainTab);
        this.setBlockBounds(0, 0, 0, 1, 1, 1);
        this.setHardness(2.0F);
        this.setStepSound(Block.soundTypeWood);
        
        _startWoodIndex = startWoodIndex;
    }

    @Override
    public IIcon getIcon(int side, int meta)
    {
        return _startWoodIndex == 0
            ? TFCBlocks.Planks.getIcon(side, meta)
            : TFCBlocks.Planks2.getIcon(side, meta);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List list)
    {
        int len = _startWoodIndex == 0 ? 16: Global.WOOD_ALL.length - _startWoodIndex;
        
        for(int i = 0; i < len; i++)
            list.add(new ItemStack(this, 1, i));
    }

    @Override
    public int damageDropped(int i)
    {
        return i;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }
    
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        if(player.isSneaking())
            return false;
        
        if (world.isRemote)
        {
            world.markBlockForUpdate(x, y, z);
            return true;
        }

        TileEntity te = world.getTileEntity(x, y, z);

        if(te == null || !(te instanceof TileEntityStorageRack))
            return false;
        
        TileEntityStorageRack storageRack = (TileEntityStorageRack)te;
        
        player.openGui(MerchantsMod.instance, GuiHandler.GuiStorageRack, world, x, y, z);

        return true;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess par1iBlockAccess, int par2, int par3, int par4, int par5)
    {
        return true;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer)
    {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer)
    {
        return true;
    }

    @Override
    public int getRenderType()
    {
        return BlockList.StorageRackRenderId;
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileEntityStorageRack();
    }
}
