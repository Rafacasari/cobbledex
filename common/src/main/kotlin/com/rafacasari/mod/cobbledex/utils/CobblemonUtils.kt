package com.rafacasari.mod.cobbledex.utils

import com.cobblemon.mod.common.api.drop.ItemDropEntry
import com.cobblemon.mod.common.api.pokemon.evolution.Evolution
import com.cobblemon.mod.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cobblemon.mod.common.api.spawning.CobblemonSpawnPools
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.cobblemon.mod.common.pokemon.Species

object CobblemonUtils {

//    private var COBBLEDEX_POOL: SpawnPool? = null
//    private fun getSpawnPool() : SpawnPool? {
//        if (COBBLEDEX_POOL == null || COBBLEDEX_POOL!!.count() == 0)
//            COBBLEDEX_POOL = SpawnPool("world").addPrecalculators(ContextPrecalculation, BucketPrecalculation)
//
//        return COBBLEDEX_POOL
//    }

//    fun getSpawnDetails(species: Species) : List<PokemonSpawnDetail> {
//
//        // Works on single-player, but not on multiplayer
//        // Also works if you join single-player first, then multiplayer (lol)
//        val cobblemonSpawnPool = CobblemonSpawnPools.WORLD_SPAWN_POOL
//
//        val spawnDetails = cobblemonSpawnPool.filter { x ->
//            x is PokemonSpawnDetail && x.pokemon.species != null && x.pokemon.species == species.resourceIdentifier.path
//        }.map { x -> x as PokemonSpawnDetail }
//
//        return spawnDetails
//    }

    fun getSpawnDetails(species: Species, aspects: Set<String>) : List<PokemonSpawnDetail> {

        // Ignore male n female conditions
        val pokeAspects = aspects.filter {
            (it != "male" && it != "female")
        }.toSet()

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


    // TODO: Create a serializable/encodable class "SerializablePokemonEvolution" that returns:
    //  - Identifier
    //  - FormData
    //  - Aspects
    //  - Evolution Requirements (with a SerializableEvolutionRequirement class)

    fun getPokemonEvolutionRequirements(species: Species) : List<Pair<Evolution, MutableSet<EvolutionRequirement>>> {
        val evolutionsWithRequirements = species.evolutions.map {
            it to it.requirements
        }

        return evolutionsWithRequirements
    }
}