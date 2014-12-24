package com.aleksey.merchants.Handlers.Network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

import com.aleksey.merchants.Containers.ContainerTrussel;
import com.bioxx.tfc.Handlers.Network.AbstractPacket;

public class DieCopyPacket extends AbstractPacket
{
    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
    {
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
    {
    }

    @Override
    public void handleClientSide(EntityPlayer player)
    {
    }

    @Override
    public void handleServerSide(EntityPlayer player)
    {
        if(player.openContainer != null && player.openContainer instanceof ContainerTrussel)
        {
            ((ContainerTrussel)player.openContainer).copyTrussel();
        }
    }
}
