package com.aleksey.merchants.GUI;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.aleksey.merchants.GUI.Buttons.GuiDieButton;
import com.bioxx.tfc.Core.TFC_Core;

public class GuiTrusselCore extends GuiScreen
{
    private static final ResourceLocation _texture = new ResourceLocation("merchants", "textures/gui/gui_trussel_core.png");

    public static final int WindowWidth = 176;
    public static final int WindowHeight = 127;
    
    private static final int _titleX = 0;
    private static final int _titleY = 4;
    private static final int _nameLabelX = 6;
    private static final int _nameLabelY = 16;
    private static final int _nameTextFieldX = 43;
    private static final int _nameTextFieldY = 16;
    private static final int _nameTextFieldWidth = 124;
    private static final int _dieLabelX = 6;
    private static final int _dieLabelY = 38;
    private static final int _createButtonX = 42;
    private static final int _createButtonY = 101;
    private static final int _cancelButtonX = 94;
    private static final int _cancelButtonY = 101;
    
    private static final int _topLeftDiePixelX = 42;
    private static final int _topLeftDiePixelY = 38;
    private static final int _diePixelSize = 5;
    private static final int _dieStride = 12;
    
    private static final int _buttonId_createButton = 0;
    private static final int _buttonId_cancelButton = 1;
    private static final int _buttonId_dieButton = 2;
    
    private static final int _colorDefaultText = 0x555555;

    private EntityPlayer _player;
    private World _world;
    private GuiTextField _nameTextField;
    private GuiButton _createButton;
    private GuiButton _cancelButton;

    public GuiTrusselCore(InventoryPlayer inventory, World world)
    {
        _player = inventory.player;
        _world = world;
    }

    @Override
    public void updateScreen()
    {
        _nameTextField.updateCursorCounter();
    }

    @Override
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        
        int w = (this.width - WindowWidth) / 2;
        int h = (this.height - WindowHeight) / 2;

        _nameTextField = new GuiTextField(fontRendererObj, w + _nameTextFieldX, h + _nameTextFieldY, _nameTextFieldWidth, 20);
        _nameTextField.setFocused(true);
        
        Keyboard.enableRepeatEvents(true);
        
        _createButton = new GuiButton(_buttonId_createButton, w + _createButtonX, h + _createButtonY, 50, 20, StatCollector.translateToLocal("gui.TrusselCore.Create"));
        buttonList.add(_createButton);
        
        _cancelButton = new GuiButton(_buttonId_cancelButton, w + _cancelButtonX, h + _cancelButtonY, 50, 20, StatCollector.translateToLocal("gui.TrusselCore.Cancel"));
        buttonList.add(_cancelButton);
        
        initDieMatrix(w, h);
    }
    
    private void initDieMatrix(int w, int h)
    {
        int index = _buttonId_dieButton;
        int y = h + _topLeftDiePixelY;
                
        for(int row = 0; row < _dieStride; row++)
        {
            int x = w + _topLeftDiePixelX;
            
            for(int col = 0; col < _dieStride; col++)
            {
                GuiDieButton dieButton = new GuiDieButton(index++, x, y, _diePixelSize, _diePixelSize, _texture);
                buttonList.add(dieButton);
                
                x += _diePixelSize;
            }
            
            y += _diePixelSize;
        }
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        
        _nameTextField.mouseClicked(par1, par2, par3);
    }

    @Override
    protected void keyTyped(char par1, int par2)
    {
        _nameTextField.textboxKeyTyped(par1, par2);
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    protected void actionPerformed(GuiButton guibutton)
    {
        switch(guibutton.id)
        {
            case _buttonId_createButton:
                break;
            case _buttonId_cancelButton:
                Minecraft.getMinecraft().displayGuiScreen(null);
                break;
            default:
                ((GuiDieButton)guibutton).negateIsSet();
                break;
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        TFC_Core.bindTexture(_texture);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        int w = (this.width - WindowWidth) / 2;
        int h = (this.height - WindowHeight) / 2;

        drawTexturedModalRect(w, h, 0, 0, WindowWidth, WindowHeight);
        
        int nameLabelY = _nameLabelY + (20 - this.fontRendererObj.FONT_HEIGHT) / 2;
        
        drawCenteredString(StatCollector.translateToLocal("gui.TrusselCore.Title"), w + _titleX, h + _titleY, WindowWidth, _colorDefaultText);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.TrusselCore.Name") + ":", w + _nameLabelX, h + nameLabelY, _colorDefaultText);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.TrusselCore.Die") + ":", w + _dieLabelX, h + _dieLabelY, _colorDefaultText);

        _nameTextField.drawTextBox();

        super.drawScreen(par1, par2, par3);
    }
    
    private void drawCenteredString(String s, int x, int y, int columnWidth, int color)
    {
        int offset = (columnWidth - this.fontRendererObj.getStringWidth(s)) / 2;
        
        this.fontRendererObj.drawString(s, x + offset, y, color);
    }
}
