package com.aleksey.merchants.Blocks.Devices;

import net.minecraft.block.Block;

import com.aleksey.merchants.Core.DieInfo;
import com.bioxx.tfc.api.TFCBlocks;

public class BlockCustomAnvilDie2 extends BlockCustomAnvilDie
{
    public BlockCustomAnvilDie2(DieInfo info)
    {
        super(info);
    }
    
    @Override
    protected int getLogsMetadata(int metadata)
    {
        return 16 + metadata;
    }
    
    @Override
    public Block getLogBlock()
    {
        return TFCBlocks.WoodVert2;
    }
}
