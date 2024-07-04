package com.rafacasari.mod.cobbledex.network.server.handlers

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.rafacasari.mod.cobbledex.Cobbledex
import com.rafacasari.mod.cobbledex.network.client.packets.ReceiveCobbledexPacket
import com.rafacasari.mod.cobbledex.network.server.IServerNetworkPacketHandler
import com.rafacasari.mod.cobbledex.network.server.packets.RequestCobbledexPacket
import com.rafacasari.mod.cobbledex.network.template.SerializableItemDrop
import com.rafacasari.mod.cobbledex.network.template.SerializablePokemonEvolution
import com.rafacasari.mod.cobbledex.network.template.SerializablePokemonSpawnDetail
import com.rafacasari.mod.cobbledex.utils.CobblemonUtils
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity

object RequestCobbledexPacketHandler : IServerNetworkPacketHandler<RequestCobbledexPacket> {
    override fun handle(packet: RequestCobbledexPacket, server: MinecraftServer, player: ServerPlayerEntity) {
        val pokemon = PokemonSpecies.getByIdentifier(packet.pokemon)

        if (pokemon != null ) {
            val form = pokemon.getForm(packet.aspects)

            val serverConfig = Cobbledex.getConfig()
            
            val evolutions = if (serverConfig.ShowEvolutions_IsEnabled) form.evolutions.mapNotNull {
                it.result.species?.let { evoSpeciesName ->
                    val evoSpecies = PokemonSpecies.getByName(evoSpeciesName)
                    if(evoSpecies != null)
                        SerializablePokemonEvolution(it)
                    else null
                }
            } else listOf()

            // Select all pre-evolution forms or just the default form
            val preEvolutions = form.preEvolution?.let { preEvolution ->
                val forms =
                    if (preEvolution.species.forms.isEmpty()) setOf(preEvolution.form)
                    else preEvolution.species.forms.toSet()

                return@let forms.map {
                    it.species.resourceIdentifier to it.aspects.toSet()
                }
            } ?: listOf()

            val spawnDetails = CobblemonUtils.getSpawnDetails(pokemon, packet.aspects)
            val serializableSpawnDetails = if(serverConfig.HowToFind_IsEnabled) spawnDetails.map {
                SerializablePokemonSpawnDetail(it)
            } else listOf()

            val serializableItemDrops: List<SerializableItemDrop> = if(serverConfig.ItemDrops_IsEnabled) CobblemonUtils.getPokemonDrops(form).map {
                SerializableItemDrop(it)
            } else listOf()

            ReceiveCobbledexPacket(pokemon, evolutions, preEvolutions, serializableSpawnDetails, serializableItemDrops).sendToPlayer(player)
        }
    }
}