package com.rafacasari.mod.cobbledex.network.client.handlers

import com.rafacasari.mod.cobbledex.CobbledexConfig
import com.rafacasari.mod.cobbledex.network.client.IClientNetworkPacketHandler
import com.rafacasari.mod.cobbledex.network.client.packets.SyncServerSettingsPacket
import net.minecraft.client.MinecraftClient

object SyncServerSettingsHandler : IClientNetworkPacketHandler<SyncServerSettingsPacket> {
    var config = CobbledexConfig()

    override fun handle(packet: SyncServerSettingsPacket, client: MinecraftClient) {
        config = packet.config
    }
}