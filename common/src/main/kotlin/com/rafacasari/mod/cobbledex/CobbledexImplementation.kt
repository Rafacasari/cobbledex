package com.rafacasari.mod.cobbledex

import com.rafacasari.mod.cobbledex.network.client.IClientNetworkPacketHandler
import com.rafacasari.mod.cobbledex.network.INetworkPacket
import com.rafacasari.mod.cobbledex.network.server.IServerNetworkPacketHandler
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import kotlin.reflect.KClass

//import net.minecraft.world.biome.Biome

interface CobbledexImplementation {
    val modAPI: ModAPI
    val networkManager: INetworkManager
    fun server(): MinecraftServer?
    fun environment(): Environment
    fun registerItems()
//    fun getAllRegisteredBiomes() : List<Biome>
}

enum class ModAPI {
    FABRIC,
    FORGE
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

    fun <T : INetworkPacket<*>> asVanillaClientBound(packet: T): Packet<ClientPlayPacketListener>

}