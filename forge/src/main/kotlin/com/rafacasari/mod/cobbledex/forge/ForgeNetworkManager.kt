package com.rafacasari.mod.cobbledex.forge

import com.rafacasari.mod.cobbledex.INetworkManager
import com.rafacasari.mod.cobbledex.network.CobbledexNetworkManager
import com.rafacasari.mod.cobbledex.network.server.IClientNetworkPacketHandler
import com.rafacasari.mod.cobbledex.network.server.INetworkPacket
import com.rafacasari.mod.cobbledex.network.server.IServerNetworkPacketHandler
import net.minecraft.network.packet.Packet
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraftforge.network.NetworkDirection
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.PacketDistributor
import kotlin.reflect.KClass

object ForgeNetworkManager : INetworkManager {

    private const val PROTOCOL_VERSION = "1"
    private var id = 0

    private val channel = NetworkRegistry.newSimpleChannel(
        Identifier("cobbledex", "main"),
        { PROTOCOL_VERSION },
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    )

    override fun registerClientBound() {
        CobbledexNetworkManager.registerClientBound()
    }

    override fun registerServerBound() {
        CobbledexNetworkManager.registerServerBound()
    }

    @Suppress("INACCESSIBLE_TYPE")
    override fun <T : INetworkPacket<T>> createClientBound(
        identifier: Identifier,
        kClass: KClass<T>,
        encoder: (T, PacketByteBuf) -> Unit,
        decoder: (PacketByteBuf) -> T,
        handler: IClientNetworkPacketHandler<T>
    ) {
        this.channel.registerMessage(this.id++, kClass.java, encoder::invoke, decoder::invoke) { msg, ctx ->
            val context = ctx.get()
            handler.handleOnNettyThread(msg)
            context.packetHandled = true
        }
    }

    @Suppress("INACCESSIBLE_TYPE")
    override fun <T : INetworkPacket<T>> createServerBound(
        identifier: Identifier,
        kClass: KClass<T>,
        encoder: (T, PacketByteBuf) -> Unit,
        decoder: (PacketByteBuf) -> T,
        handler: IServerNetworkPacketHandler<T>
    ) {
        this.channel.registerMessage(this.id++, kClass.java, encoder::invoke, decoder::invoke) { msg, ctx ->
            val context = ctx.get()
            handler.handleOnNettyThread(msg, context.sender!!.server, context.sender!!)
            context.packetHandled = true
        }
    }

    override fun sendPacketToPlayer(player: ServerPlayerEntity, packet: INetworkPacket<*>) {
        this.channel.send(PacketDistributor.PLAYER.with { player }, packet)
    }

    override fun sendPacketToServer(packet: INetworkPacket<*>) {
        this.channel.sendToServer(packet)
    }

    override fun <T : INetworkPacket<*>> asVanillaClientBound(packet: T): Packet<ClientPlayPacketListener> {
        return this.channel.toVanillaPacket(packet, NetworkDirection.PLAY_TO_CLIENT) as Packet<ClientPlayPacketListener>
    }
}
