package com.rafacasari.mod.cobbledex.network.client.handlers

import com.rafacasari.mod.cobbledex.CobbledexConstants.Client.discoveredList
import com.rafacasari.mod.cobbledex.network.client.packets.ReceiveCollectionDataPacket
import com.rafacasari.mod.cobbledex.network.client.IClientNetworkPacketHandler
import net.minecraft.client.MinecraftClient

object ReceiveCollectionDataHandler : IClientNetworkPacketHandler<ReceiveCollectionDataPacket> {
    override fun handle(packet: ReceiveCollectionDataPacket, client: MinecraftClient) {
        discoveredList = packet.discoveredList
    }
}