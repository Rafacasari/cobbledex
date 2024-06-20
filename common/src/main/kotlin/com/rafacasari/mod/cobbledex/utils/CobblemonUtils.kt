package com.rafacasari.mod.cobbledex.utils

import com.cobblemon.mod.common.api.drop.ItemDropEntry
import com.cobblemon.mod.common.api.spawning.CobblemonSpawnPools
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.cobblemon.mod.common.pokemon.Species

object CobblemonUtils {

    fun removeUnnecessaryAspects(pokeAspects: Set<String>) : Set<String> {
        return pokeAspects.filter {
            (it != "male" && it != "female" && it != "shiny")
        }.toSet()
    }

    fun getSpawnDetails(species: Species, aspects: Set<String>) : List<PokemonSpawnDetail> {
        // Ignore male n female conditions
        val pokeAspects = removeUnnecessaryAspects(aspects)

        val cobblemonSpawnPool = CobblemonSpawnPools.WORLD_SPAWN_POOL

        val spawnDetails = cobblemonSpawnPool
            .filterIsInstance<PokemonSpawnDetail>()
            .filter {
                it.pokemon.species != null &&
                it.pokemon.species == species.resourceIdentifier.path &&
                it.pokemon.aspects == pokeAspects
            }

        return spawnDetails
    }


    fun getPokemonDrops(species: Species) : List<ItemDropEntry> {
        val drops = species.drops.entries.filterIsInstance<ItemDropEntry>()

        return drops
    }
}