package com.rafacasari.mod.cobbledex.utils

import com.cobblemon.mod.common.api.drop.ItemDropEntry
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.spawning.CobblemonSpawnPools
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.client.render.models.blockbench.repository.PokemonModelRepository
import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.pokemon.FormData
import com.cobblemon.mod.common.pokemon.Species
import com.mojang.blaze3d.systems.RenderSystem
import com.rafacasari.mod.cobbledex.utils.MiscUtils.logError
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import org.joml.Quaternionf


object CobblemonUtils {

    fun Species.getFormByName(name: String) : FormData {
        return forms.find { it.name == name } ?: this.standardForm
    }

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


    fun drawBlackSilhouettePokemon(
        species: Identifier,
        aspects: Set<String>,
        matrixStack: MatrixStack,
        rotation: Quaternionf,
        scale: Float = 20F
    ) {
        var model: PokemonPoseableModel? = null
        try {
            model = PokemonModelRepository.getPoser(species, aspects)
        } catch (e: Exception) {
            logError("Failed to load poser for $species")
            e.printStackTrace()
        }

        if (model == null) return
        val texture = PokemonModelRepository.getTexture(species, aspects, 0F)

        val context = RenderContext()
        PokemonModelRepository.getTextureNoSubstitute(species, aspects, 0f).let { it -> context.put(RenderContext.TEXTURE, it) }
        context.put(RenderContext.SCALE, PokemonSpecies.getByIdentifier(species)!!.getForm(aspects).baseScale)
        context.put(RenderContext.SPECIES, species)
        context.put(RenderContext.ASPECTS, aspects)

        val renderType = model.getLayer(texture)

        RenderSystem.applyModelViewMatrix()
        matrixStack.scale(scale, scale, -scale)


        model.setupAnimStateless(PoseType.PROFILE)

        matrixStack.translate(model.profileTranslation.x, model.profileTranslation.y,  model.profileTranslation.z - 4.0)
        matrixStack.scale(model.profileScale, model.profileScale, 1 / model.profileScale)

        matrixStack.multiply(rotation)

        val entityRenderDispatcher = MinecraftClient.getInstance().entityRenderDispatcher
        rotation.conjugate()
        entityRenderDispatcher.rotation = rotation

        val bufferSource = MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers
        val buffer = bufferSource.getBuffer(renderType)

        model.withLayerContext(bufferSource, null, PokemonModelRepository.getLayers(species, aspects)) {
            renderBlackModel(model, context, matrixStack, buffer, -100, -1)
            bufferSource.draw()
        }
        model.setDefault()
    }

    private fun renderBlackModel(
        model: PokemonPoseableModel,
        context: RenderContext,
        stack: MatrixStack,
        buffer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        r: Float = 1f,
        g: Float = 1f,
        b: Float = 1f,
        a: Float = 1f
    ) {
        model.rootPart.render(
            context,
            stack,
            buffer,
            packedLight,
            packedOverlay,
            model.red * r,
            model.green * g,
            model.blue * b,
            model.alpha * a
        )

        val provider = model.bufferProvider
        if (provider != null) {
            for (layer in model.currentLayers) {
                val texture = layer.texture?.invoke(model.currentState?.animationSeconds ?: 0F) ?: continue
                val renderLayer = model.getLayer(texture, emissive = false, translucent = false)
                val consumer = provider.getBuffer(renderLayer)
                stack.push()
                model.rootPart.render(
                    context,
                    stack,
                    consumer,
                    packedLight,
                    packedOverlay,
                    layer.tint.x,
                    layer.tint.y,
                    layer.tint.z,
                    layer.tint.w
                )
                stack.pop()
            }
        }
    }
}