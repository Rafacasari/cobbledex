package rafacasari.cobbledex.client.widget

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.gui.drawPortraitPokemon
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.client.CobblemonResources
import com.cobblemon.mod.common.client.gui.drawProfilePokemon
import com.cobblemon.mod.common.client.gui.summary.SummaryButton
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.pokemon.FormData
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.Species
import com.cobblemon.mod.common.util.math.fromEulerXYZDegrees
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.util.math.MatrixStack
import rafacasari.cobbledex.client.gui.CobbledexGUI
import rafacasari.cobbledex.utils.cobbledexResource
import org.joml.Quaternionf
import org.joml.Vector3f


import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.repository.PokemonModelRepository
import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity

import com.mojang.blaze3d.systems.RenderSystem

import net.minecraft.client.render.DiffuseLighting

import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.OverlayTexture
import net.minecraft.util.math.RotationAxis


class PokemonEvolutionDisplay(
    x: Int,
    y: Int,
    val pokemon: Pokemon
): CobbledexScrollList<PokemonEvolutionDisplay.EvolveSlot>(
    x,
    y,
    SLOT_HEIGHT + SLOT_SPACING
) {
    companion object {
        const val SLOT_HEIGHT = 25
        const val SLOT_SPACING = 5
        const val PORTRAIT_DIAMETER = 25
        const val PORTRAIT_OFFSET_X = 53
        const val PORTRAIT_OFFSET_Y = 0

        private val slotResource = cobbledexResource("textures/gui/evolution_slot.png")

        private val portraitBackground = cobbledexResource("textures/gui/evolution_slot_background.png")
        private val slotOverlay = cobbledexResource("textures/gui/evolution_slot_overlay.png")

        private val buttonResource = cobbledexResource("textures/gui/evolution_button.png")
    }

    private var entriesCreated = false


    public override fun addEntry(entry: EvolveSlot): Int {
        return super.addEntry(entry)
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, partialTicks: Float) {

        if (!entriesCreated) {
            entriesCreated = true

            pokemon.form.evolutions.map { EvolveSlot(it.result.create()) }.forEach { entry ->
                this.addEntry(entry)
            }
        }
        super.render(context, mouseX, mouseY, partialTicks)
    }

    class EvolveSlot(private val evolution: Pokemon) : Entry<EvolveSlot>() {
        val client: MinecraftClient = MinecraftClient.getInstance()
        val form: FormData = evolution.form
        val selectButton: SummaryButton = SummaryButton(
            buttonX = 0F,
            buttonY = 0F,
            buttonWidth = 40,
            buttonHeight = 10,
            clickAction = {
                CobbledexGUI.Instance?.setPreviewPokemon(evolution)
            },
            text = "View".text(),
            resource = buttonResource,
            boldText = true,
            largeText = false,
            textScale = 0.5F
        )

        override fun getNarration() = evolution.species.translatedName

        override fun render(
            context: DrawContext,
            index: Int,
            rowTop: Int,
            rowLeft: Int,
            rowWidth: Int,
            rowHeight: Int,
            mouseX: Int,
            mouseY: Int,
            isHovered: Boolean,
            partialTicks: Float
        ) {
            val x = rowLeft - 3
            val y = rowTop
            val matrices = context.matrices

//            blitk(
//                matrixStack = matrices,
//                texture = slotResource,
//                x = x,
//                y = y,
//                height = SLOT_HEIGHT,
//                width = rowWidth
//            )




//            // Render Pokémon
//            matrices.push()
//            matrices.translate(x + (PORTRAIT_DIAMETER / 2) + 53.0, y - 3.0, 0.0)
//            matrices.scale(2.5F, 2.5F, 1F)
//
//            drawProfilePokemon(
//                species = this.evolution.species.resourceIdentifier,
//                aspects = this.evolution.aspects,
//                matrixStack = matrices,
//                rotation = Quaternionf().fromEulerXYZDegrees(Vector3f(13F, 35F, 0F)),
//                state = null,
//                scale = 5.5F,
//                partialTicks = partialTicks
//            )
//            matrices.pop()

            blitk(
                matrixStack = matrices,
                texture = portraitBackground,
                x = x + PORTRAIT_OFFSET_X,
                y = y + PORTRAIT_OFFSET_Y,
                height = PORTRAIT_DIAMETER,
                width = PORTRAIT_DIAMETER
            )

            context.enableScissor(
                x + PORTRAIT_OFFSET_X,
                y + PORTRAIT_OFFSET_Y,
                x + PORTRAIT_OFFSET_X + PORTRAIT_DIAMETER,
                y + PORTRAIT_OFFSET_Y + PORTRAIT_DIAMETER
            )

            matrices.push()
            matrices.translate(
                x + PORTRAIT_OFFSET_X + PORTRAIT_DIAMETER / 2.0 - 1.0,
                y.toDouble() - 10,
                0.0
            )

            drawPortraitPokemon(this.evolution.species, this.evolution.aspects, matrices, partialTicks = partialTicks, reversed = true)

            matrices.pop()
            context.disableScissor()


            blitk(
                matrixStack = matrices,
                texture = slotOverlay,
                x = x,
                y = y,
                height = SLOT_HEIGHT,
                width = rowWidth
            )

            selectButton.setPosFloat(x + 1F, y + 13F)
            selectButton.render(context, mouseX, mouseY, partialTicks)

            drawScaledText(
                context = context,
                font = CobblemonResources.DEFAULT_LARGE,
                text = evolution.species.translatedName.bold(),
                x = x + 3.7f,
                y = y + 1f,
                shadow = true,
                centered = false
            )


        }

        override fun mouseClicked(d: Double, e: Double, i: Int): Boolean {
            if (selectButton.isHovered) {
                selectButton.onPress()
                return true
            }
            return false
        }

        fun drawEvolutionPortrait(
            species: Species,
            aspects: Set<String>,
            matrixStack: MatrixStack,
            scale: Float = 13F,
            reversed: Boolean = false,
            state: PoseableEntityState<PokemonEntity>? = null,
            partialTicks: Float
        ) {
            val model = PokemonModelRepository.getPoser(species.resourceIdentifier, aspects)
            val texture = PokemonModelRepository.getTexture(species.resourceIdentifier, aspects, state?.animationSeconds ?: 0F)

            val context = RenderContext()
            PokemonModelRepository.getTextureNoSubstitute(species.resourceIdentifier, aspects, 0f).let { it -> context.put(RenderContext.TEXTURE, it) }
            context.put(RenderContext.SCALE, species.getForm(aspects).baseScale)
            context.put(RenderContext.SPECIES, species.resourceIdentifier)
            context.put(RenderContext.ASPECTS, aspects)

            val renderType = model.getLayer(texture)

            RenderSystem.applyModelViewMatrix()
            val quaternion1 = RotationAxis.POSITIVE_Y.rotationDegrees(-32F * if (reversed) -1F else 1F)
            val quaternion2 = RotationAxis.POSITIVE_X.rotationDegrees(5F)

            if (state == null) {
                model.setupAnimStateless(setOf(PoseType.PORTRAIT, PoseType.PROFILE))
            } else {
                val originalPose = state.currentPose
                model.getPose(PoseType.PORTRAIT)?.let { state.setPose(it.poseName) }
                state.timeEnteredPose = 0F
                state.updatePartialTicks(partialTicks)
                model.setupAnimStateful(null, state, 0F, 0F, 0F, 0F, 0F)
                originalPose?.let { state.setPose(it) }
            }

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

            model.withLayerContext(immediate, state, PokemonModelRepository.getLayers(species.resourceIdentifier, aspects)) {
                model.render(context, matrixStack, buffer, packedLight, OverlayTexture.DEFAULT_UV, 1F, 1F, 1F, 1F)
                immediate.draw()
            }

            matrixStack.pop()
            model.setDefault()

            DiffuseLighting.enableGuiDepthLighting()
        }

    }
}