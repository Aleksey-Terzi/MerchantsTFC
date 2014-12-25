package com.aleksey.merchants.GUI;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.aleksey.merchants.Containers.ContainerAnvilDie;
import com.aleksey.merchants.Helpers.CoinHelper;
import com.aleksey.merchants.Items.ItemTrussel;
import com.aleksey.merchants.TileEntities.TileEntityAnvilDie;
import com.bioxx.tfc.Core.Player.PlayerInventory;
import com.bioxx.tfc.GUI.GuiContainerTFC;

public class GuiAnvilDie extends GuiContainerTFC
{
    private static final ResourceLocation _texture = new ResourceLocation("merchants", "textures/gui/gui_anvil_die.png");

    public static final int SlotSize = 18;
    public static final int WindowWidth = 176;
    public static final int WindowHeight = 127;

    public static final int HammerSlotX = 87;
    public static final int HammerSlotY = 28;
    public static final int TrusselSlotX = 87;
    public static final int TrusselSlotY = 46;
    public static final int FlanSlotX = 87;
    public static final int FlanSlotY = 64;
    public static final int AnvilDieSlotX = 87;
    public static final int AnvilDieSlotY = 82;
    public static final int CoinSlotX = 131;
    public static final int CoinSlotY = 64;

    private static final int _titleX = 0;
    private static final int _titleY = 4;
    private static final int _trusselInfoX = 105;
    private static final int _trusselInfoY = 84;
    private static final int _trusselInfoWidth = 68;
    private static final int _mintButtonX = 28;
    private static final int _mintButtonY = 62;
    
    private static final int _barX = 105;
    private static final int _barY = 64;
    private static final int _barTextureX = 178;
    private static final int _barTextureY = 0;
    private static final int _barWidth = 1;
    private static final int _barHeight = 16;
    
    private static final int _buttonId_mintButton = 0;
    
    private static final int _colorDefaultText = 0x555555;

    private EntityPlayer _player;
    private TileEntityAnvilDie _tileEntity;
    private GuiButton _mintButton;

    public GuiAnvilDie(InventoryPlayer inventoryplayer, TileEntityAnvilDie tileEntity, World world, int x, int y, int z)
    {
        super(new ContainerAnvilDie(inventoryplayer, tileEntity, world, x, y, z), WindowWidth, WindowHeight - 1);

        _player = inventoryplayer.player;
        _tileEntity = tileEntity;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        
        buttonList.add(_mintButton = new GuiButton(_buttonId_mintButton, guiLeft + _mintButtonX, guiTop + _mintButtonY, 50, 20, StatCollector.translateToLocal("gui.AnvilDie.Mint")));
    }

    @Override
    protected void actionPerformed(GuiButton guibutton)
    {
        switch(guibutton.id)
        {
            case  _buttonId_mintButton:
                _tileEntity.actionMint();
                break;
        }
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        int x = mouseX - this.guiLeft;
        int y = mouseY - this.guiTop;
        
        if(x < _barX || x > _barX + _barWidth || y < _barY || y > _barY + _barHeight)
            return;
        
        int metalWeight = _tileEntity.getMetalWeight();
        
        if(metalWeight == 0)
            return;
        
        double metalWeightInOz = (double)metalWeight / 100.0;
        String toolTip = String.valueOf(metalWeightInOz);

        drawTooltip(x, y, toolTip);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        bindTexture(_texture);
        
        int w = (width - xSize) / 2;
        int h = (height - ySize) / 2;
        int v = 0;

        drawTexturedModalRect(w, h, 0, v, xSize, ySize);
        
        drawBar(w, h);

        drawCenteredString(StatCollector.translateToLocal("gui.AnvilDie.Title"), w + _titleX, h + _titleY, WindowWidth, _colorDefaultText);

        drawTrusselInfo(w, h);

        PlayerInventory.drawInventory(this, width, height, ySize - PlayerInventory.invYSize);
    }
    
    private void drawBar(int w, int h)
    {
        int metalWeight = _tileEntity.getMetalWeight();
        
        if(metalWeight == 0)
            return;
        
        int barHeight = _barHeight * metalWeight / CoinHelper.MaxFlanWeight;
        int y = h + _barY - (_barHeight - barHeight);
        
        drawTexturedModalRect(w + _barX, y, _barTextureX, _barTextureY, _barWidth, barHeight);
    }
    
    private void drawTrusselInfo(int w, int h)
    {
        ItemStack itemStack = _tileEntity.getStackInSlot(1);
        
        if(itemStack == null)
        {
            _mintButton.enabled = false;
            return;
        }
        
        String name = ItemTrussel.getTrusselName(itemStack);
        String weight = CoinHelper.getWeightText(ItemTrussel.getTrusselWeight(itemStack));
        
        int x = w + _trusselInfoX;
        int y = h + _trusselInfoY;
        
        drawCenteredString(name, x, y, _trusselInfoWidth, _colorDefaultText);
        drawCenteredString(weight, x, y + this.fontRendererObj.FONT_HEIGHT, _trusselInfoWidth, _colorDefaultText);
        
        _mintButton.enabled = true;
    }

    private void drawCenteredString(String s, int x, int y, int columnWidth, int color)
    {
        int offset = (columnWidth - this.fontRendererObj.getStringWidth(s)) / 2;
        
        fontRendererObj.drawString(s, x + offset, y, color);
    }
}
