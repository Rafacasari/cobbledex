package com.rafacasari.mod.cobbledex.client.gui

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.gui.drawProfilePokemon
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.client.CobblemonResources
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.util.math.fromEulerXYZDegrees
import com.rafacasari.mod.cobbledex.utils.CobblemonUtils.drawBlackSilhouettePokemon
import com.rafacasari.mod.cobbledex.utils.cobbledexResource
import com.rafacasari.mod.cobbledex.utils.cobbledexTextTranslation
import com.rafacasari.mod.cobbledex.utils.cobbledexTranslation
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Identifier
import org.joml.Quaternionf
import org.joml.Vector3f

class CobbledexCollectionGUI : Screen(cobbledexTextTranslation("cobbledex")) {
    companion object {
        var discoveredList: MutableList<Int>? = null

        const val BASE_WIDTH: Int = 349
        const val BASE_HEIGHT: Int = 205
        const val PORTRAIT_SIZE = 58F

        const val LINES_SIZE = 6
        const val COLUMN_SIZE = 11
        const val AREA_WIDTH = 259f
        const val AREA_HEIGHT = 145f

        private val MAIN_BACKGROUND: Identifier = cobbledexResource("textures/gui/collection/collection_base.png")
        private val PORTRAIT_BACKGROUND: Identifier = cobbledexResource("textures/gui/collection/portrait_background.png")
        private val ENTRY_BACKGROUND: Identifier = cobbledexResource("textures/gui/collection/entry_background.png")

        fun show() {
            val instance = CobbledexCollectionGUI()
            MinecraftClient.getInstance().setScreen(instance)
        }

        fun playSound(soundEvent: SoundEvent) {
            MinecraftClient.getInstance().soundManager.play(PositionedSoundInstance.master(soundEvent, 1f))
        }

        private var currentPage = 1
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(context)

        val matrices = context.matrices
        val x = (width - BASE_WIDTH) / 2
        val y = (height - BASE_HEIGHT) / 2

        blitk(
            matrixStack = matrices,
            texture = PORTRAIT_BACKGROUND,
            x = x + 13,
            y = y + 41,
            width = PORTRAIT_SIZE,
            height = PORTRAIT_SIZE
        )

        blitk(
            matrixStack = matrices,
            texture = MAIN_BACKGROUND,
            x = x,  y = y,
            width = BASE_WIDTH,
            height = BASE_HEIGHT
        )

        drawScaledText(
            context = context,
            font = CobblemonResources.DEFAULT_LARGE,
            text = cobbledexTranslation("cobbledex.texts.cobbledex").bold(),
            x = x + 200.25F,
            y = y + 10.5F,
            shadow = false,
            centered = true,
            scale = 1.1f
        )

        val minWidth = AREA_WIDTH / (COLUMN_SIZE + 0.5)
        val minHeight = AREA_HEIGHT / (LINES_SIZE + 0.5)
        val tamanhoQuadrado = minOf(minWidth, minHeight)
        val espacoHorizontal = (AREA_WIDTH - tamanhoQuadrado * COLUMN_SIZE) / (COLUMN_SIZE - 1)
        val espacoVertical = (AREA_HEIGHT - tamanhoQuadrado * LINES_SIZE) / (LINES_SIZE - 1)



        var currentIndex = 1
        val linesSize = LINES_SIZE - 1
        val columnsSize = COLUMN_SIZE - 1
        for (currentY: Int in 0..linesSize) {
            for (currentX in 0..columnsSize) {
                val entryX = x + (83.5F + (tamanhoQuadrado * currentX) + (espacoHorizontal * currentX))
                val entryY = y + (24.5F + (tamanhoQuadrado * currentY) + (espacoVertical * currentY))

                val species = PokemonSpecies.getByPokedexNumber(currentIndex * currentPage)

                species?.let {

                    blitk(
                        matrixStack = matrices,
                        texture = ENTRY_BACKGROUND,
                        x = entryX,  y = entryY,
                        width = tamanhoQuadrado,
                        height = tamanhoQuadrado
                    )

                    context.enableScissor(
                        entryX.toInt(),
                        entryY.toInt(),
                        entryX.toInt() + tamanhoQuadrado.toInt(),
                        entryY.toInt() + tamanhoQuadrado.toInt()
                    )


                    matrices.push()
                    matrices.translate(entryX + (tamanhoQuadrado / 2.0), entryY, 0.0)

                    val rotation = Quaternionf().fromEulerXYZDegrees(Vector3f(10f, 35f, 0F))


                    if (discoveredList?.contains(currentIndex * currentPage) == true) {
                        drawProfilePokemon(
                            species = species.resourceIdentifier,
                            aspects = species.standardForm.aspects.toSet(),
                            matrixStack = matrices,
                            rotation = rotation,
                            state = null,
                            partialTicks = delta,
                            scale = (tamanhoQuadrado / 2).toFloat()
                        )
                    } else {

                        drawBlackSilhouettePokemon(
                            species = species.resourceIdentifier,
                            aspects = species.standardForm.aspects.toSet(),
                            matrixStack = matrices,
                            rotation = rotation,
                            scale = (tamanhoQuadrado / 2).toFloat()
                        )
                    }
                    matrices.pop()

                    context.disableScissor()

                }

                currentIndex++

            }
        }
    }
}