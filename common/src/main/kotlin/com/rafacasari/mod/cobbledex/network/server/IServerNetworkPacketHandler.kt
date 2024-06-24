package com.rafacasari.mod.cobbledex.network.server

import com.rafacasari.mod.cobbledex.network.INetworkPacket
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity

interface IServerNetworkPacketHandler<T: INetworkPacket<T>> {

    fun handle(packet: T, server: MinecraftServer, player: ServerPlayerEntity)

    fun handleOnNettyThread(packet: T, server: MinecraftServer, player: ServerPlayerEntity) {
        server.execute { handle(packet, server, player) }
    }
}