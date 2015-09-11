package com.aleksey.merchants.GUI;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.aleksey.merchants.Containers.ContainerTrussel;
import com.aleksey.merchants.Handlers.Network.DieCopyPacket;
import com.aleksey.merchants.Helpers.CoinHelper;
import com.aleksey.merchants.Items.ItemTrussel;
import com.bioxx.tfc.TerraFirmaCraft;
import com.bioxx.tfc.Core.Player.PlayerInventory;
import com.bioxx.tfc.GUI.GuiContainerTFC;

public class GuiTrussel extends GuiContainerTFC
{
    private static final ResourceLocation _texture = new ResourceLocation("merchants", "textures/gui/gui_trussel.png");

    public static final int SlotSize = 18;
    public static final int WindowWidth = 176;
    public static final int WindowHeight = 127;
    
    public static final int SlotY = 73;
    public static final int SrcSlotX = 58;
    public static final int DstSlotX = 102;

    private static final int _titleX = 0;
    private static final int _titleY = 4;
    private static final int _nameLabelX = 6;
    private static final int _nameLabelY = 20;
    private static final int _nameX = 57;
    private static final int _nameY = 20;
    private static final int _weightLabelX = 6;
    private static final int _weightLabelY = 30;
    private static final int _weightX = 57;
    private static final int _weightY = 30;
    private static final int _dieLabelX = 6;
    private static final int _dieLabelY = 40;
    private static final int _dieX = 58;
    private static final int _dieY = 41;
    private static final int _copyButtonX = 63;
    private static final int _copyButtonY = 96;
    
    private static final int _dieTextureY = 128;
    private static final int _dieNotSetTextureX = 178;
    private static final int _dieSetTextureX = 181;
    private static final int _diePixelSize = 2;
    
    private static final int _buttonId_copyButton = 0;
    
    private static final int _colorDefaultText = 0x555555;

    private EntityPlayer _player;
    private GuiButton _copyButton;
    private String _name;
    private String _weight;
    private boolean[] _die;

    public GuiTrussel(InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        super(new ContainerTrussel(inventoryplayer, world, x, y, z), WindowWidth, WindowHeight - 1);

        _player = inventoryplayer.player;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        
        buttonList.add(_copyButton = new GuiButton(_buttonId_copyButton, guiLeft + _copyButtonX, guiTop + _copyButtonY, 50, 20, StatCollector.translateToLocal("gui.Trussel.Copy")));
        
        ItemStack itemStack = _player.inventory.getCurrentItem();
        
        int weightIndex = CoinHelper.getCoinWeight(itemStack);
        
        _name = CoinHelper.getCoinName(itemStack);
        _weight = CoinHelper.getWeightText(weightIndex);
        _die = CoinHelper.unpackDie(CoinHelper.getCoinDie(itemStack));
    }

    @Override
    protected void actionPerformed(GuiButton guibutton)
    {
        switch(guibutton.id)
        {
            case  _buttonId_copyButton:
                copyTrussel();
                break;
        }
    }
    
    private void copyTrussel()
    {
        if(!((ContainerTrussel)this.inventorySlots).copyTrussel())
            return;
        
        DieCopyPacket packet = new DieCopyPacket();

        TerraFirmaCraft.PACKET_PIPELINE.sendToServer(packet);
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
        int v = WindowHeight;

        drawTexturedModalRect(w, h, 0, v, xSize, ySize);
        
        drawDie(w, h);

        drawCenteredString(StatCollector.translateToLocal("gui.Trussel.Title"), w + _titleX, h + _titleY, WindowWidth, _colorDefaultText);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.Trussel.Name") + ":",  w + _nameLabelX, h + _nameLabelY, _colorDefaultText);
        this.fontRendererObj.drawString(_name,  w + _nameX, h + _nameY, _colorDefaultText);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.Trussel.Weight") + ":",  w + _weightLabelX, h + _weightLabelY, _colorDefaultText);
        this.fontRendererObj.drawString(_weight,  w + _weightX, h + _weightY, _colorDefaultText);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.Trussel.Die") + ":",  w + _dieLabelX, h + _dieLabelY, _colorDefaultText);

        PlayerInventory.drawInventory(this, width, height, ySize - PlayerInventory.invYSize);
        
        _copyButton.enabled = this.inventorySlots.getSlot(0).getStack() != null;
    }
    
    private void drawDie(int w, int h)
    {
        int index = 0;
        int y = h + _dieY;
        
        for(int row = 0; row < CoinHelper.DieStride; row++)
        {
            int x = w + _dieX;
            
            for(int col = 0; col < CoinHelper.DieStride; col++)
            {
                int textureX = _die[index] ? _dieSetTextureX: _dieNotSetTextureX;
                
                drawTexturedModalRect(x, y, textureX, _dieTextureY, _diePixelSize, _diePixelSize);
                
                index++;
                
                x += _diePixelSize;
            }
            
            y += _diePixelSize;
        }
    }
    
    private void drawCenteredString(String s, int x, int y, int columnWidth, int color)
    {
        int offset = (columnWidth - this.fontRendererObj.getStringWidth(s)) / 2;
        
        fontRendererObj.drawString(s, x + offset, y, color);
    }
}
