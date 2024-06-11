package com.rafacasari.mod.cobbledex.network.server

import com.cobblemon.mod.common.api.net.Encodable
import com.rafacasari.mod.cobbledex.Cobbledex
import com.rafacasari.mod.cobbledex.network.CobbledexNetworkManager
import io.netty.buffer.Unpooled
import net.minecraft.network.PacketByteBuf
import net.minecraft.registry.RegistryKey
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.world.World

interface INetworkPacket<T: INetworkPacket<T>> : Encodable {

    val id: Identifier

    fun sendToPlayer(player: ServerPlayerEntity) = CobbledexNetworkManager.sendPacketToPlayer(player, this)
    fun sendToPlayers(players: Iterable<ServerPlayerEntity>) {
        if (players.any()) {
            CobbledexNetworkManager.sendPacketToPlayers(players, this)
        }
    }

    fun sendToAllPlayers() = CobbledexNetworkManager.sendToAllPlayers(this)
    fun sendToServer() = CobbledexNetworkManager.sendPacketToServer(this)

    fun sendToPlayersAround(x: Double, y: Double, z: Double, distance: Double, worldKey: RegistryKey<World>, exclusionCondition: (ServerPlayerEntity) -> Boolean = { false }) {
        val server = Cobbledex.implementation.server() ?: return
        server.playerManager.playerList.filter { player ->
            if (exclusionCondition.invoke(player))
                return@filter false
            val xDiff = x - player.x
            val yDiff = y - player.y
            val zDiff = z - player.z
            return@filter (xDiff * xDiff + yDiff * yDiff + zDiff) < distance * distance
        }
            .forEach { player -> CobbledexNetworkManager.sendPacketToPlayer(player, this) }
    }


    fun toBuffer(): PacketByteBuf {
        val buffer = PacketByteBuf(Unpooled.buffer())
        this.encode(buffer)
        return buffer
    }
}