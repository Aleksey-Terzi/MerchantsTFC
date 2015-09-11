package com.aleksey.merchants.Core.Player;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.item.ItemTossEvent;

import com.aleksey.merchants.Handlers.Network.InitClientWorldPacket;
import com.bioxx.tfc.TerraFirmaCraft;
import com.bioxx.tfc.Handlers.Network.AbstractPacket;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemPickupEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerDisconnectionFromClientEvent;

public class PlayerTracker
{
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerLoggedInEvent event)
    {
        AbstractPacket pkt = new InitClientWorldPacket();
        TerraFirmaCraft.PACKET_PIPELINE.sendTo(pkt, (EntityPlayerMP) event.player);
    }

    @SubscribeEvent
    public void onClientConnect(ClientConnectedToServerEvent event)
    {
    }

    @SubscribeEvent
    public void onClientDisconnect(ServerDisconnectionFromClientEvent event)
    {
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerRespawnEvent event)
    {
    }

    @SubscribeEvent
    public void notifyPickup(ItemPickupEvent event)
    {
    }

    // Register the Player Toss Event Handler, workaround for a crash fix
    @SubscribeEvent
    public void onPlayerTossEvent(ItemTossEvent event)
    {
    }
}
