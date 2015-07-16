package com.aleksey.merchants.Render.Blocks;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import com.aleksey.merchants.TileEntities.TileEntityStall;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class RenderStall implements ISimpleBlockRenderingHandler
{
    public static final double VoxelSizeScaled = 0.0625;// 1/16
    
    private static final Bound[] _caseBounds = new Bound[] {
        new Bound(0, 0, 0, 1, VoxelSizeScaled, 1),//bottom
        new Bound(0, VoxelSizeScaled, VoxelSizeScaled, VoxelSizeScaled, 10 * VoxelSizeScaled, 1 - VoxelSizeScaled),//left
        new Bound(0, VoxelSizeScaled, 0, 1, 10 * VoxelSizeScaled, VoxelSizeScaled),//back
        new Bound(1 - VoxelSizeScaled, VoxelSizeScaled, VoxelSizeScaled, 1, 10 * VoxelSizeScaled, 1 - VoxelSizeScaled),//right
        new Bound(0, VoxelSizeScaled, 1 - VoxelSizeScaled, 1, 10 * VoxelSizeScaled, 1),//forward
    };
    
    private static final Bound _topBound = new Bound(VoxelSizeScaled, 6 * VoxelSizeScaled, VoxelSizeScaled, 1 - VoxelSizeScaled, 7 * VoxelSizeScaled, 1 - VoxelSizeScaled);
    
    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        renderer.overrideBlockTexture = Block.getBlockFromName("planks").getIcon(0, 0);
        
        for(int i = 0; i < _caseBounds.length; i++)
        {
            setBound(_caseBounds[i], renderer);
            
            renderInvBlock(block, metadata, renderer);
        }
        
        renderer.clearOverrideBlockTexture();
        
        setBound(_topBound, renderer);
        
        renderInvBlock(block, 1, renderer);
    }
    
    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        renderer.overrideBlockTexture = Block.getBlockFromName("planks").getIcon(0, 0);
        
        for(int i = 0; i < _caseBounds.length; i++)
        {
            setBound(_caseBounds[i], renderer);
            
            renderer.renderStandardBlock(block, x, y, z);
        }
        
        renderer.clearOverrideBlockTexture();

        setBound(_topBound, renderer);
        
        renderer.renderStandardBlock(block, x, y, z);

        return true;
    }
    
    @Override
    public boolean shouldRender3DInInventory(int modelId)
    {
        return true;
    }
    
    @Override
    public int getRenderId()
    {
        return 0;
    }
    
    private static void renderInvBlock(Block block, int m, RenderBlocks renderer)
    {
        Tessellator var14 = Tessellator.instance;
        
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        var14.startDrawingQuads();
        var14.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, m));
        var14.draw();
        var14.startDrawingQuads();
        var14.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(1, m));
        var14.draw();
        var14.startDrawingQuads();
        var14.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(2, m));
        var14.draw();
        var14.startDrawingQuads();
        var14.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(3, m));
        var14.draw();
        var14.startDrawingQuads();
        var14.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(4, m));
        var14.draw();
        var14.startDrawingQuads();
        var14.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(5, m));
        var14.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }
    
    private static void setBound(Bound bound, RenderBlocks renderer)
    {
        renderer.setRenderBounds(bound.MinX, bound.MinY, bound.MinZ, bound.MaxX, bound.MaxY, bound.MaxZ);
    }
}
