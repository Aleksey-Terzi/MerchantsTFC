package com.aleksey.merchants.Blocks.Devices;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.aleksey.merchants.MerchantsMod;
import com.aleksey.merchants.Core.BlockList;
import com.aleksey.merchants.Core.MerchantsTabs;
import com.aleksey.merchants.Handlers.GuiHandler;
import com.aleksey.merchants.TileEntities.TileEntityWarehouse;
import com.bioxx.tfc.Blocks.BlockTerraContainer;
import com.bioxx.tfc.api.TFCBlocks;
import com.bioxx.tfc.api.Constant.Global;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockWarehouse extends BlockTerraContainer
{
    private int _startWoodIndex;
    public int getStartWoodIndex()
    {
        return _startWoodIndex;
    }
    
    public BlockWarehouse(int startWoodIndex)
    {
        super(Material.wood);
        this.setCreativeTab(MerchantsTabs.MainTab);
        this.setBlockBounds(0, 0, 0, 1, 1, 1);
        
        _startWoodIndex = startWoodIndex;
    }

    @Override
    public IIcon getIcon(int side, int meta)
    {
        return _startWoodIndex == 0
            ? TFCBlocks.planks.getIcon(side, meta)
            : TFCBlocks.planks2.getIcon(side, meta);
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
    public int damageDropped(int meta)
    {
        return meta;
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
    public int getRenderType()
    {
        return BlockList.WarehouseRenderId;
    }

    @Override
    public void onBlockPreDestroy(World world, int x, int y, int z, int meta) 
    {
        if (world.isRemote)
            return;
        
        EntityItem ei = new EntityItem(world, x, y, z, new ItemStack(Item.getItemFromBlock(this), 1, meta));
        world.spawnEntityInWorld(ei);
    }

    /**
     * Called when the block is placed in the world.
     */
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack is)
    {
        super.onBlockPlacedBy(world, x, y, z, player, is);
        
        TileEntity te = world.getTileEntity(x, y, z);
        
        if (te == null || !(te instanceof TileEntityWarehouse))
            return;

        TileEntityWarehouse warehouse = (TileEntityWarehouse)te;

        warehouse.initKey();
        
        world.setBlockMetadataWithNotify(x, y, z, is.getItemDamage(), 2);
        world.markBlockForUpdate(x, y, z);
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    @Override
    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4)
    {
        return true;
    }

    @Override
    public boolean canDropFromExplosion(Explosion exp)
    {

        return true;
    }

    @Override
    protected void dropBlockAsItem(World par1World, int par2, int par3, int par4, ItemStack par5ItemStack)
    {
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

        if(te == null || !(te instanceof TileEntityWarehouse))
            return false;
        
        TileEntityWarehouse warehouse = (TileEntityWarehouse)te;
        
        player.openGui(MerchantsMod.instance, GuiHandler.GuiWarehouse, world, x, y, z);

        return true;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess par1iBlockAccess, int par2, int par3, int par4, int par5)
    {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2)
    {
        return new TileEntityWarehouse();
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
}