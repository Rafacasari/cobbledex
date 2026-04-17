package com.rafacasari.mod.cobbledex.utils

import com.cobblemon.mod.common.api.drop.ItemDropEntry
import com.cobblemon.mod.common.api.gui.drawPosablePortrait
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.spawning.CobblemonSpawnPools
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.cobblemon.mod.common.client.gui.drawProfilePokemon
import com.cobblemon.mod.common.client.render.models.blockbench.FloatingState
import com.cobblemon.mod.common.client.render.models.blockbench.repository.VaryingModelRepository
import com.cobblemon.mod.common.pokemon.FormData
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.pokemon.Species
import com.mojang.blaze3d.vertex.PoseStack as MatrixStack
import net.minecraft.resources.ResourceLocation as Identifier
import org.joml.Quaternionf

@Suppress("unused")
object CobblemonUtils {

    fun removeUnnecessaryAspects(pokeAspects: Set<String>): Set<String> {
        return pokeAspects.filter {
            it != "male" && it != "female" && it != "shiny"
        }.toSet()
    }

    fun getSpawnDetails(species: Species, aspects: Set<String>): List<PokemonSpawnDetail> {
        val pokeAspects = removeUnnecessaryAspects(aspects)

        return CobblemonSpawnPools.WORLD_SPAWN_POOL
            .filterIsInstance<PokemonSpawnDetail>()
            .filter {
                it.pokemon.species != null &&
                    it.pokemon.species == species.resourceIdentifier.path &&
                    it.pokemon.aspects == pokeAspects
            }
    }

    private val formCache = mutableMapOf<Identifier, List<FormData>>()
    val Species.validForms: List<FormData>
        get() = formCache.getOrPut(resourceIdentifier) {
            forms.filter { form ->
                VaryingModelRepository.variations[resourceIdentifier]?.variations?.any { variation ->
                    variation.model != null && variation.aspects.containsAll(form.aspects)
                } ?: false
            }
        }

    private val canSpawnCache: MutableMap<String, Boolean> = mutableMapOf()
    fun FormData.canSpawn(): Boolean {
        if (canSpawnCache[name] == null) {
            canSpawnCache[name] = CobblemonSpawnPools.WORLD_SPAWN_POOL
                .filterIsInstance<PokemonSpawnDetail>()
                .any { it.pokemon.aspects == aspects }
        }

        return canSpawnCache[name] == true
    }

    fun getPokemonDrops(form: FormData): List<ItemDropEntry> {
        return form.drops.entries.filterIsInstance<ItemDropEntry>()
    }

    fun drawBlackSilhouettePokemon(
        species: Identifier,
        aspects: Set<String>,
        matrixStack: MatrixStack,
        rotation: Quaternionf,
        scale: Float = 20F
    ) {
        val resolvedSpecies = PokemonSpecies.getByIdentifier(species) ?: return

        drawProfilePokemon(
            renderablePokemon = RenderablePokemon(resolvedSpecies, aspects),
            matrixStack = matrixStack,
            rotation = rotation,
            state = FloatingState(),
            partialTicks = 0F,
            scale = scale,
            r = 0F,
            g = 0F,
            b = 0F,
            a = 1F
        )
    }

    fun drawPortraitPokemon(
        species: Species,
        aspects: Set<String>,
        matrixStack: MatrixStack,
        scale: Float = 13F,
        reversed: Boolean = false,
        blackSilhouette: Boolean
    ) {
        val state = FloatingState().also { it.currentAspects = aspects }

        drawPosablePortrait(
            identifier = species.resourceIdentifier,
            matrixStack = matrixStack,
            scale = scale,
            contextScale = species.getForm(aspects).baseScale,
            reversed = reversed,
            state = state,
            partialTicks = 0F,
            doQuirks = false,
            r = if (blackSilhouette) 0F else 1F,
            g = if (blackSilhouette) 0F else 1F,
            b = if (blackSilhouette) 0F else 1F,
            a = 1F
        )
    }
}
