package com.rafacasari.mod.cobbledex.network.server

import net.minecraft.client.MinecraftClient

interface IClientNetworkPacketHandler<T: INetworkPacket<T>> {

    fun handle(packet: T, client: MinecraftClient)

    fun handleOnNettyThread(packet: T) {
        val client = MinecraftClient.getInstance()
        client.execute { handle(packet, client) }
    }
}