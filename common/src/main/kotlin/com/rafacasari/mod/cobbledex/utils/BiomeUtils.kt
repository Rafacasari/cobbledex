package com.rafacasari.mod.cobbledex.utils

import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries as RegistryKeys
import net.minecraft.resources.ResourceLocation as Identifier
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.biome.Biome

object BiomeUtils {
    data class CobbledexBiome(val identifier: Identifier, val biome: Biome)

    fun getBiomesRegistry(world: World): Registry<Biome> {
        return world.registryAccess().registryOrThrow(RegistryKeys.BIOME)
    }

    fun getAllBiomes(world: World): List<CobbledexBiome> {
        val registry = getBiomesRegistry(world)
        return registry.entrySet()
            .map { entry -> CobbledexBiome(entry.key.location(), entry.value) }
            .toList()
    }
}
