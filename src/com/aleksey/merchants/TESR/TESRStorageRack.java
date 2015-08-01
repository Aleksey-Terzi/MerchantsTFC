package com.aleksey.merchants.TESR;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import com.aleksey.merchants.Render.Blocks.RenderStall;
import com.aleksey.merchants.TileEntities.TileEntityStorageRack;
import com.bioxx.tfc.Items.ItemBlocks.ItemAnvil;
import com.bioxx.tfc.Items.ItemBlocks.ItemWoodDoor;
import com.bioxx.tfc.Render.TESR.TESRBase;

public class TESRStorageRack extends TESRBase
{
    public TESRStorageRack()
    {
    }
    
    public void renderAt(TileEntityStorageRack te, double x, double y, double z, float f)
    {
        if (te.getWorldObj() == null || !RenderManager.instance.options.fancyGraphics)
            return;
        
        ItemStack itemStack = te.getStackInSlot(0);
        
        if(itemStack == null)
            return;
        
        EntityItem customitem = new EntityItem(field_147501_a.field_147550_f); //tileEntityRenderer.worldObj
        customitem.hoverStart = 0f;
        
        float blockScale = itemStack.getItem() instanceof ItemWoodDoor ? 1f: 2f;
        float itemY = (float)y + (float)(RenderStall.VoxelSizeScaled * 3);

        GL11.glPushMatrix(); //start
        
        if(itemStack.getItem() instanceof ItemAnvil)
        {
            GL11.glTranslatef((float)x + 0.25f, itemY - 0.25f, (float)z + 0.25F);
        }
        else
        {
            float timeD = (float) (360.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);

            GL11.glTranslatef((float)x + 0.5F, itemY, (float)z + 0.5F);
            GL11.glRotatef(timeD, 0.0F, 1.0F, 0.0F);
        }
        
        GL11.glScalef(blockScale, blockScale, blockScale);
        customitem.setEntityItemStack(itemStack);
        itemRenderer.doRender(customitem, 0, 0, 0, 0, 0);
        GL11.glPopMatrix(); //end
    }

    @Override
    public void renderTileEntityAt(TileEntity par1TileEntity, double x, double y, double z, float f)
    {
        this.renderAt((TileEntityStorageRack)par1TileEntity, x, y, z, f);
    }
}
