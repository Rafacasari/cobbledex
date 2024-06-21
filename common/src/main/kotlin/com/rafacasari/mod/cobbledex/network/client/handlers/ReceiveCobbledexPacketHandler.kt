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
//            val evolutions = packet.evolutionList.mapNotNull {
//                val species = PokemonSpecies.getByIdentifier(it.first)
//                if (species != null) Pair(species, it.second) else null
//            }

            val preEvolutions = packet.preevolutionList.mapNotNull {
                val species = PokemonSpecies.getByIdentifier(it.first)
                if (species != null) Pair(species, it.second) else null
            }
//
//            val forms = packet.formList.mapNotNull {
//                val species = PokemonSpecies.getByIdentifier(it.first)
//                if (species != null) Pair(species, it.second) else null
//            }

            CobbledexGUI.lastLoadedEvolutions = packet.evolutionList
            CobbledexGUI.lastLoadedPreEvolutions = preEvolutions
            //CobbledexGUI.lastLoadedForms = forms

            CobbledexGUI.Instance?.updateInfoPage(packet.species, packet.spawnDetails, packet.pokemonDrops)
            CobbledexGUI.Instance?.updateRelatedSpecies()

        } catch (e: Exception) {
            logError(e.toString())
        }
    }
}