package com.rafacasari.mod.cobbledex.network.client.handlers

import com.rafacasari.mod.cobbledex.client.gui.CobbledexCollectionGUI
import com.rafacasari.mod.cobbledex.network.client.IClientNetworkPacketHandler
import com.rafacasari.mod.cobbledex.network.client.packets.OpenDiscoveryPacket
import net.minecraft.client.MinecraftClient

object OpenDiscoveryHandler : IClientNetworkPacketHandler<OpenDiscoveryPacket> {
    override fun handle(packet: OpenDiscoveryPacket, client: MinecraftClient) {
        CobbledexCollectionGUI.show()
    }
}