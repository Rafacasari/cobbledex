package com.rafacasari.mod.cobbledex.client.gui

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.client.CobblemonResources
import com.cobblemon.mod.common.client.gui.drawProfilePokemon
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.util.math.fromEulerXYZDegrees
import com.rafacasari.mod.cobbledex.client.widget.ImageButton
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

        private val DOUBLE_LEFT_ARROW: Identifier = cobbledexResource("textures/gui/collection/double_left_arrow.png")
        private val DOUBLE_RIGHT_ARROW: Identifier = cobbledexResource("textures/gui/collection/double_right_arrow.png")
        private val LEFT_ARROW: Identifier = cobbledexResource("textures/gui/collection/left_arrow.png")
        private val RIGHT_ARROW: Identifier = cobbledexResource("textures/gui/collection/right_arrow.png")

        fun show() {
            val instance = CobbledexCollectionGUI()
            MinecraftClient.getInstance().setScreen(instance)
        }

        fun playSound(soundEvent: SoundEvent) {
            MinecraftClient.getInstance().soundManager.play(PositionedSoundInstance.master(soundEvent, 1f))
        }

        private var currentPage = 1
        private var maxPages = 1

        val implementedSpecies by lazy {
            return@lazy PokemonSpecies.implemented.toSortedSet(compareBy {
                it.nationalPokedexNumber
            }).toList()
        }

    }

    override fun init() {
        val x = (width - CobbledexGUI.BASE_WIDTH) / 2
        val y = (height - CobbledexGUI.BASE_HEIGHT) / 2

        addDrawableChild(ImageButton(DOUBLE_LEFT_ARROW, 14, 11, x + 131, y + 175) {
            currentPage = 1
        })

        addDrawableChild(ImageButton(LEFT_ARROW, 8, 11, x + 156, y + 175) {
            if (currentPage <= 1)
                currentPage = maxPages
            else currentPage--
        })

        addDrawableChild(ImageButton(RIGHT_ARROW, 8, 11, x + 229, y + 175) {
            if (currentPage >= maxPages)
                currentPage = 1
            else currentPage++
        })

        addDrawableChild(ImageButton(DOUBLE_RIGHT_ARROW, 14, 11, x + 248, y + 175) {
            currentPage = maxPages
        })

        val totalItems = (COLUMN_SIZE * LINES_SIZE)
        // Get total items
        maxPages =  (implementedSpecies.size + totalItems - 1) / totalItems
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
            y = y + 13.25F,
            shadow = false,
            centered = true,
            scale = 1f
        )

        drawScaledText(
            context = context,
            font = CobblemonResources.DEFAULT_LARGE,
            text = "$currentPage / $maxPages".text().bold(),
            x = x + 196F,
            y = y + 173F,
            shadow = false,
            centered = true,
            scale = 1.5F
        )

        val padding = 0.5
        val minWidth = AREA_WIDTH / (COLUMN_SIZE + padding)
        val minHeight = AREA_HEIGHT / (LINES_SIZE + padding)
        val minSize = minOf(minWidth, minHeight)
        val horizontalPadding = (AREA_WIDTH - minSize * COLUMN_SIZE) / (COLUMN_SIZE - 1)
        val verticalPadding = (AREA_HEIGHT - minSize * LINES_SIZE) / (LINES_SIZE - 1)

        var currentIndex = 1
        val linesSize = LINES_SIZE - 1
        val columnsSize = COLUMN_SIZE - 1
        for (currentY: Int in 0..linesSize) {
            for (currentX in 0..columnsSize) {
                val currentPokemonIndex = COLUMN_SIZE * LINES_SIZE * (currentPage - 1) + (currentIndex - 1)
                val entryX = x + (83.5F + (minSize * currentX) + (horizontalPadding * currentX))
                val entryY = y + (24.5F + (minSize * currentY) + (verticalPadding * currentY))

                val species = if(implementedSpecies.size > currentPokemonIndex) implementedSpecies[currentPokemonIndex] else null

                species?.let {

                    blitk(
                        matrixStack = matrices,
                        texture = ENTRY_BACKGROUND,
                        x = entryX,  y = entryY,
                        width = minSize,
                        height = minSize
                    )

                    context.enableScissor(
                        entryX.toInt(),
                        entryY.toInt(),
                        entryX.toInt() + minSize.toInt(),
                        entryY.toInt() + minSize.toInt()
                    )


                    matrices.push()
                    matrices.translate(entryX + (minSize / 2.0), entryY, 0.0)

                    val rotation = Quaternionf().fromEulerXYZDegrees(Vector3f(10f, 35f, 0F))


                    if (discoveredList?.contains(species.nationalPokedexNumber) == true) {
                        drawProfilePokemon(
                            species = species.resourceIdentifier,
                            aspects = species.standardForm.aspects.toSet(),
                            matrixStack = matrices,
                            rotation = rotation,
                            state = null,
                            partialTicks = delta,
                            scale = (minSize / 2).toFloat()
                        )
                    } else {

                        drawBlackSilhouettePokemon(
                            species = species.resourceIdentifier,
                            aspects = species.standardForm.aspects.toSet(),
                            matrixStack = matrices,
                            rotation = rotation,
                            scale = (minSize / 2).toFloat()
                        )
                    }
                    matrices.pop()

                    context.disableScissor()

                    drawScaledText(
                        context = context,
                        font = CobblemonResources.DEFAULT_LARGE,
                        text = "#${species.nationalPokedexNumber}".text().bold(),
                        x = entryX + 1.5f,
                        y = entryY + 0.5f,
                        shadow = false,
                        centered = false,
                        scale = 0.5F, opacity = 0.5f
                    )

                }

                currentIndex++

            }
        }

        super.render(context, mouseX, mouseY, delta)
    }
}