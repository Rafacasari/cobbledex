package com.rafacasari.mod.cobbledex

import com.rafacasari.mod.cobbledex.network.client.IClientNetworkPacketHandler
import com.rafacasari.mod.cobbledex.network.INetworkPacket
import com.rafacasari.mod.cobbledex.network.server.IServerNetworkPacketHandler
import net.minecraft.network.FriendlyByteBuf as PacketByteBuf
import net.minecraft.network.protocol.Packet
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer as ServerPlayerEntity
import net.minecraft.resources.ResourceLocation as Identifier
import kotlin.reflect.KClass

interface CobbledexImplementation {
    val modAPI: ModAPI
    val networkManager: INetworkManager
    fun server(): MinecraftServer?
    fun environment(): Environment
    fun registerItems()
}

enum class ModAPI {
    FABRIC,
    NEOFORGE
}

enum class Environment {
    CLIENT,
    SERVER
}

interface INetworkManager {

    fun registerClientBound()

    fun registerServerBound()

    fun <T: INetworkPacket<T>> createClientBound(identifier: Identifier, kClass: KClass<T>, encoder: (T, PacketByteBuf) -> Unit, decoder: (PacketByteBuf) -> T, handler: IClientNetworkPacketHandler<T>)

    fun <T: INetworkPacket<T>> createServerBound(identifier: Identifier, kClass: KClass<T>, encoder: (T, PacketByteBuf) -> Unit, decoder: (PacketByteBuf) -> T, handler: IServerNetworkPacketHandler<T>)

    fun sendPacketToPlayer(player: ServerPlayerEntity, packet: INetworkPacket<*>)

    fun sendPacketToServer(packet: INetworkPacket<*>)

    fun <T : INetworkPacket<*>> asVanillaClientBound(packet: T): Packet<*>
}