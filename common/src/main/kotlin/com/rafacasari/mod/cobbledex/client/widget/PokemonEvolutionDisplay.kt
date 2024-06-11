package com.rafacasari.mod.cobbledex.client.widget

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.gui.drawPortraitPokemon
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.client.CobblemonResources
import com.cobblemon.mod.common.client.gui.summary.SummaryButton
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import com.rafacasari.mod.cobbledex.client.gui.CobbledexGUI
import com.rafacasari.mod.cobbledex.utils.cobbledexResource

class PokemonEvolutionDisplay(x: Int, y: Int): CobbledexScrollList<PokemonEvolutionDisplay.EvolveSlot>(x, y, SLOT_HEIGHT + SLOT_SPACING) {
    companion object {
        const val SLOT_HEIGHT = 25
        const val SLOT_SPACING = 5
        const val PORTRAIT_DIAMETER = 25
        const val PORTRAIT_OFFSET_X = 53
        const val PORTRAIT_OFFSET_Y = 0

        private val portraitBackground = cobbledexResource("textures/gui/evolution_slot_background.png")
        private val slotOverlay = cobbledexResource("textures/gui/evolution_slot_overlay.png")

        private val buttonResource = cobbledexResource("textures/gui/evolution_button.png")
    }

    public override fun addEntry(entry: EvolveSlot): Int {
        return super.addEntry(entry)
    }

//    fun selectPokemon(pokemon: Pokemon?) {
//        clearEntries()
//
//        pokemon?.form?.evolutions?.map {
//            EvolveSlot(it.result.create()) }?.forEach { entry ->
//            this.addEntry(entry)
//        }
//    }

    fun clearEvolutions() = clearEntries()


    fun selectEvolutions(pokemonList: List<Pokemon>?) {
        clearEntries()

        pokemonList?.map {
            EvolveSlot(it)
        }?.forEach { entry ->
            this.addEntry(entry)
        }
    }

    class EvolveSlot(private val evolution: Pokemon) : Entry<EvolveSlot>() {
        val client: MinecraftClient = MinecraftClient.getInstance()

        private val selectButton: SummaryButton = SummaryButton(
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
    }
}