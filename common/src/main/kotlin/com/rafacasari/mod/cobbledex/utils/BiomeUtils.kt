package com.rafacasari.mod.cobbledex.utils

import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import net.minecraft.world.World
import net.minecraft.world.biome.Biome

object BiomeUtils {
    data class CobbledexBiome(val identifier: Identifier, val biome: Biome)

    fun getBiomesRegistry(world: World) : Registry<Biome> {
        return world.registryManager.get(RegistryKeys.BIOME)
    }

    fun getAllBiomes(world: World) : List<CobbledexBiome> {
        val registry: Registry<Biome> = world.registryManager.get(RegistryKeys.BIOME)
        return registry.entrySet.map { entry ->  CobbledexBiome(entry.key.value, entry.value) }
    }
}