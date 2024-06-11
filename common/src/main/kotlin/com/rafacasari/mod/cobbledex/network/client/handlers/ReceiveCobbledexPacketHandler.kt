package com.rafacasari.mod.cobbledex.network.client.handlers

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.rafacasari.mod.cobbledex.client.gui.CobbledexGUI
import com.rafacasari.mod.cobbledex.network.client.packets.ReceiveCobbledexPacket
import com.rafacasari.mod.cobbledex.network.server.IClientNetworkPacketHandler
import com.rafacasari.mod.cobbledex.utils.logError
import net.minecraft.client.MinecraftClient

object ReceiveCobbledexPacketHandler : IClientNetworkPacketHandler<ReceiveCobbledexPacket> {
    override fun handle(packet: ReceiveCobbledexPacket, client: MinecraftClient) {
        try {
            val evolutions = packet.evolutionList.mapNotNull {
                 PokemonSpecies.getByIdentifier(it)
            }

            CobbledexGUI.Instance?.setEvolutions(evolutions)

        } catch (e: Exception) {
            logError(e.toString())
        }
    }
}