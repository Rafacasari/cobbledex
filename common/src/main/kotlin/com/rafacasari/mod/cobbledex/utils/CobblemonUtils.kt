package com.rafacasari.mod.cobbledex.utils

import com.cobblemon.mod.common.api.drop.ItemDropEntry
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.spawning.CobblemonSpawnPools
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.cobblemon.mod.common.client.gui.battle.BattleOverlay.Companion.PORTRAIT_DIAMETER
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.client.render.models.blockbench.repository.PokemonModelRepository
import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.pokemon.FormData
import com.cobblemon.mod.common.pokemon.Species
import com.mojang.blaze3d.systems.RenderSystem
import com.rafacasari.mod.cobbledex.utils.MiscUtils.logError
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.DiffuseLighting
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.RotationAxis
import org.joml.Quaternionf
import org.joml.Vector3f

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

    private val formCache = mutableMapOf<Identifier, List<FormData>>()
    val Species.validForms: List<FormData>
        get() = formCache.getOrPut(this.resourceIdentifier) {
            this.forms.filter {
                PokemonModelRepository.variations[this.resourceIdentifier]?.variations?.any { x ->
                    x.model != null && x.aspects.containsAll(it.aspects)
                } ?: false
            }
        }

    private val canSpawnCache: MutableMap<String, Boolean> = mutableMapOf()
    fun FormData.canSpawn() : Boolean {
        if (canSpawnCache[this.name] == null)
            canSpawnCache[this.name] = CobblemonSpawnPools.WORLD_SPAWN_POOL.filterIsInstance<PokemonSpawnDetail>().any {
                it.pokemon.aspects == this.aspects
            }

        return canSpawnCache[this.name]!!
    }


    fun getPokemonDrops(form: FormData) : List<ItemDropEntry> {
        return form.drops.entries.filterIsInstance<ItemDropEntry>()
    }


    // TODO: Implement cache to prevent pokedex FPS lag
    // private val cachedPortrait: MutableMap<String, Framebuffer> = mutableMapOf()

    fun drawBlackSilhouettePokemon(species: Identifier, aspects: Set<String>, matrixStack: MatrixStack, rotation: Quaternionf, scale: Float = 20F) {
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
        // Use BufferedImage to reduce lag?
        val buffer = bufferSource.getBuffer(renderType)

        model.withLayerContext(bufferSource, null, PokemonModelRepository.getLayers(species, aspects)) {
            renderBlackModel(model, context, matrixStack, buffer, -100, -1)
            bufferSource.draw()
        }
        model.setDefault()
    }


    fun drawPortraitPokemon(
        species: Species,
        aspects: Set<String>,
        matrixStack: MatrixStack,
        scale: Float = 13F,
        reversed: Boolean = false,
        blackSilhouette: Boolean
    ) {
        val model = PokemonModelRepository.getPoser(species.resourceIdentifier, aspects)
        val texture = PokemonModelRepository.getTexture(species.resourceIdentifier, aspects, 0f)

        val context = RenderContext()
        PokemonModelRepository.getTextureNoSubstitute(species.resourceIdentifier, aspects, 0f).let { it -> context.put(RenderContext.TEXTURE, it) }
        context.put(RenderContext.SCALE, species.getForm(aspects).baseScale)
        context.put(RenderContext.SPECIES, species.resourceIdentifier)
        context.put(RenderContext.ASPECTS, aspects)

        val renderType = model.getLayer(texture)

        RenderSystem.applyModelViewMatrix()
        val quaternion1 = RotationAxis.POSITIVE_Y.rotationDegrees(-32F * if (reversed) -1F else 1F)
        val quaternion2 = RotationAxis.POSITIVE_X.rotationDegrees(5F)

        model.setupAnimStateless(setOf(PoseType.PORTRAIT, PoseType.PROFILE))

        matrixStack.push()
        matrixStack.translate(0.0, PORTRAIT_DIAMETER.toDouble() + 2.0, 0.0)
        matrixStack.scale(scale, scale, -scale)
        matrixStack.translate(0.0, -PORTRAIT_DIAMETER / 18.0, 0.0)
        matrixStack.translate(model.portraitTranslation.x * if (reversed) -1F else 1F, model.portraitTranslation.y, model.portraitTranslation.z - 4)
        matrixStack.scale(model.portraitScale, model.portraitScale, 1 / model.portraitScale)
        matrixStack.multiply(quaternion1)
        matrixStack.multiply(quaternion2)

        val light1 = Vector3f(0.2F, 1.0F, -1.0F)
        val light2 = Vector3f(0.1F, 0.0F, 8.0F)
        RenderSystem.setShaderLights(light1, light2)
        quaternion1.conjugate()

        val immediate = MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers
        val buffer = immediate.getBuffer(renderType)
        val packedLight = LightmapTextureManager.pack(11, 7)

        model.withLayerContext(immediate, null, PokemonModelRepository.getLayers(species.resourceIdentifier, aspects)) {
            if (blackSilhouette)
                renderBlackModel(model, context, matrixStack, buffer, -100, -1)
            else
                model.render(context, matrixStack, buffer, packedLight, OverlayTexture.DEFAULT_UV, 1F, 1F, 1F, 1F)

            immediate.draw()
        }

        matrixStack.pop()
        model.setDefault()

        DiffuseLighting.enableGuiDepthLighting()
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