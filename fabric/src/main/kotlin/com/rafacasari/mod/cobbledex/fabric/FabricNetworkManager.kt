package com.rafacasari.mod.cobbledex.fabric

import com.rafacasari.mod.cobbledex.INetworkManager

import com.rafacasari.mod.cobbledex.network.CobbledexNetwork
import com.rafacasari.mod.cobbledex.network.server.IClientNetworkPacketHandler
import com.rafacasari.mod.cobbledex.network.server.INetworkPacket
import com.rafacasari.mod.cobbledex.network.server.IServerNetworkPacketHandler
import kotlin.reflect.KClass
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.Packet
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier


object FabricNetworkManager : INetworkManager {

    override fun registerClientBound() {
        CobbledexNetwork.registerClientBound()
    }

    override fun registerServerBound() {
        CobbledexNetwork.registerServerBound()
    }

    override fun <T : INetworkPacket<T>> createClientBound(
        identifier: Identifier,
        kClass: KClass<T>,
        encoder: (T, PacketByteBuf) -> Unit,
        decoder: (PacketByteBuf) -> T,
        handler: IClientNetworkPacketHandler<T>
    ) {
        ClientPlayNetworking.registerGlobalReceiver(identifier, this.createClientBoundHandler(decoder::invoke) { msg, _ ->
            handler.handleOnNettyThread(msg)
        })
    }

    override fun <T : INetworkPacket<T>> createServerBound(
        identifier: Identifier,
        kClass: KClass<T>,
        encoder: (T, PacketByteBuf) -> Unit,
        decoder: (PacketByteBuf) -> T,
        handler: IServerNetworkPacketHandler<T>
    ) {
        ServerPlayNetworking.registerGlobalReceiver(identifier, this.createServerBoundHandler(decoder::invoke, handler::handleOnNettyThread))
    }

    fun <T : INetworkPacket<*>> createServerBoundHandler(
        decoder: (PacketByteBuf) -> T,
        handler: (T, MinecraftServer, ServerPlayerEntity) -> Unit
    ) = ServerPlayNetworking.PlayChannelHandler { server, player, _, buffer, _ ->
        handler(decoder(buffer), server, player)
    }

    fun <T : INetworkPacket<*>> createClientBoundHandler(
        decoder: (PacketByteBuf) -> T,
        handler: (T, MinecraftClient) -> Unit
    ) = ClientPlayNetworking.PlayChannelHandler { client, _,  buffer, _ ->
        handler(decoder(buffer), client)
    }

    override fun sendPacketToPlayer(player: ServerPlayerEntity, packet: INetworkPacket<*>) {
        ServerPlayNetworking.send(player, packet.id, packet.toBuffer())
    }

    override fun sendPacketToServer(packet: INetworkPacket<*>) {
        ClientPlayNetworking.send(packet.id, packet.toBuffer())
    }

    override fun <T : INetworkPacket<*>> asVanillaClientBound(packet: T): Packet<ClientPlayPacketListener> {
        return ServerPlayNetworking.createS2CPacket(packet.id, packet.toBuffer())
    }
}
