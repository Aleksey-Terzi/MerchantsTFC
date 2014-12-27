package com.aleksey.merchants.Render.Blocks;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import com.aleksey.merchants.Blocks.Devices.BlockCustomAnvilDie;
import com.bioxx.tfc.TFCBlocks;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class RenderAnvilDie implements ISimpleBlockRenderingHandler
{
    public static final double VoxelSizeScaled = 0.0625;// 1/16
    
    private static final double _anvilDieHeight = 6 * VoxelSizeScaled;
    
    private static final Bound _logBound = new Bound(0, 0, 0, 1, 1 - _anvilDieHeight, 1);
    private static final Bound _anvilDieBound = new Bound(3 * VoxelSizeScaled, 1 - _anvilDieHeight, 3 * VoxelSizeScaled, 1 - 3 * VoxelSizeScaled, 1, 1 - 3 * VoxelSizeScaled);
    
    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        setBound(_logBound, renderer);
        renderInvBlock(((BlockCustomAnvilDie)block).getLogBlock(), metadata, renderer);
        
        setBound(_anvilDieBound, renderer);
        renderInvBlock(block, 0, renderer);
    }
    
    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        setBound(_logBound, renderer);
        renderer.renderStandardBlock(((BlockCustomAnvilDie)block).getLogBlock(), x, y, z);
        
        setBound(_anvilDieBound, renderer);
        renderer.renderStandardBlock(block, x, y, z);

        return false;
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