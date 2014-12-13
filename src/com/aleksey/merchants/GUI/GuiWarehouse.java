package com.aleksey.merchants.GUI;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.aleksey.merchants.Containers.ContainerWarehouse;
import com.aleksey.merchants.TileEntities.TileEntityWarehouse;
import com.bioxx.tfc.Core.Player.PlayerInventory;
import com.bioxx.tfc.GUI.GuiContainerTFC;

public class GuiWarehouse extends GuiContainerTFC
{
    private static final ResourceLocation _texture = new ResourceLocation("merchants", "textures/gui/gui_warehouse.png");

    public static final int SlotSize = 18;
    public static final int WindowWidth = 176;
    public static final int WindowHeight = 97;
    
    public static final int SlotX = 80;
    public static final int SlotY = 32;

    private static final int _titleX = 0;
    private static final int _titleY = 4;
    private static final int _warehouseCoordsX = 97;
    private static final int _warehouseCoordsY = 27;
    private static final int _columnWarehouseWidth = 76;
    private static final int _signButtonX = 63;
    private static final int _signButtonY = 65;
    
    private static final int _buttonId_signButton = 0;
    
    private static final int _colorDefaultText = 0x555555;

    private TileEntityWarehouse _warehouse;
    private EntityPlayer _player;
    private GuiButton _signButton;

    public GuiWarehouse(InventoryPlayer inventoryplayer, TileEntityWarehouse warehouse, World world, int x, int y, int z)
    {
        super(new ContainerWarehouse(inventoryplayer, warehouse, world, x, y, z), WindowWidth, WindowHeight - 1);

        _warehouse = warehouse;
        _player = inventoryplayer.player;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        
        _signButton = new GuiButton(_buttonId_signButton, guiLeft + _signButtonX, guiTop + _signButtonY, 50, 20, StatCollector.translateToLocal("gui.Warehouse.Sign"));
        
        buttonList.add(_signButton);
    }

    @Override
    protected void actionPerformed(GuiButton guibutton)
    {
        switch(guibutton.id)
        {
            case  _buttonId_signButton:
                _warehouse.actionSign();
                break;
        }
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

        drawTexturedModalRect(w, h, 0, 0, xSize, ySize);
        
        String inventoryName = StatCollector.translateToLocal(_warehouse.getInventoryName());

        drawCenteredString(inventoryName, w + _titleX, h + _titleY, WindowWidth, _colorDefaultText);
        drawCoords(w, h);
        
        _signButton.enabled = _warehouse.getStackInSlot(0) != null;

        PlayerInventory.drawInventory(this, width, height, ySize - PlayerInventory.invYSize);
    }
    
    private void drawCoords(int w, int h)
    {
        String coordXText = String.valueOf(_warehouse.xCoord);
        String coordYText = String.valueOf(_warehouse.yCoord);
        String coordZText = String.valueOf(_warehouse.zCoord);
        
        int coordXTextWidth = this.fontRendererObj.getStringWidth("X: " + coordXText);
        int coordYTextWidth = this.fontRendererObj.getStringWidth("Y: " + coordYText);
        int coordZTextWidth = this.fontRendererObj.getStringWidth("Z: " + coordZText);
        int coordTextWidth = coordXTextWidth;
        
        if(coordTextWidth < coordYTextWidth)
            coordTextWidth = coordYTextWidth;
        
        if(coordTextWidth < coordZTextWidth)
            coordTextWidth = coordZTextWidth;
        
        int x = w + _warehouseCoordsX + (_columnWarehouseWidth - coordTextWidth) / 2;
        int y1 = h + _warehouseCoordsY;
        int y2 = y1 + this.fontRendererObj.FONT_HEIGHT;
        int y3 = y2 + this.fontRendererObj.FONT_HEIGHT;
        
        fontRendererObj.drawString("X: ", x, y1, _colorDefaultText);
        drawRightAlignedString(coordXText, x, y1, coordTextWidth, _colorDefaultText);
        fontRendererObj.drawString("Y: ", x, y2, _colorDefaultText);
        drawRightAlignedString(coordYText, x, y2, coordTextWidth, _colorDefaultText);
        fontRendererObj.drawString("Z: ", x, y3, _colorDefaultText);
        drawRightAlignedString(coordZText, x, y3, coordTextWidth, _colorDefaultText);
    }
    
    private void drawRightAlignedString(String s, int x, int y, int columnWidth, int color)
    {
        int offset = columnWidth - this.fontRendererObj.getStringWidth(s);
        
        fontRendererObj.drawString(s, x + offset, y, color);
    }
    
    private void drawCenteredString(String s, int x, int y, int columnWidth, int color)
    {
        int offset = (columnWidth - this.fontRendererObj.getStringWidth(s)) / 2;
        
        fontRendererObj.drawString(s, x + offset, y, color);
    }
}
