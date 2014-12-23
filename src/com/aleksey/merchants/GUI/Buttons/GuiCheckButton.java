package com.aleksey.merchants.GUI.Buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.bioxx.tfc.Core.TFC_Core;

public class GuiCheckButton extends GuiButton
{
    private static final int _indent = 2;
    private static final int _colorDefaultText = 0x555555;
    
    private int _textureSize;
    private int _textureY;
    private int _falseTextureX;
    private int _trueTextureX;    
    private ResourceLocation _texture;
    private boolean _checked;
    
    public boolean getChecked()
    {
        return _checked;
    }
    
    public void setChecked(boolean checked)
    {
        _checked = checked;
    }

    public void negateChecked()
    {
        _checked = !_checked;
    }
    
    public GuiCheckButton(
            int index,
            int xPos,
            int yPos,
            int width,
            int height,
            String text,
            int textureSize,
            int textureY,
            int falseTextureX,
            int trueTextureX,
            ResourceLocation texture
            )
    {
        super(index, xPos, yPos, width, height, text);
        
        _textureSize = textureSize;
        _textureY = textureY;
        _falseTextureX = falseTextureX;
        _trueTextureX = trueTextureX;
        _texture = texture;
        _checked = false;
    }

    @Override
    public void drawButton(Minecraft mc, int xPos, int yPos)
    {
        if (!this.visible)
            return;

        TFC_Core.bindTexture(_texture);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.field_146123_n = xPos >= this.xPosition && yPos >= this.yPosition && xPos < this.xPosition + this.width && yPos < this.yPosition + this.height;
        
        int textureX = _checked ? _trueTextureX: _falseTextureX;
        
        drawTexturedModalRect(this.xPosition, this.yPosition, textureX, _textureY, _textureSize, _textureSize);
        
        this.mouseDragged(mc, xPos, yPos);

        if(this.displayString != null && this.displayString.length() > 0)
            mc.fontRenderer.drawString(this.displayString, this.xPosition + _textureSize + _indent, this.yPosition, _colorDefaultText);
    }
}