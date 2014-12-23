package com.aleksey.merchants.GUI.Buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.bioxx.tfc.Core.TFC_Core;

public class GuiDieButton extends GuiButton
{
    private static final int _textureY = 0;
    private static final int _notSetTextureX = 178;
    private static final int _setTextureX = 183;
    
    private ResourceLocation _texture;
    private boolean _isSet;
    
    public boolean getIsSet()
    {
        return _isSet;
    }
    
    public void negateIsSet()
    {
        _isSet = !_isSet;
    }
    
    public GuiDieButton(int index, int xPos, int yPos, int width, int height, ResourceLocation texture)
    {
        super(index, xPos, yPos, width, height, "");
        
        _texture = texture;
        _isSet = false;
    }

    @Override
    public void drawButton(Minecraft par1Minecraft, int xPos, int yPos)
    {
        if (!this.visible)
            return;

        TFC_Core.bindTexture(_texture);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.field_146123_n = xPos >= this.xPosition && yPos >= this.yPosition && xPos < this.xPosition + this.width && yPos < this.yPosition + this.height;
        
        int textureX = _isSet ? _setTextureX: _notSetTextureX;
        
        drawTexturedModalRect(this.xPosition, this.yPosition, textureX, _textureY, this.width, this.height);
        
        this.mouseDragged(par1Minecraft, xPos, yPos);
    }
}
