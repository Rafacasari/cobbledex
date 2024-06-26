package com.rafacasari.mod.cobbledex.network

import com.rafacasari.mod.cobbledex.Cobbledex
import com.rafacasari.mod.cobbledex.network.client.handlers.*
import com.rafacasari.mod.cobbledex.network.client.packets.*
import com.rafacasari.mod.cobbledex.network.client.IClientNetworkPacketHandler
import com.rafacasari.mod.cobbledex.network.server.IServerNetworkPacketHandler
import com.rafacasari.mod.cobbledex.network.server.handlers.*
import com.rafacasari.mod.cobbledex.network.server.packets.*
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

object CobbledexNetwork {
    fun sendToAllPlayers(packet: INetworkPacket<*>) = sendPacketToPlayers(Cobbledex.implementation.server()!!.playerManager.playerList, packet)
    fun sendPacketToPlayers(players: Iterable<ServerPlayerEntity>, packet: INetworkPacket<*>) = players.forEach { sendPacketToPlayer(it, packet) }
    fun sendPacketToPlayer(player: ServerPlayerEntity, packet: INetworkPacket<*>) = Cobbledex.implementation.networkManager.sendPacketToPlayer(player, packet)
    fun sendPacketToServer(packet: INetworkPacket<*>) = Cobbledex.implementation.networkManager.sendPacketToServer(packet)

    fun registerClientBound() {
        createClientBound(ReceiveCobbledexPacket.ID, ReceiveCobbledexPacket::decode, ReceiveCobbledexHandler)
        createClientBound(AddToCollectionPacket.ID, AddToCollectionPacket::decode, AddToCollectionHandler)
        createClientBound(OpenCobbledexPacket.ID, OpenCobbledexPacket::decode, OpenCobbledexHandler)
        createClientBound(ReceiveCollectionDataPacket.ID, ReceiveCollectionDataPacket::decode, ReceiveCollectionDataHandler)
        createClientBound(SyncServerSettingsPacket.ID, SyncServerSettingsPacket::decode, SyncServerSettingsHandler)
        createClientBound(OpenDiscoveryPacket.ID, OpenDiscoveryPacket::decode, OpenDiscoveryHandler)
    }

    fun registerServerBound() {
        createServerBound(RequestCobbledexPacket.ID, RequestCobbledexPacket::decode, RequestCobbledexPacketHandler)
    }

    private inline fun <reified T : INetworkPacket<T>> createClientBound(identifier: Identifier, noinline decoder: (PacketByteBuf) -> T, handler: IClientNetworkPacketHandler<T>) {
        Cobbledex.implementation.networkManager.createClientBound(identifier, T::class, { message, buffer -> message.encode(buffer) }, decoder, handler)
    }

    private inline fun <reified T : INetworkPacket<T>> createServerBound(identifier: Identifier, noinline decoder: (PacketByteBuf) -> T, handler: IServerNetworkPacketHandler<T>) {
        Cobbledex.implementation.networkManager.createServerBound(identifier, T::class, { message, buffer -> message.encode(buffer) }, decoder, handler)
    }
}