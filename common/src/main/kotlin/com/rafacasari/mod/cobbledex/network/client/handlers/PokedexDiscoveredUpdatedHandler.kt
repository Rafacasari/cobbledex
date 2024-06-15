package com.rafacasari.mod.cobbledex.network.client.handlers


import com.rafacasari.mod.cobbledex.items.CobbledexItem
import com.rafacasari.mod.cobbledex.network.client.packets.PokedexDiscoveredUpdated
import com.rafacasari.mod.cobbledex.network.server.IClientNetworkPacketHandler
import net.minecraft.client.MinecraftClient

object PokedexDiscoveredUpdatedHandler : IClientNetworkPacketHandler<PokedexDiscoveredUpdated> {
    override fun handle(packet: PokedexDiscoveredUpdated, client: MinecraftClient) {
        CobbledexItem.totalPokemonDiscovered = packet.discoveredCount
    }
}