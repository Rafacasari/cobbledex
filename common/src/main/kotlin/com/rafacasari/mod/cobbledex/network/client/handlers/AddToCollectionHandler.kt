package com.rafacasari.mod.cobbledex.network.client.handlers

import com.rafacasari.mod.cobbledex.client.gui.CobbledexCollectionGUI
import com.rafacasari.mod.cobbledex.network.client.packets.AddToCollectionPacket
import com.rafacasari.mod.cobbledex.network.client.IClientNetworkPacketHandler
import net.minecraft.client.MinecraftClient

object AddToCollectionHandler : IClientNetworkPacketHandler<AddToCollectionPacket> {
    override fun handle(packet: AddToCollectionPacket, client: MinecraftClient) {
//        CobbledexItem.totalPokemonDiscovered = packet.discoveredCount
        CobbledexCollectionGUI.discoveredList?.add(packet.nationalNumber)
    }
}