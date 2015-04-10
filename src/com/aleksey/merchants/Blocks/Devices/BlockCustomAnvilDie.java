package com.aleksey.merchants.Blocks.Devices;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.aleksey.merchants.MerchantsMod;
import com.aleksey.merchants.Core.BlockList;
import com.aleksey.merchants.Core.DieInfo;
import com.aleksey.merchants.Handlers.GuiHandler;
import com.aleksey.merchants.TileEntities.TileEntityAnvilDie;
import com.bioxx.tfc.api.TFCBlocks;
import com.bioxx.tfc.api.TFCItems;
import com.bioxx.tfc.Blocks.BlockTerraContainer;
import com.bioxx.tfc.Core.TFCTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCustomAnvilDie extends BlockTerraContainer
{
    private DieInfo _info;
    
    @SideOnly(Side.CLIENT)
    private IIcon _topIcon;
    
    @SideOnly(Side.CLIENT)
    private IIcon _sideIcon;
    
    public BlockCustomAnvilDie(DieInfo info)
    {
        super(Material.iron);
        
        _info = info;
        
        //this.setCreativeTab(TFCTabs.TFCDevices);
        this.setBlockBounds(0, 0, 0, 1, 1, 1);
        this.setHardness(4.0F);
        this.setResistance(10.0F);
        this.setStepSound(Block.soundTypeMetal);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register)
    {
        _topIcon = register.registerIcon("merchants:anvildies/AnvilDieTop" + _info.DieName);
        _sideIcon = register.registerIcon("merchants:anvildies/AnvilDieSide" + _info.DieName);
    }

    @Override
    public IIcon getIcon(int side, int meta)
    {
        return side <= 1 ? _topIcon: _sideIcon;
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
        return BlockList.AnvilDieRenderId;
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

        if(te == null || !(te instanceof TileEntityAnvilDie))
            return false;
        
        player.openGui(MerchantsMod.instance, GuiHandler.GuiAnvilDie, world, x, y, z);

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
        return new TileEntityAnvilDie();
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
    public void breakBlock(World world, int x, int y, int z, Block block, int metadata)
    {
        EntityItem ei = new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, new ItemStack(TFCItems.Logs, 1, getLogsMetadata(metadata)));
        ei.motionX = 0;
        ei.motionY = 0;
        ei.motionZ = 0;
        world.spawnEntityInWorld(ei);

        super.breakBlock(world, x, y, z, block, metadata);
    }
    
    protected int getLogsMetadata(int metadata)
    {
        return metadata;
    }
    
    public Block getLogBlock()
    {
        return TFCBlocks.WoodVert;
    }
}
