package com.aleksey.merchants.GUI;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.aleksey.merchants.Containers.ContainerStall;
import com.aleksey.merchants.Helpers.ItemHelper;
import com.aleksey.merchants.TileEntities.TileEntityStall;
import com.bioxx.tfc.Core.Player.PlayerInventory;
import com.bioxx.tfc.GUI.GuiContainerTFC;

public class GuiStall extends GuiContainerTFC
{
    private class QuantityInfo
    {
        public int Quantity;
        public int Color;
        public String ToolTip;
    }
    
    private static final ResourceLocation _texture = new ResourceLocation("merchants", "textures/gui/gui_stall.png");

    public static final int SlotSize = 18;
    public static final int WindowWidth = 176;
    public static final int WindowHeight = 127;
    
    public static final int TopSlotY = 32;
    public static final int PricesSlotX = 18;
    public static final int GoodsSlotX = 62;
    public static final int BookSlotX = 127;
    public static final int BookSlotY = 32;

    private static final int _titleX = 0;
    private static final int _titleY = 4;
    private static final int _pricesTitleX = 6;
    private static final int _pricesTitleY = 17;
    private static final int _goodsTitleX = 50;
    private static final int _goodsTitleY = 17;
    private static final int _columnTitleWidth = 40;
    private static final int _warehouseTitleX = 97;
    private static final int _warehouseTitleY = 17;
    private static final int _columnWarehouseWidth = 76;
    private static final int _warehouseCoordsX = 97;
    private static final int _warehouseCoordsY = 52;
    private static final int _clearButtonX = 110;
    private static final int _clearButtonY = 102;
    private static final int _quantityX = 81;
    
    private static final int _buttonId_clearButton = 0;
    
    private static final int _colorDefaultText = 0x555555;
    private static final int _colorSuccessText = 0x00AA00;
    private static final int _colorFailedText = 0xAA0000;

    private TileEntityStall _stall;
    private EntityPlayer _player;
    private boolean _isOwnerMode;
    private QuantityInfo[] _quantities;

    public GuiStall(InventoryPlayer inventoryplayer, TileEntityStall stall, boolean isOwnerMode, World world, int x, int y, int z)
    {
        super(new ContainerStall(inventoryplayer, stall, isOwnerMode, world, x, y, z), WindowWidth, WindowHeight - 1);

        _stall = stall;
        _player = inventoryplayer.player;
        _isOwnerMode = isOwnerMode;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        
        if(_isOwnerMode)
            buttonList.add(new GuiButton(_buttonId_clearButton, guiLeft + _clearButtonX, guiTop + _clearButtonY, 50, 20, StatCollector.translateToLocal("gui.Stall.Clear")));
    }

    @Override
    protected void actionPerformed(GuiButton guibutton)
    {
        switch(guibutton.id)
        {
            case  _buttonId_clearButton:
                _stall.actionClearPrices();
                break;
        }
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        QuantityInfo info = getQuantityInfo(mouseX, mouseY);
        
        if(info != null)
            drawTooltip(mouseX - this.guiLeft, mouseY - this.guiTop, info.ToolTip);
    }
    
    private QuantityInfo getQuantityInfo(int mouseX, int mouseY)
    {
        if(!getQuantities())
            return null;
        
        int w = (width - xSize) / 2;
        int h = (height - ySize) / 2;
        
        if(mouseX < w + _quantityX)
            return null;
        
        int y = h + TopSlotY + SlotSize - this.fontRendererObj.FONT_HEIGHT;
        
        for(int i = 0; i < _quantities.length; i++)
        {
            if(mouseY < y)
                return null;
            
            if(mouseY < y + SlotSize)
            {
                QuantityInfo info = _quantities[i];
                
                if(info == null)
                    return null;
                
                int textWidth = this.fontRendererObj.getStringWidth(String.valueOf(info.Quantity));
                
                return mouseX < w + _quantityX + textWidth ? info: null;
            }
            
            y += SlotSize;
        }
        
        return null;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        resetQuantities();
        
        bindTexture(_texture);
        
        int w = (width - xSize) / 2;
        int h = (height - ySize) / 2;
        int v = _isOwnerMode ? 0: WindowHeight;

        drawTexturedModalRect(w, h, 0, v, xSize, ySize);
        
        String inventoryName = StatCollector.translateToLocal(_stall.getInventoryName());
        String title = !_stall.getIsWarehouseSpecified() ? inventoryName: inventoryName + " (" + _stall.getOwnerUserName() + ")";

        drawCenteredString(title, w + _titleX, h + _titleY, WindowWidth, _colorDefaultText);
        drawCenteredString(StatCollector.translateToLocal("gui.Stall.Prices"), w + _pricesTitleX, h + _pricesTitleY, _columnTitleWidth, _colorDefaultText);
        drawCenteredString(StatCollector.translateToLocal("gui.Stall.Goods"), w + _goodsTitleX, h + _goodsTitleY, _columnTitleWidth, _colorDefaultText);
        
        drawWarehouseText(w, h);
        drawQuantities(w, h);

        PlayerInventory.drawInventory(this, width, height, ySize - PlayerInventory.invYSize);
    }
    
    private void drawWarehouseText(int w, int h)
    {
        if(!_isOwnerMode)
            return;
        
        drawCenteredString(StatCollector.translateToLocal("gui.Stall.Warehouse"), w + _warehouseTitleX, h + _warehouseTitleY, _columnWarehouseWidth, _colorDefaultText);
        
        if(!_stall.getIsWarehouseSpecified())
            return;

        String coordXText = String.valueOf(_stall.getWarehouseX());
        String coordYText = String.valueOf(_stall.getWarehouseY());
        String coordZText = String.valueOf(_stall.getWarehouseZ());
        
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
        
        fontRendererObj.drawString("C: " + String.valueOf(_stall.getContainersInWarehouse()), x, y3 + this.fontRendererObj.FONT_HEIGHT, _colorDefaultText);
    }
    
    private void drawQuantities(int w, int h)
    {
        if(!getQuantities())
            return;
        
        int y = TopSlotY + SlotSize - this.fontRendererObj.FONT_HEIGHT;
        
        for(int i = 0; i < _quantities.length; i++)
        {
            QuantityInfo info = _quantities[i];
            
            if(info != null)
                fontRendererObj.drawString(String.valueOf(info.Quantity), w + _quantityX, h + y, info.Color);
            
            y += SlotSize;
        }
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
    
    private void resetQuantities()
    {
        _quantities = null;
    }
    
    private boolean getQuantities()
    {
        if(!_stall.getIsWarehouseSpecified())
        {
            _quantities = null;
            return false;
        }
        
        if(_quantities != null)
            return true;

        _quantities = new QuantityInfo[TileEntityStall.GoodsSlotIndexes.length];
        
        for(int i = 0; i < TileEntityStall.GoodsSlotIndexes.length; i++)
        {
            int slotIndex = TileEntityStall.GoodsSlotIndexes[i];
            ItemStack itemStack = _stall.getStackInSlot(slotIndex);
            
            if(itemStack == null)
                continue;

            QuantityInfo info = new QuantityInfo();                
            info.Quantity = _stall.getQuantityInWarehouse(itemStack);
            
            if(info.Quantity < ItemHelper.getItemStackQuantity(itemStack))
            {
                info.Color = _colorFailedText;
                info.ToolTip = StatCollector.translateToLocal("gui.Stall.NotEnoughGoods");
            }
            else
            {
                info.Color = _colorSuccessText;
                info.ToolTip = StatCollector.translateToLocal("gui.Stall.CanBuy");
            }
            
            _quantities[i] = info;
        }
        
        return true;
    }
}