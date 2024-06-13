package com.rafacasari.mod.cobbledex.network.server.handlers

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.rafacasari.mod.cobbledex.network.client.packets.ReceiveCobbledexPacket
import com.rafacasari.mod.cobbledex.network.server.IServerNetworkPacketHandler
import com.rafacasari.mod.cobbledex.network.server.packets.RequestCobbledexPacket
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
                PokemonSpecies.getByName(it.result.species!!)?.resourceIdentifier
            }

            val spawnDetails = CobblemonUtils.getSpawnDetails(pokemon)

//            val details = Seriali(spawnDetails.associate {
//                conditions -> conditions.id to conditions.conditions.map { SerializableSpawnCondition(it) }
//            })

            val serializableSpawnDetails = spawnDetails.map {
                SerializablePokemonSpawnDetail(it)
            }

            ReceiveCobbledexPacket(evolutions, serializableSpawnDetails).sendToPlayer(player)
        }
    }
}