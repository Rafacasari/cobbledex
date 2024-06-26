package com.rafacasari.mod.cobbledex.network.client.handlers

import com.rafacasari.mod.cobbledex.client.gui.CobbledexCollectionGUI
import com.rafacasari.mod.cobbledex.network.client.packets.AddToCollectionPacket
import com.rafacasari.mod.cobbledex.network.client.IClientNetworkPacketHandler
import net.minecraft.client.MinecraftClient

object AddToCollectionHandler : IClientNetworkPacketHandler<AddToCollectionPacket> {
    override fun handle(packet: AddToCollectionPacket, client: MinecraftClient) {

        val speciesRegister = CobbledexCollectionGUI.discoveredList[packet.species]

        if (speciesRegister == null) {
            // Register not found, create it
            CobbledexCollectionGUI.discoveredList[packet.species] = mutableMapOf(packet.form to packet.entry)
        }
        else speciesRegister[packet.form] = packet.entry
    }
}