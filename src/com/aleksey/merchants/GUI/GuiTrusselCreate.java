package com.aleksey.merchants.GUI;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.aleksey.merchants.GUI.Buttons.GuiCheckButton;
import com.aleksey.merchants.Items.ItemTrussel;
import com.bioxx.tfc.TerraFirmaCraft;
import com.bioxx.tfc.Core.TFC_Core;
import com.bioxx.tfc.Handlers.Network.ItemNBTPacket;

public class GuiTrusselCreate extends GuiScreen
{
    private static final ResourceLocation _texture = new ResourceLocation("merchants", "textures/gui/gui_trussel.png");

    public static final int WindowWidth = 176;
    public static final int WindowHeight = 127;
    
    private static final int _titleX = 0;
    private static final int _titleY = 4;
    private static final int _nameLabelX = 6;
    private static final int _nameLabelY = 16;
    private static final int _nameTextFieldX = 36;
    private static final int _nameTextFieldY = 16;
    private static final int _nameTextFieldWidth = 134;
    private static final int _dieLabelX = 6;
    private static final int _dieLabelY = 38;
    private static final int _createButtonX = 35;
    private static final int _createButtonY = 101;
    private static final int _cancelButtonX = 87;
    private static final int _cancelButtonY = 101;
    
    private static final int _dieTextureY = 0;
    private static final int _dieNotSetTextureX = 178;
    private static final int _dieSetTextureX = 183;
    private static final int _diePixelSize = 5;
    
    private static final int _topLeftDiePixelX = 35;
    private static final int _topLeftDiePixelY = 38;
    private static final int _dieStride = 12;
    
    private static final int _weightBtnTextureY = 7;
    private static final int _weightBtnFalseTextureX = 178;
    private static final int _weightBtnTrueTextureX = 186;
    private static final int _weightBtnSize = 7;
    private static final int _weightBtnWidth = 66;
    
    private static final int _weightLabelX = 98;
    private static final int _weightLabelY = 38;
    private static final int _weightIndent = 3;

    private static final int[] _weights = { 1, 20, 50, 100, 200 };

    private static final int _buttonId_createButton = 0;
    private static final int _buttonId_cancelButton = 1;
    private static final int _buttonId_weightButton = 2;
    private static final int _buttonId_dieButton = _buttonId_weightButton + _weights.length;
    
    private static final int _colorDefaultText = 0x555555;

    private EntityPlayer _player;
    private World _world;
    private GuiTextField _nameTextField;
    private GuiButton _createButton;
    private GuiButton _cancelButton;
    private GuiCheckButton[] _weightButtons;
    private GuiCheckButton[] _dieButtons;

    public GuiTrusselCreate(InventoryPlayer inventory, World world)
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
        
        _createButton = new GuiButton(_buttonId_createButton, w + _createButtonX, h + _createButtonY, 50, 20, StatCollector.translateToLocal("gui.TrusselCreate.Create"));
        buttonList.add(_createButton);
        
        _cancelButton = new GuiButton(_buttonId_cancelButton, w + _cancelButtonX, h + _cancelButtonY, 50, 20, StatCollector.translateToLocal("gui.TrusselCreate.Cancel"));
        buttonList.add(_cancelButton);
        
        initDieMatrix(w, h);
        initWeightButtons(w, h);
    }
    
    private void initDieMatrix(int w, int h)
    {
        int index = 0;
        int y = h + _topLeftDiePixelY;
        
        _dieButtons = new GuiCheckButton[_dieStride * _dieStride];
                
        for(int row = 0; row < _dieStride; row++)
        {
            int x = w + _topLeftDiePixelX;
            
            for(int col = 0; col < _dieStride; col++)
            {
                GuiCheckButton dieButton = new GuiCheckButton(_buttonId_dieButton + index, x, y, _diePixelSize, _diePixelSize, "", _diePixelSize, _dieTextureY, _dieNotSetTextureX, _dieSetTextureX, _texture);
                
                _dieButtons[index] = dieButton;
                
                buttonList.add(dieButton);
                
                index++;
                
                x += _diePixelSize;
            }
            
            y += _diePixelSize;
        }
    }
    
    private void initWeightButtons(int w, int h)
    {
        int x = w + _weightLabelX;
        int y = h + _weightLabelY + this.fontRendererObj.FONT_HEIGHT + _weightIndent;
        int index = _buttonId_weightButton;
        
        _weightButtons = new GuiCheckButton[_weights.length];
        
        for(int i = 0; i < _weightButtons.length; i++)
        {
            String text = StatCollector.translateToLocal("gui.TrusselCreate.WeightChoice" + String.valueOf(i + 1));
            GuiCheckButton button = new GuiCheckButton(index, x, y, _weightBtnWidth, _weightBtnSize, text, _weightBtnSize, _weightBtnTextureY, _weightBtnFalseTextureX, _weightBtnTrueTextureX, _texture);
            
            _weightButtons[i] = button;
            
            buttonList.add(button);
            
            y += _weightBtnSize + _weightIndent;
            
            index++;
        }
        
        selectWeight(_weightButtons[_weightButtons.length - 1]);
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
        if(guibutton.id == _buttonId_createButton)
            createDie();
        else if(guibutton.id == _buttonId_cancelButton)
            Minecraft.getMinecraft().displayGuiScreen(null);
        else if(guibutton.id < _buttonId_dieButton)
            selectWeight((GuiCheckButton)guibutton);
        else
            ((GuiCheckButton)guibutton).negateChecked();
    }
    
    private void createDie()
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString(ItemTrussel.TagName_Key, createKey());
        tag.setString(ItemTrussel.TagName_Name, _nameTextField.getText());
        tag.setInteger(ItemTrussel.TagName_Weight, getSelectedWeight());
        tag.setByteArray(ItemTrussel.TagName_Die, getDieData());

        ItemNBTPacket packet = new ItemNBTPacket(tag);
        packet.addAcceptedTag(ItemTrussel.TagName_Key);
        packet.addAcceptedTag(ItemTrussel.TagName_Name);
        packet.addAcceptedTag(ItemTrussel.TagName_Weight);
        packet.addAcceptedTag(ItemTrussel.TagName_Die);

        TerraFirmaCraft.packetPipeline.sendToServer(packet);

        Minecraft.getMinecraft().displayGuiScreen(null);
    }
    
    private void selectWeight(GuiCheckButton btn)
    {
        int buttonIndex = btn.id - _buttonId_weightButton;
        
        for(int i = 0; i < _weightButtons.length; i++)
            _weightButtons[i].setChecked(buttonIndex == i);
    }
    
    private String createKey()
    {
        return UUID.randomUUID().toString();
    }
    
    private int getSelectedWeight()
    {
        for(int i = 0; i < _weightButtons.length; i++)
        {
            if(_weightButtons[i].getChecked())
                return _weights[i];
        }
        
        return 0;
    }
    
    private byte[] getDieData()
    {
        int dataLen = _dieButtons.length / 8;
        
        if((_dieButtons.length % 8) != 0)
            dataLen++;
        
        byte[] data = new byte[dataLen];
        int mask = 1;
        
        for(int i = 0; i < _dieButtons.length; i++)
        {
            GuiCheckButton button = _dieButtons[i];
            int dataIndex = i / 8;
            byte dataByte = data[dataIndex];
            int bitIndex = i % 8;
            
            if(bitIndex == 0)
                dataByte = 0;
            
            if(button.getChecked())
                dataByte |= (byte)(mask << bitIndex);
        }
        
        return data;
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
        
        drawCenteredString(StatCollector.translateToLocal("gui.TrusselCreate.Title"), w + _titleX, h + _titleY, WindowWidth, _colorDefaultText);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.TrusselCreate.Name") + ":", w + _nameLabelX, h + nameLabelY, _colorDefaultText);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.TrusselCreate.Die") + ":", w + _dieLabelX, h + _dieLabelY, _colorDefaultText);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.TrusselCreate.Weight") + ":", w + _weightLabelX, h + _weightLabelY, _colorDefaultText);

        _nameTextField.drawTextBox();
        
        _createButton.enabled = _nameTextField.getText().length() > 0;
        
        super.drawScreen(par1, par2, par3);
    }
    
    private void drawCenteredString(String s, int x, int y, int columnWidth, int color)
    {
        int offset = (columnWidth - this.fontRendererObj.getStringWidth(s)) / 2;
        
        this.fontRendererObj.drawString(s, x + offset, y, color);
    }
}