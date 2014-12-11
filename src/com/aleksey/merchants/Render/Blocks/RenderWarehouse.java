package com.aleksey.merchants.Render.Blocks;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import com.aleksey.merchants.Core.PointF;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class RenderWarehouse implements ISimpleBlockRenderingHandler
{
    private static final double VoxelSizeScaled = 0.0625;// 1/16
    private static final double Thickness = 2 * VoxelSizeScaled;
    
    private static final BoundTransform[] _caseBounds = new BoundTransform[] {
        new BoundTransform(new Bound(0, 0, 0, Thickness, 1, Thickness),
                new PointF[] {
                    new PointF(0, 0, 1 - Thickness),
                    new PointF(1 - Thickness, 0, 0),
                    new PointF(0, 0, Thickness - 1),
                }
            ),
        new BoundTransform(new Bound(Thickness, 0, 0, 1 - Thickness, Thickness, Thickness),
                new PointF[] {
                    new PointF(0, 0, 1 - Thickness),
                    new PointF(0, 1 - Thickness, 0),
                    new PointF(0, 0, Thickness - 1),
                }
            ),
        new BoundTransform(new Bound(0, 0, Thickness, Thickness, Thickness, 1 - Thickness),
                new PointF[] {
                    new PointF(1 - Thickness, 0, 0),
                    new PointF(0, 1 - Thickness, 0),
                    new PointF(Thickness - 1, 0, 0),
                }
            ),
        new BoundTransform(new Bound(Thickness / 2, Thickness, Thickness, Thickness, 1 - Thickness, 1 - Thickness),
                new PointF[] {
                    new PointF(1 - 2 * Thickness + Thickness / 2, 0, 0),
                }
            ),
        new BoundTransform(new Bound(Thickness, Thickness, Thickness / 2, 1 - Thickness, 1 - Thickness, Thickness),
                new PointF[] {
                    new PointF(0, 0, 1 - 2 * Thickness + Thickness / 2),
                }
            ),
        new BoundTransform(new Bound(Thickness, Thickness / 2, Thickness, 1 - Thickness, 1 - Thickness + Thickness / 2, 1 - Thickness), null),
    };
    
    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        for(int i = 0; i < _caseBounds.length; i++)
        {
            BoundTransform boundTransform = _caseBounds[i];
            Bound bound = boundTransform.Bound.copy();
            
            setBound(bound, renderer);
            renderInvBlock(block, metadata, renderer);

            if(boundTransform.Transforms == null)
                continue;
            
            for(int j = 0; j < boundTransform.Transforms.length; j++)
            {
                PointF p = boundTransform.Transforms[j];
                
                bound.MinX += p.X;
                bound.MinY += p.Y;
                bound.MinZ += p.Z;
                bound.MaxX += p.X;
                bound.MaxY += p.Y;
                bound.MaxZ += p.Z;
                
                setBound(bound, renderer);
                renderInvBlock(block, metadata, renderer);
            }
        }
    }
    
    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        for(int i = 0; i < _caseBounds.length; i++)
        {
            BoundTransform boundTransform = _caseBounds[i];
            Bound bound = boundTransform.Bound.copy();
            
            setBound(bound, renderer);
            renderer.renderStandardBlock(block, x, y, z);

            if(boundTransform.Transforms == null)
                continue;
            
            for(int j = 0; j < boundTransform.Transforms.length; j++)
            {
                PointF p = boundTransform.Transforms[j];
                
                bound.MinX += p.X;
                bound.MinY += p.Y;
                bound.MinZ += p.Z;
                bound.MaxX += p.X;
                bound.MaxY += p.Y;
                bound.MaxZ += p.Z;
                
                setBound(bound, renderer);
                renderer.renderStandardBlock(block, x, y, z);;
            }
        }

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
