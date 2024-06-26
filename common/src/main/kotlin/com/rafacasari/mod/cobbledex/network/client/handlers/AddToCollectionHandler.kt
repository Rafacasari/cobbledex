package com.rafacasari.mod.cobbledex.network.client.handlers

import com.rafacasari.mod.cobbledex.CobbledexConstants.Client.discoveredList
import com.rafacasari.mod.cobbledex.network.client.packets.AddToCollectionPacket
import com.rafacasari.mod.cobbledex.network.client.IClientNetworkPacketHandler
import net.minecraft.client.MinecraftClient

object AddToCollectionHandler : IClientNetworkPacketHandler<AddToCollectionPacket> {
    override fun handle(packet: AddToCollectionPacket, client: MinecraftClient) {

        val speciesRegister = discoveredList[packet.species]

        if (speciesRegister == null) {
            // Register not found, create it
            discoveredList[packet.species] = mutableMapOf(packet.form to packet.entry)
        }
        else speciesRegister[packet.form] = packet.entry
    }
}