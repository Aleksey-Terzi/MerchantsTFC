package com.aleksey.merchants.GUI;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.aleksey.merchants.Containers.ContainerStorageRack;
import com.aleksey.merchants.TileEntities.TileEntityStorageRack;
import com.bioxx.tfc.Core.Player.PlayerInventory;
import com.bioxx.tfc.GUI.GuiContainerTFC;

public class GuiStorageRack extends GuiContainerTFC
{
    private static final ResourceLocation _texture = new ResourceLocation("merchants", "textures/gui/gui_storagerack.png");

    public static final int SlotSize = 18;
    public static final int WindowWidth = 176;
    public static final int WindowHeight = 80;
    
    public static final int SlotX = 80;
    public static final int SlotY = 32;

    private static final int _titleX = 0;
    private static final int _titleY = 4;
    
    private static final int _colorDefaultText = 0x555555;

    private TileEntityStorageRack _storageRack;

    public GuiStorageRack(InventoryPlayer inventoryplayer, TileEntityStorageRack storageRack, World world, int x, int y, int z)
    {
        super(new ContainerStorageRack(inventoryplayer, storageRack, world, x, y, z), WindowWidth, WindowHeight - 1);

        _storageRack = storageRack;
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
        
        String inventoryName = StatCollector.translateToLocal(_storageRack.getInventoryName());

        drawCenteredString(inventoryName, w + _titleX, h + _titleY, WindowWidth, _colorDefaultText);

        PlayerInventory.drawInventory(this, width, height, ySize - PlayerInventory.invYSize);
    }
    
    private void drawCenteredString(String s, int x, int y, int columnWidth, int color)
    {
        int offset = (columnWidth - this.fontRendererObj.getStringWidth(s)) / 2;
        
        fontRendererObj.drawString(s, x + offset, y, color);
    }
}
