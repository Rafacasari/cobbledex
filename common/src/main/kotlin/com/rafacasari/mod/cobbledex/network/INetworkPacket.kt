package com.rafacasari.mod.cobbledex.network

import com.rafacasari.mod.cobbledex.Cobbledex
import io.netty.buffer.Unpooled
import net.minecraft.network.FriendlyByteBuf as PacketByteBuf
import net.minecraft.resources.ResourceKey as RegistryKey
import net.minecraft.server.level.ServerPlayer as ServerPlayerEntity
import net.minecraft.resources.ResourceLocation as Identifier
import net.minecraft.world.level.Level as World

interface INetworkPacket<T: INetworkPacket<T>> : IEncodable {

    val id: Identifier

    fun sendToPlayer(player: ServerPlayerEntity) = CobbledexNetwork.sendPacketToPlayer(player, this)
    fun sendToPlayers(players: Iterable<ServerPlayerEntity>) {
        if (players.any()) {
            CobbledexNetwork.sendPacketToPlayers(players, this)
        }
    }

    fun sendToAllPlayers() = CobbledexNetwork.sendToAllPlayers(this)
    fun sendToServer() = CobbledexNetwork.sendPacketToServer(this)

    fun sendToPlayersAround(x: Double, y: Double, z: Double, distance: Double, worldKey: RegistryKey<World>, exclusionCondition: (ServerPlayerEntity) -> Boolean = { false }) {
        val server = Cobbledex.implementation.server() ?: return
        server.playerList.players.filter { player ->
            if (exclusionCondition.invoke(player))
                return@filter false
            val xDiff = x - player.x
            val yDiff = y - player.y
            val zDiff = z - player.z
            return@filter (xDiff * xDiff + yDiff * yDiff + zDiff) < distance * distance
        }
            .forEach { player -> CobbledexNetwork.sendPacketToPlayer(player, this) }
    }


    fun toBuffer(): PacketByteBuf {
        val buffer = PacketByteBuf(Unpooled.buffer())
        this.encode(buffer)
        return buffer
    }
}