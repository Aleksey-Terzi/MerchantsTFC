package com.aleksey.merchants.GUI.Buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

public class GuiLimitButton extends GuiButton
{
    private static final int _colorDefaultText = 0x555555;
    
    public void setText(String text, FontRenderer fontRenderer)
    {
        this.width = fontRenderer.getStringWidth(text);
        this.height = fontRenderer.FONT_HEIGHT;
        this.displayString = text;
    }
    
    public GuiLimitButton(int index, int xPos, int yPos)
    {
        super(index, xPos, yPos, 200, 50, "");
    }

    @Override
    public void drawButton(Minecraft mc, int xPos, int yPos)
    {
        if (!this.visible)
            return;

        this.mouseDragged(mc, xPos, yPos);

        if(this.displayString != null && this.displayString.length() > 0)
            mc.fontRenderer.drawString(this.displayString, this.xPosition, this.yPosition, _colorDefaultText);
    }
}
