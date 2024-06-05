package rafacasari.cobbledex.utils

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import net.minecraft.world.World
import net.minecraft.world.biome.Biome

data class CobbledexBiome(val identifier: Identifier, val biome: Biome)

object BiomeUtils {

    fun getAllBiomes(world: World) : List<CobbledexBiome> {
        val registry: Registry<Biome> = world.registryManager.get(RegistryKeys.BIOME)
        return registry.entrySet.map { entry ->  CobbledexBiome(entry.key.value, entry.value)}
    }


    fun canSpawnAt (biome: Biome,  world: World, condition: SpawningCondition<*>) : Boolean {

        val registry: Registry<Biome> = world.registryManager.get(RegistryKeys.BIOME)
        if (condition.biomes == null) return false


        val count = condition.biomes!!.count { biomeCondition ->
                biomeCondition.fits(
                    biome,
                    registry
                )
            }

        return count > 0
    }
}