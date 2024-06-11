package com.rafacasari.mod.cobbledex.network


import com.rafacasari.mod.cobbledex.Cobbledex
import com.rafacasari.mod.cobbledex.network.client.handlers.ReceiveCobbledexPacketHandler
import com.rafacasari.mod.cobbledex.network.client.packets.ReceiveCobbledexPacket
import com.rafacasari.mod.cobbledex.network.server.IClientNetworkPacketHandler
import com.rafacasari.mod.cobbledex.network.server.INetworkPacket
import com.rafacasari.mod.cobbledex.network.server.IServerNetworkPacketHandler
import com.rafacasari.mod.cobbledex.network.server.handlers.RequestCobbledexPacketHandler
import com.rafacasari.mod.cobbledex.network.server.packets.RequestCobbledexPacket
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

object CobbledexNetworkManager {
    fun ServerPlayerEntity.sendPacket(packet: INetworkPacket<*>) = sendPacketToPlayer(this, packet)
    fun sendToServer(packet: INetworkPacket<*>) = this.sendPacketToServer(packet)
    fun sendToAllPlayers(packet: INetworkPacket<*>) = sendPacketToPlayers(Cobbledex.implementation.server()!!.playerManager.playerList, packet)
    fun sendPacketToPlayers(players: Iterable<ServerPlayerEntity>, packet: INetworkPacket<*>) = players.forEach { sendPacketToPlayer(it, packet) }
    fun sendPacketToPlayer(player: ServerPlayerEntity, packet: INetworkPacket<*>) = Cobbledex.implementation.networkManager.sendPacketToPlayer(player, packet)
    fun sendPacketToServer(packet: INetworkPacket<*>) = Cobbledex.implementation.networkManager.sendPacketToServer(packet)



    fun registerClientBound() {
       createClientBound(ReceiveCobbledexPacket.ID, ReceiveCobbledexPacket::decode, ReceiveCobbledexPacketHandler)
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