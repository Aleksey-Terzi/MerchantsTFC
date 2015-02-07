package com.aleksey.merchants.GUI;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;

import com.aleksey.merchants.Containers.ContainerStallLimit;
import com.aleksey.merchants.TileEntities.TileEntityStall;
import com.bioxx.tfc.Core.Player.PlayerInventory;
import com.bioxx.tfc.GUI.GuiContainerTFC;

public class GuiStallLimit extends GuiContainerTFC
{
    private static final ResourceLocation _texture = new ResourceLocation("merchants", "textures/gui/gui_stall_limit.png");
    private static final RenderItem _itemRenderer = new RenderItem();
    
    public static final int SlotSize = 18;
    public static final int SlotY = 17;
    public static final int PriceSlotX = 58;
    public static final int GoodSlotX = 102;
    public static final int WindowWidth = 176;
    public static final int WindowHeight = 99;
    
    private static final int _titleX = 0;
    private static final int _titleY = 4;
    private static final int _limitLabelX = 6;
    private static final int _limitLabelY = 43;
    private static final int _limitLabelWidth = 47;

    private static final int _limitTextFieldX = 58;
    private static final int _limitTextFieldY = 38;
    private static final int _limitTextFieldWidth = 60;

    private static final int _buttonY = 69;
    private static final int _applyButtonX = 37;
    private static final int _cancelButtonX = 89;
    
    private static final int _buttonId_applyButton = 0;
    private static final int _buttonId_cancelButton = 1;
    
    private static final int _colorDefaultText = 0x555555;

    private TileEntityStall _stall;
    private int _priceSlotIndex;
    private int _goodSlotIndex;
    private GuiTextField _limitTextField;

    public GuiStallLimit(InventoryPlayer inventoryplayer, TileEntityStall stall, World world, int x, int y, int z)
    {
        super(new ContainerStallLimit(inventoryplayer, stall, world, x, y, z), WindowWidth, WindowHeight - 1);

        _stall = stall;
        _priceSlotIndex = stall.getActivePriceSlotIndex();
        _goodSlotIndex = stall.getActiveGoodSlotIndex();
    }
    
    @Override
    public void updateScreen()
    {
        _limitTextField.updateCursorCounter();
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
        
        _limitTextField = new GuiTextField(fontRendererObj, guiLeft + _limitTextFieldX, guiTop + _limitTextFieldY, _limitTextFieldWidth, 20);
        _limitTextField.setFocused(true);
        
        int limit = _stall.getLimitByGoodSlotIndex(_goodSlotIndex);
        
        if(limit > 0)
            _limitTextField.setText(String.valueOf(limit));
        
        Keyboard.enableRepeatEvents(true);
        
        this.buttonList.add(new GuiButton(_buttonId_applyButton, guiLeft + _applyButtonX, guiTop + _buttonY, 50, 20, StatCollector.translateToLocal("gui.StallLimit.Apply")));
        this.buttonList.add(new GuiButton(_buttonId_cancelButton, guiLeft + _cancelButtonX, guiTop + _buttonY, 50, 20, StatCollector.translateToLocal("gui.StallLimit.Cancel")));
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        
        _limitTextField.mouseClicked(par1, par2, par3);
    }

    @Override
    protected void keyTyped(char key, int par2)
    {
        if(key >= '0' && key <= '9'
            || key == '\u0008'//Backspace
            || key == '\u007F'//Delete
            )
        {
            _limitTextField.textboxKeyTyped(key, par2);
        }
        else if(key == 13)
            applyLimit();
        else if(key == 27)
            _stall.actionSetLimit(_goodSlotIndex, null);
    }
    
    @Override
    protected void actionPerformed(GuiButton guibutton)
    {
        switch(guibutton.id)
        {
            case  _buttonId_applyButton:
                applyLimit();
                break;
            case  _buttonId_cancelButton:
                _stall.actionSetLimit(_goodSlotIndex, null);
                break;
        }
    }
    
    private void applyLimit()
    {
        String limitText = _limitTextField.getText();
        int limit;
        
        try
        {
            limit = limitText == null || limitText.length() == 0 ? 0: Integer.parseInt(limitText);
        }
        catch(NumberFormatException ex)
        {
            limit = 0;
        }
        
        _stall.actionSetLimit(_goodSlotIndex, limit);
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        bindTexture(_texture);
        
        int w = (width - xSize) / 2;
        int h = (height - ySize) / 2;
        int v = 0;

        drawTexturedModalRect(w, h, 0, v, xSize, ySize);
        
        drawCenteredString(StatCollector.translateToLocal("gui.StallLimit.Title"), w + _titleX, h + _titleY, WindowWidth, _colorDefaultText);
        drawRightAlignedString(StatCollector.translateToLocal("gui.StallLimit.Limit"), w + _limitLabelX, h + _limitLabelY, _limitLabelWidth, _colorDefaultText);
        
        _limitTextField.drawTextBox();
        
        PlayerInventory.drawInventory(this, width, height, ySize - PlayerInventory.invYSize);
    }
    
    private void drawCenteredString(String s, int x, int y, int columnWidth, int color)
    {
        int offset = (columnWidth - this.fontRendererObj.getStringWidth(s)) / 2;
        
        fontRendererObj.drawString(s, x + offset, y, color);
    }
    
    private void drawRightAlignedString(String s, int x, int y, int columnWidth, int color)
    {
        int offset = columnWidth - this.fontRendererObj.getStringWidth(s);
        
        fontRendererObj.drawString(s, x + offset, y, color);
    }
}
