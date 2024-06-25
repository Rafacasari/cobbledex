package com.rafacasari.mod.cobbledex.client.gui

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.client.CobblemonResources
import com.cobblemon.mod.common.client.gui.ExitButton
import com.cobblemon.mod.common.client.gui.TypeIcon
import com.cobblemon.mod.common.client.gui.drawProfilePokemon
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.pokemon.Species
import com.cobblemon.mod.common.util.math.fromEulerXYZDegrees
import com.rafacasari.mod.cobbledex.client.gui.CobbledexGUI.Companion.SCALE
import com.rafacasari.mod.cobbledex.client.gui.CobbledexGUI.Companion.TYPE_SPACER
import com.rafacasari.mod.cobbledex.client.gui.CobbledexGUI.Companion.TYPE_SPACER_DOUBLE
import com.rafacasari.mod.cobbledex.client.widget.ImageButton
import com.rafacasari.mod.cobbledex.client.widget.SilhouetteModelWidget
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
        private const val AREA_WIDTH = 259f
        private const val AREA_HEIGHT = 145f
        private const val PADDING = 0.5

        val ENTRY_SIZE = minOf(AREA_WIDTH / (COLUMN_SIZE + PADDING), AREA_HEIGHT / (LINES_SIZE + PADDING))
        val X_PADDING = (AREA_WIDTH - ENTRY_SIZE * COLUMN_SIZE) / (COLUMN_SIZE - 1)
        val Y_PADDING = (AREA_HEIGHT - ENTRY_SIZE * LINES_SIZE) / (LINES_SIZE - 1)

        private val MAIN_BACKGROUND: Identifier = cobbledexResource("textures/gui/collection/collection_base.png")
        private val PORTRAIT_BACKGROUND: Identifier = cobbledexResource("textures/gui/collection/portrait_background.png")
        private val ENTRY_BACKGROUND: Identifier = cobbledexResource("textures/gui/collection/entry_background.png")
        private val TYPE_SPACER_UNKNOWN: Identifier = cobbledexResource("textures/gui/collection/type_spacer_unknown.png")

        private val DOUBLE_LEFT_ARROW: Identifier = cobbledexResource("textures/gui/collection/double_left_arrow.png")
        private val DOUBLE_RIGHT_ARROW: Identifier = cobbledexResource("textures/gui/collection/double_right_arrow.png")
        private val LEFT_ARROW: Identifier = cobbledexResource("textures/gui/collection/left_arrow.png")
        private val RIGHT_ARROW: Identifier = cobbledexResource("textures/gui/collection/right_arrow.png")

        fun show() {
            playSound(CobblemonSounds.PC_ON)

            val instance = CobbledexCollectionGUI()
            MinecraftClient.getInstance().setScreen(instance)
        }

        fun playSound(soundEvent: SoundEvent) {
            MinecraftClient.getInstance().soundManager.play(PositionedSoundInstance.master(soundEvent, 1f))
        }

        private var currentPage = 1
        private var maxPages = 1

        internal var needReload = true
        private var implementedSpeciesInternal: List<Species>? = null

        var lastHoveredEntry: Species? = null
        val implementedSpecies: List<Species>
            get() {
                if (needReload)
                {
                    currentPage = 1
                    lastHoveredEntry = null
                    implementedSpeciesInternal = PokemonSpecies.implemented.toSortedSet(compareBy {
                        it.nationalPokedexNumber
                    }).toList()

                    needReload = false
                }

                return  implementedSpeciesInternal ?: listOf()
            }

    }


    private var modelWidget: SilhouetteModelWidget? = null
    private var typeWidget: TypeIcon? = null

    override fun init() {
        val x = (width - CobbledexGUI.BASE_WIDTH) / 2
        val y = (height - CobbledexGUI.BASE_HEIGHT) / 2

        this.addDrawableChild(ExitButton(pX = x + 313, pY = y + 175) { this.close() })

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

        loadSpecies(lastHoveredEntry, if (lastHoveredEntry != null) discoveredList?.contains(lastHoveredEntry!!.nationalPokedexNumber) == true else false)
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

        modelWidget?.render(context, mouseX, mouseY, delta)

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

        lastHoveredEntry?.let { hoveredEntry ->
            val isDiscovered =  discoveredList?.contains(hoveredEntry.nationalPokedexNumber) == true
            drawScaledText(
                context = context,
                font = CobblemonResources.DEFAULT_LARGE,
                text = if (isDiscovered) hoveredEntry.translatedName.bold() else "???".text().bold(),
                x = x + 13,
                y = y + 28.3F,
                shadow = false
            )

            if (isDiscovered) {
                blitk(
                    matrixStack = matrices,
                    texture = if (hoveredEntry.secondaryType == null) TYPE_SPACER else TYPE_SPACER_DOUBLE,
                    x = (x + 5.5 + 3) / SCALE,
                    y = (y + 100 + 14) / SCALE,
                    width = 134,
                    height = 24,
                    scale = SCALE
                )

                typeWidget?.render(context)
            }
            else
                blitk(
                    matrixStack = matrices,
                    texture = TYPE_SPACER_UNKNOWN,
                    x = (x + 5.5 + 3) / SCALE,
                    y = (y + 100 + 14) / SCALE,
                    width = 134,
                    height = 24,
                    scale = SCALE
                )


        }


        var currentIndex = 1
        val linesSize = LINES_SIZE - 1
        val columnsSize = COLUMN_SIZE - 1
        for (currentY: Int in 0..linesSize) {
            for (currentX in 0..columnsSize) {
                val currentPokemonIndex = COLUMN_SIZE * LINES_SIZE * (currentPage - 1) + (currentIndex - 1)
                val entryX = x + (83.5F + (ENTRY_SIZE * currentX) + (X_PADDING * currentX))
                val entryY = y + (24.5F + (ENTRY_SIZE * currentY) + (Y_PADDING * currentY))

                val species = if(implementedSpecies.size > currentPokemonIndex) implementedSpecies[currentPokemonIndex] else null

                species?.let {

                    blitk(
                        matrixStack = matrices,
                        texture = ENTRY_BACKGROUND,
                        x = entryX,  y = entryY,
                        width = ENTRY_SIZE,
                        height = ENTRY_SIZE
                    )

                    context.enableScissor(
                        entryX.toInt(),
                        entryY.toInt(),
                        entryX.toInt() + ENTRY_SIZE.toInt(),
                        entryY.toInt() + ENTRY_SIZE.toInt()
                    )


                    matrices.push()
                    matrices.translate(entryX + (ENTRY_SIZE / 2.0), entryY, 0.0)

                    val rotation = Quaternionf().fromEulerXYZDegrees(Vector3f(10f, 35f, 0F))

                    val isDiscovered = discoveredList?.contains(species.nationalPokedexNumber) == true
                    if (isDiscovered) {
                        drawProfilePokemon(
                            species = species.resourceIdentifier,
                            aspects = species.standardForm.aspects.toSet(),
                            matrixStack = matrices,
                            rotation = rotation,
                            state = null,
                            partialTicks = delta,
                            scale = (ENTRY_SIZE / 2).toFloat()
                        )
                    } else {

                        drawBlackSilhouettePokemon(
                            species = species.resourceIdentifier,
                            aspects = species.standardForm.aspects.toSet(),
                            matrixStack = matrices,
                            rotation = rotation,
                            scale = (ENTRY_SIZE / 2).toFloat()
                        )
                    }
                    matrices.pop()

                    context.disableScissor()

                    drawScaledText(
                        context = context,
                        text = "${species.nationalPokedexNumber}".text(),
                        x = entryX + 1.5,
                        y = entryY + 1.5,
                        shadow = false,
                        scale = SCALE, opacity = 0.3
                    )


                    if (lastHoveredEntry != species &&  mouseX.toDouble() in entryX .. (entryX + ENTRY_SIZE) && mouseY.toDouble() in entryY .. (entryY + ENTRY_SIZE)) {
                        lastHoveredEntry = species
                        loadSpecies(species, isDiscovered)
                    }

                }

                currentIndex++

            }
        }

        super.render(context, mouseX, mouseY, delta)
    }

    override fun shouldPause() = false

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val entry = getMouseEntry(mouseX, mouseY)
        if (entry != -1)
        {
            val species = if(implementedSpecies.size > entry) implementedSpecies[entry] else null
            if (species != null) {
                close()
                playSound(CobblemonSounds.PC_CLICK)
                CobbledexGUI.openCobbledexScreen(species.standardForm, setOf(), true)
                return true
            }
        }

        return super.mouseClicked(mouseX, mouseY, button)
    }

    private fun getMouseEntry(mouseX: Double, mouseY: Double): Int {
        val x = (width - CobbledexGUI.BASE_WIDTH) / 2
        val y = (height - CobbledexGUI.BASE_HEIGHT) / 2

        var currentIndex = 1
        val linesSize = LINES_SIZE - 1
        val columnsSize = COLUMN_SIZE - 1
        for (currentY: Int in 0..linesSize) {
            for (currentX in 0..columnsSize) {
                val currentPokemonIndex = COLUMN_SIZE * LINES_SIZE * (currentPage - 1) + (currentIndex - 1)
                val entryX = x + (83.5F + (ENTRY_SIZE * currentX) + (X_PADDING * currentX))
                val entryY = y + (24.5F + (ENTRY_SIZE * currentY) + (Y_PADDING * currentY))

                if (mouseX in entryX .. (entryX + ENTRY_SIZE) && mouseY in entryY .. (entryY + ENTRY_SIZE))
                    return currentPokemonIndex

                currentIndex++
            }
        }


        return -1
    }

    fun loadSpecies(species: Species?, isDiscovered: Boolean = false)
    {
        val x = (width - CobbledexGUI.BASE_WIDTH) / 2
        val y = (height - CobbledexGUI.BASE_HEIGHT) / 2

        if(species != null) {
            modelWidget = SilhouetteModelWidget(
                pX = x + 13,
                pY = y + 41,
                pWidth = CobbledexGUI.PORTRAIT_SIZE,
                pHeight = CobbledexGUI.PORTRAIT_SIZE,
                pokemon = RenderablePokemon(species, setOf()),
                baseScale = 1.8F,
                rotationY = 345F,
                offsetY = -10.0,
                isDiscovered = isDiscovered
            )

            typeWidget = TypeIcon(
                x = x + 39 + 3,
                y = y + 97 + 14,
                type = species.primaryType,
                secondaryType = species.secondaryType,
                doubleCenteredOffset = 14f / 2,
                secondaryOffset = 14f,
                small = false,
                centeredX = true
            )
        } else {
            modelWidget = null
            typeWidget = null
        }
    }

}