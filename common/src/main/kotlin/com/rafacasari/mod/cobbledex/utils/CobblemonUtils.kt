package com.rafacasari.mod.cobbledex.utils

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

    fun getSpawnDetails(species: Species) : List<PokemonSpawnDetail> {

        // Works on single-player, but not on multiplayer
        // Also works if you join single-player first, then multiplayer (lol)
        val cobblemonSpawnPool = CobblemonSpawnPools.WORLD_SPAWN_POOL

        val spawnDetails = cobblemonSpawnPool.filter { x ->
            x is PokemonSpawnDetail && x.pokemon.species != null && x.pokemon.species == species.resourceIdentifier.path
        }.map { x -> x as PokemonSpawnDetail }

        return spawnDetails
    }
}