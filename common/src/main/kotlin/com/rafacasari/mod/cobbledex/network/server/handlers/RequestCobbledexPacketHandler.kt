package com.rafacasari.mod.cobbledex.network.server.handlers

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.rafacasari.mod.cobbledex.network.client.packets.ReceiveCobbledexPacket
import com.rafacasari.mod.cobbledex.network.server.IServerNetworkPacketHandler
import com.rafacasari.mod.cobbledex.network.server.packets.RequestCobbledexPacket
import com.rafacasari.mod.cobbledex.network.template.SerializableItemDrop
import com.rafacasari.mod.cobbledex.network.template.SerializablePokemonSpawnDetail
import com.rafacasari.mod.cobbledex.utils.CobblemonUtils
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity

object RequestCobbledexPacketHandler : IServerNetworkPacketHandler<RequestCobbledexPacket> {
    override fun handle(packet: RequestCobbledexPacket, server: MinecraftServer, player: ServerPlayerEntity) {
        val pokemon = PokemonSpecies.getByIdentifier(packet.pokemon)

        if (pokemon != null ) {

            val evolutions = pokemon.evolutions.filter {
                it.result.species != null
            }.mapNotNull {
                val identifier = PokemonSpecies.getByName(it.result.species!!)?.resourceIdentifier
                if (identifier != null) identifier to it.result.aspects
                else null
            }

            val spawnDetails = CobblemonUtils.getSpawnDetails(pokemon)

            val serializableSpawnDetails = spawnDetails.map {
                SerializablePokemonSpawnDetail(it)
            }

            val drops = CobblemonUtils.getPokemonDrops(pokemon).map {
                SerializableItemDrop(it)
            }

            ReceiveCobbledexPacket(pokemon, evolutions, serializableSpawnDetails, drops).sendToPlayer(player)
        }
    }
}