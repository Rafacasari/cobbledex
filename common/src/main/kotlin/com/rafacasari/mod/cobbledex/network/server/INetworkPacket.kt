package com.rafacasari.mod.cobbledex.network.server

import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.util.server
import com.rafacasari.mod.cobbledex.network.CobbledexNetworkManager
import io.netty.buffer.Unpooled
import net.minecraft.network.PacketByteBuf
import net.minecraft.registry.RegistryKey
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.world.World

interface INetworkPacket<T: INetworkPacket<T>> : Encodable {

    /**
     *
     */
    val id: Identifier

    /**
     * TODO
     *
     * @param player
     */
    fun sendToPlayer(player: ServerPlayerEntity) = CobbledexNetworkManager.sendPacketToPlayer(player, this)

    /**
     * TODO
     *
     * @param players
     */
    fun sendToPlayers(players: Iterable<ServerPlayerEntity>) {
        if (players.any()) {
            CobbledexNetworkManager.sendPacketToPlayers(players, this)
        }
    }

    /**
     * TODO
     *
     */
    fun sendToAllPlayers() = CobbledexNetworkManager.sendToAllPlayers(this)

    /**
     * TODO
     *
     */
    fun sendToServer() = CobbledexNetworkManager.sendPacketToServer(this)

    // A copy from PlayerManager#sendToAround to work with our packets
    /**
     * TODO
     *
     * @param x
     * @param y
     * @param z
     * @param distance
     * @param worldKey
     * @param exclusionCondition
     */
    fun sendToPlayersAround(x: Double, y: Double, z: Double, distance: Double, worldKey: RegistryKey<World>, exclusionCondition: (ServerPlayerEntity) -> Boolean = { false }) {
        val server = server() ?: return
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

    /**
     * TODO
     *
     * @return
     */
    fun toBuffer(): PacketByteBuf {
        val buffer = PacketByteBuf(Unpooled.buffer())
        this.encode(buffer)
        return buffer
    }

}