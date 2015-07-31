package com.aleksey.merchants.TESR;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import com.aleksey.merchants.Render.Blocks.RenderStall;
import com.aleksey.merchants.TileEntities.TileEntityStall;
import com.bioxx.tfc.Render.TESR.TESRBase;

public class TESRStall extends TESRBase
{
	public TESRStall()
	{
	}
	
	public void renderAt(TileEntityStall te, double x, double y, double z, float f)
	{
		if (te.getWorldObj() == null || !RenderManager.instance.options.fancyGraphics)
			return;

		EntityItem customitem = new EntityItem(field_147501_a.field_147550_f); //tileEntityRenderer.worldObj
		customitem.hoverStart = 0f;
		
		float blockScale = 0.6F;
		float timeD = (float) (360.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);
		
		ItemStack good1 = te.getStackInSlot(1);
		ItemStack good2 = te.getStackInSlot(3);
		ItemStack good3 = te.getStackInSlot(5);
		ItemStack good4 = te.getStackInSlot(7);
		ItemStack good5 = te.getStackInSlot(9);
		
        float goodY = (float)y + (float)(RenderStall.VoxelSizeScaled * 9);

        if (good1 != null)
		{
			GL11.glPushMatrix(); //start
			GL11.glTranslatef((float)x + 0.25F, goodY, (float)z + 0.25F);
			GL11.glRotatef(timeD, 0.0F, 1.0F, 0.0F);
			GL11.glScalef(blockScale, blockScale, blockScale);
			customitem.setEntityItemStack(good1);
			itemRenderer.doRender(customitem, 0, 0, 0, 0, 0);
			GL11.glPopMatrix(); //end
		}
		if (good2 != null)
		{
			GL11.glPushMatrix(); //start
			GL11.glTranslatef((float)x + 0.75F, goodY, (float)z + 0.25F);
			GL11.glRotatef(timeD, 0.0F, 1.0F, 0.0F);
			GL11.glScalef(blockScale, blockScale, blockScale);
			customitem.setEntityItemStack(good2);
			itemRenderer.doRender(customitem, 0, 0, 0, 0, 0);
			GL11.glPopMatrix(); //end
		}
		if (good3 != null)
		{
			GL11.glPushMatrix(); //start
			GL11.glTranslatef((float)x + 0.5F, goodY, (float)z + 0.5F);
			GL11.glRotatef(timeD, 0.0F, 1.0F, 0.0F);
			GL11.glScalef(blockScale, blockScale, blockScale);
			customitem.setEntityItemStack(good3);
			itemRenderer.doRender(customitem, 0, 0, 0, 0, 0);
			GL11.glPopMatrix(); //end
		}
		if (good4 != null)
		{
			GL11.glPushMatrix(); //start
			GL11.glTranslatef((float)x + 0.25F, goodY, (float)z + 0.75F);
			GL11.glRotatef(timeD, 0.0F, 1.0F, 0.0F);
			GL11.glScalef(blockScale, blockScale, blockScale);
			customitem.setEntityItemStack(good4);
			itemRenderer.doRender(customitem, 0, 0, 0, 0, 0);
			GL11.glPopMatrix(); //end
		}
		if (good5 != null)
		{
			GL11.glPushMatrix(); //start
			GL11.glTranslatef((float)x + 0.75F, goodY, (float)z + 0.75F);
			GL11.glRotatef(timeD, 0.0F, 1.0F, 0.0F);
			GL11.glScalef(blockScale, blockScale, blockScale);
			customitem.setEntityItemStack(good5);
			itemRenderer.doRender(customitem, 0, 0, 0, 0, 0);
			GL11.glPopMatrix(); //end
		}
	}

	@Override
	public void renderTileEntityAt(TileEntity par1TileEntity, double x, double y, double z, float f)
	{
		this.renderAt((TileEntityStall)par1TileEntity, x, y, z, f);
	}
}