package com.rafacasari.mod.cobbledex.network.server.handlers

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.rafacasari.mod.cobbledex.network.client.packets.ReceiveCobbledexPacket
import com.rafacasari.mod.cobbledex.network.server.IServerNetworkPacketHandler
import com.rafacasari.mod.cobbledex.network.server.packets.RequestCobbledexPacket
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity

object RequestCobbledexPacketHandler : IServerNetworkPacketHandler<RequestCobbledexPacket> {
    override fun handle(packet: RequestCobbledexPacket, server: MinecraftServer, player: ServerPlayerEntity) {
        val pokemon = PokemonSpecies.getByIdentifier(packet.pokemon)?.create()

        if (pokemon != null ) {
            val evolutions = pokemon.evolutions.map { it.result.create().species.resourceIdentifier }
//            val biomeRegistry = server.registryManager.get(RegistryKeys.BIOME)
//            val allBiomes = BiomeUtils.getAllBiomes(biomeRegistry)
//
//            val spawnDetails = CobblemonUtils.getSpawnDetails(pokemon)
//                .flatMap { spawnDetail ->
//                    spawnDetail.conditions.mapNotNull { y -> y.biomes }.flatten().map { condition ->
//                        val antiConditions = spawnDetail.anticonditions.mapNotNull { y -> y.biomes }.flatten()
//
//                        BiomeChecker(spawnDetail, condition,allBiomes.filter {
//                                b -> condition.fits(b.biome, biomeRegistry) && !antiConditions.any { anti -> anti.fits(b.biome, biomeRegistry) }
//                        }.map {
//                                b -> "biome.${b.identifier.toTranslationKey()}".asTranslated()
//                        })
//                    }
//                }

            ReceiveCobbledexPacket(evolutions).sendToPlayer(player)
        }
    }
}