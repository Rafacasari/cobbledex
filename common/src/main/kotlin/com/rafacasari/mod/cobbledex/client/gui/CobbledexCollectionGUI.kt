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
import com.cobblemon.mod.common.pokemon.FormData
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.pokemon.Species
import com.cobblemon.mod.common.util.math.fromEulerXYZDegrees
import com.rafacasari.mod.cobbledex.CobbledexConstants.Client.discoveredList
import com.rafacasari.mod.cobbledex.CobbledexConstants.Client.totalPokemonCaught
import com.rafacasari.mod.cobbledex.CobbledexConstants.Client.totalPokemonCount
import com.rafacasari.mod.cobbledex.CobbledexConstants.Client.totalPokemonDiscovered
import com.rafacasari.mod.cobbledex.api.classes.DiscoveryRegister
import com.rafacasari.mod.cobbledex.client.gui.CobbledexGUI.Companion.TYPE_SPACER
import com.rafacasari.mod.cobbledex.client.gui.CobbledexGUI.Companion.TYPE_SPACER_DOUBLE
import com.rafacasari.mod.cobbledex.client.widget.ImageButton
import com.rafacasari.mod.cobbledex.client.widget.SearchWidget
import com.rafacasari.mod.cobbledex.client.widget.SilhouetteModelWidget
import com.rafacasari.mod.cobbledex.network.client.handlers.SyncServerSettingsHandler
import com.rafacasari.mod.cobbledex.utils.CobblemonUtils.drawBlackSilhouettePokemon
import com.rafacasari.mod.cobbledex.utils.CobblemonUtils.validForms
import com.rafacasari.mod.cobbledex.utils.MiscUtils.cobbledexResource
import com.rafacasari.mod.cobbledex.utils.MiscUtils.cobbledexTextTranslation
import com.rafacasari.mod.cobbledex.utils.MiscUtils.cobbledexTranslation
import com.rafacasari.mod.cobbledex.utils.MiscUtils.emptyLine
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.sound.SoundEvent
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import org.joml.Quaternionf
import org.joml.Vector3f

class CobbledexCollectionGUI : Screen(cobbledexTextTranslation("cobbledex")) {
    companion object {
        const val BASE_WIDTH: Int = 349
        const val BASE_HEIGHT: Int = 205
        const val PORTRAIT_SIZE = 58F
        const val SCALE = 0.5F

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
        private val ENTRY_HIGHLIGHTED_BACKGROUND: Identifier = cobbledexResource("textures/gui/collection/entry_background_highlighted.png")
        private val TYPE_SPACER_UNKNOWN: Identifier = cobbledexResource("textures/gui/collection/type_spacer_unknown.png")

        private val DOUBLE_LEFT_ARROW: Identifier = cobbledexResource("textures/gui/collection/double_left_arrow.png")
        private val DOUBLE_RIGHT_ARROW: Identifier = cobbledexResource("textures/gui/collection/double_right_arrow.png")
        private val LEFT_ARROW: Identifier = cobbledexResource("textures/gui/collection/left_arrow.png")
        private val RIGHT_ARROW: Identifier = cobbledexResource("textures/gui/collection/right_arrow.png")

        private val CAUGHT_ICON: Identifier = cobbledexResource("textures/gui/collection/pokeball.png")
        private val SHINY_ICON: Identifier = cobbledexResource("textures/gui/collection/shiny_icon.png")

        fun show(skipSound: Boolean = false) {
            if (!skipSound) playSound(CobblemonSounds.PC_ON)

            val instance = CobbledexCollectionGUI()
            MinecraftClient.getInstance().setScreen(instance)
        }

        fun playSound(soundEvent: SoundEvent) {
            MinecraftClient.getInstance().soundManager.play(PositionedSoundInstance.master(soundEvent, 1f))
        }

        private var currentPage = 1
        private var maxPages = 1
        private var lastSearch = ""

        internal var needReload = true
        private var implementedSpeciesInternal: List<Species>? = null

        private var lastHoveredEntry: Species? = null
        var lastHoveredForm: FormData? = null
        val implementedSpecies: List<Species>
            get() {
                if (needReload)
                {
                    currentPage = 1
                    lastHoveredEntry = null
                    implementedSpeciesInternal = PokemonSpecies.implemented.sortedBy { it.nationalPokedexNumber }

                    needReload = false
                }

                return  implementedSpeciesInternal ?: listOf()
            }

        var filteredSpecies: MutableList<Species> = mutableListOf()

        val selectedFormMap: MutableMap<Identifier, Int> = mutableMapOf()
    }


    private var modelWidget: SilhouetteModelWidget? = null
    private var typeWidget: TypeIcon? = null
    private lateinit var searchWidget: SearchWidget
    private var currentHoveredEntry: Species? = null
    //private lateinit var rewardWidget: DiscoveryRewardWidget

    override fun init() {
        val x = (width - CobbledexGUI.BASE_WIDTH) / 2
        val y = (height - CobbledexGUI.BASE_HEIGHT) / 2

        this.addDrawableChild(ExitButton(pX = x + 313, pY = y + 175) { this.close() })
        searchWidget = SearchWidget(x + 85, y + 172) {
            updateSearch()
        }

        searchWidget.text = lastSearch
        this.addDrawableChild(searchWidget)

        setInitialFocus(searchWidget)

        addDrawableChild(ImageButton(DOUBLE_LEFT_ARROW, 14, 11, x + 170, y + 175) {
            currentPage = 1
        })

        addDrawableChild(ImageButton(LEFT_ARROW, 8, 11, x + 188, y + 175) {
            if (currentPage <= 1)
                currentPage = maxPages
            else currentPage--
        })

        addDrawableChild(ImageButton(RIGHT_ARROW, 8, 11, x + 268, y + 175) {
            if (currentPage >= maxPages)
                currentPage = 1
            else currentPage++
        })

        addDrawableChild(ImageButton(DOUBLE_RIGHT_ARROW, 14, 11, x + 280, y + 175) {
            currentPage = maxPages
        })

//        rewardWidget = DiscoveryRewardWidget(x + 5, y + 170)
//        addDrawableChild(rewardWidget)

//        val totalItems = (COLUMN_SIZE * LINES_SIZE)
//        // Get total items
//        maxPages =  (implementedSpecies.size + totalItems - 1) / totalItems

        lastHoveredForm.let {
            val register = if (it != null) discoveredList[it.showdownId()]?.get(it.formOnlyShowdownId()) else null
            val isDiscovered = register != null
            val isShiny = register != null && register.isShiny

            loadSpecies(it, isDiscovered, isShiny)
        }
        updateSearch()
    }

    private fun updateSearch() {
        if (!this::searchWidget.isInitialized) return

        lastSearch = searchWidget.text

        filteredSpecies = if(lastSearch.isNotEmpty())
            implementedSpecies.filter {  it.translatedName.string.lowercase().contains(lastSearch.lowercase()) }.toMutableList()
        else
            implementedSpecies.toMutableList()

        val totalItems = (COLUMN_SIZE * LINES_SIZE)
        maxPages = (filteredSpecies.size + totalItems - 1) / totalItems
        if (maxPages < 1) maxPages = 1

        if (currentPage > maxPages)
            currentPage = 1
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
            shadow = true,
            centered = true,
            scale = 1f
        )

        drawScaledText(
            context = context,
            font = CobblemonResources.DEFAULT_LARGE,
            text = "$currentPage / $maxPages".text().bold(),
            x = x + 188f + ((268f - 188f + 8f) / 2f),
            y = y + 175.5F,
            shadow = true,
            centered = true,
            scale = 1.1f
        )

        val percentageDiscovered = "%.2f%%".format((totalPokemonDiscovered.toDouble() / totalPokemonCount) * 100)
        val percentageCaught = "%.2f%%".format((totalPokemonCaught.toDouble() / totalPokemonCount) * 100)

        // Discovered
        drawScaledText(
            context = context,
            text = cobbledexTextTranslation("discover.discovered").bold(),
            x = x + 13,
            y = y + 135f,
            centered = false,
            scale = 0.5F
        )

        drawScaledText(
            context = context,
            text = Text.literal("${totalPokemonDiscovered}/${totalPokemonCount}"),
            x = x + 13,
            y = y + 140.5f,
            centered = false,
            scale = 0.5F
        )


        drawScaledText(
            context = context,
            text = percentageDiscovered.text().bold(),
            x = x + 64.5F,
            y = y + 138f,
            centered = true,
            scale = 0.5F
        )

        // Caught
        drawScaledText(
            context = context,
            text = cobbledexTextTranslation("discover.caught").bold(),
            x = x + 13,
            y = y + 154f,
            centered = false,
            scale = 0.5F
        )

        drawScaledText(
            context = context,
            text = Text.literal("${totalPokemonCaught}/${totalPokemonCount}"),
            x = x + 13,
            y = y + 159.5f,
            centered = false,
            scale = 0.5F
        )

        drawScaledText(
            context = context,
            text = percentageCaught.text().bold(),
            x = x + 64.5F,
            y = y + 157f,
            centered = true,
            scale = 0.5F
        )


        lastHoveredForm?.let { hoveredEntry ->
            val isDiscovered =  discoveredList.contains(hoveredEntry.species.showdownId())
            drawScaledText(
                context = context,
                font = CobblemonResources.DEFAULT_LARGE,
                text = if (isDiscovered) hoveredEntry.species.translatedName.bold() else "???".text().bold(),
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

        // Render widgets
        super.render(context, mouseX, mouseY, delta)
        val tooltip: MutableList<Text> = mutableListOf()

        var currentEntry: Species? = null
        val textRenderer = MinecraftClient.getInstance().textRenderer
        var currentIndex = 1
        val linesSize = LINES_SIZE - 1
        val columnsSize = COLUMN_SIZE - 1
        for (currentY: Int in 0..linesSize) {
            for (currentX in 0..columnsSize) {
                val currentPokemonIndex = COLUMN_SIZE * LINES_SIZE * (currentPage - 1) + (currentIndex - 1)
                val entryX = x + (83.5F + (ENTRY_SIZE * currentX) + (X_PADDING * currentX))
                val entryY = y + (24.5F + (ENTRY_SIZE * currentY) + (Y_PADDING * currentY))

                val species = if(filteredSpecies.size > currentPokemonIndex) filteredSpecies[currentPokemonIndex] else null

                species?.let {
                    // TODO: If selected form is null, use the first discovered/caught form, if also is empty, use standardForm
                    val validForms = species.validForms
                    val selectedForm = selectedFormMap[it.resourceIdentifier]?.let { formId -> validForms[formId] } ?: it.standardForm

                    val isMouseOver =
                        mouseX.toDouble() in entryX..(entryX + ENTRY_SIZE) && mouseY.toDouble() in entryY..(entryY + ENTRY_SIZE)
                    val register = discoveredList[species.showdownId()]?.get(selectedForm.formOnlyShowdownId())
                    val isDiscovered = register != null
                    val isCaught = register != null && register.status == DiscoveryRegister.RegisterType.CAUGHT
                    val isShiny = register != null && register.isShiny

                    if (isMouseOver) {
                        currentEntry = species

                        if (lastHoveredForm != selectedForm) {
                            lastHoveredForm = selectedForm

                            loadSpecies(selectedForm, isDiscovered, isShiny)
                        }
                    }

                    blitk(
                        matrixStack = matrices,
                        texture = if (isMouseOver) ENTRY_HIGHLIGHTED_BACKGROUND else ENTRY_BACKGROUND,
                        x = entryX, y = entryY,
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

                    val aspects = selectedForm.aspects.toMutableSet()
                    if (isShiny) aspects.add("shiny")

                    if (isDiscovered || SyncServerSettingsHandler.config.Collection_DisableBlackSilhouette) {
                        drawProfilePokemon(
                            species = species.resourceIdentifier,
                            aspects = aspects,
                            matrixStack = matrices,
                            rotation = rotation,
                            state = null,
                            partialTicks = delta,
                            scale = (ENTRY_SIZE / 2).toFloat()
                        )
                    } else {
                        drawBlackSilhouettePokemon(
                            species = species.resourceIdentifier,
                            aspects = aspects,
                            matrixStack = matrices,
                            rotation = rotation,
                            scale = (ENTRY_SIZE / 2).toFloat()
                        )
                    }
                    matrices.pop()

                    context.disableScissor()

                    if (isCaught) {
                        blitk(
                            matrixStack = matrices,
                            texture = CAUGHT_ICON,
                            x = (entryX + ENTRY_SIZE - (11 * SCALE) - 1f) / SCALE,
                            y = (entryY + 0.5F) / SCALE,
                            width = 12,
                            height = 12,
                            scale = SCALE,
                            alpha = 0.75f
                        )

                        if (isShiny)
                            blitk(
                                matrixStack = matrices,
                                texture = SHINY_ICON,
                                x = (entryX + ENTRY_SIZE - (14 * SCALE) - 0.5F) / SCALE,
                                y = (entryY + ENTRY_SIZE - (14 * SCALE) - 0.5F) / SCALE,
                                width = 14,
                                height = 14,
                                scale = SCALE, alpha = 0.75f
                            )
                    }

                    drawScaledText(
                        context = context,
                        text = "${species.nationalPokedexNumber}".text(),
                        x = entryX + 1.8f,
                        y = entryY + 1.8f,
                        shadow = false,
                        scale = SCALE, opacity = 0.3
                    )

                    if (isMouseOver) {
                        //val formName = if (entryForm != null) selectedForm.name else "???"
                        tooltip.add(cobbledexTextTranslation("discover.selected_form", selectedForm.name.text().bold()).formatted(Formatting.GRAY))

                        val currentStatusTranslation =
                            if (register == null)
                                Text.literal("?????").formatted(Formatting.GRAY)
                            else if (register.status == DiscoveryRegister.RegisterType.CAUGHT)
                                cobbledexTextTranslation("discovery_status.caught").formatted(Formatting.GREEN)
                            else
                                cobbledexTextTranslation("discovery_status.discovered").formatted(Formatting.GRAY)

                        tooltip.add(cobbledexTextTranslation("discovery_status", currentStatusTranslation))

                        if (register?.isShiny == true)
                            tooltip.add(cobbledexTextTranslation("shiny").formatted(Formatting.YELLOW, Formatting.BOLD))

                        if (register != null && hasShiftDown()) {
                            register.getDiscoveredTimestamp()?.let { timestamp ->
                                val translation = cobbledexTextTranslation("discovered_on", timestamp)
                                tooltip.add(translation)
                            }

                            register.getCaughtTimestamp()?.let { timestamp ->
                                val translation = cobbledexTextTranslation("caught_on", timestamp)
                                tooltip.add(translation)
                            }
                        } else if (register != null) {
                            tooltip.emptyLine()
                            tooltip.add(cobbledexTextTranslation("tooltip.hold_shift_timestamp").formatted(Formatting.GREEN))
                        }
                    }

                }

                currentIndex++
            }
        }

        currentHoveredEntry = currentEntry

        if (tooltip.isNotEmpty())
            context.drawTooltip(textRenderer, tooltip, mouseX, mouseY)
    }

    override fun shouldPause() = false

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        currentHoveredEntry?.let {
            val validForms = it.validForms
            if (validForms.isNotEmpty()) {
                var current = selectedFormMap[it.resourceIdentifier] ?: 0
                if (amount > 0) {
                    current++
                    if (current >= validForms.size)
                        selectedFormMap.remove(it.resourceIdentifier)
                    else selectedFormMap[it.resourceIdentifier] = current
                } else {
                    current--
                    if (current < 0)
                        selectedFormMap[it.resourceIdentifier] = validForms.size - 1
                    else selectedFormMap[it.resourceIdentifier] = current
                }
            }
            return true
        }

        return super.mouseScrolled(mouseX, mouseY, amount)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        currentHoveredEntry?.let { species ->
            val validForms = species.validForms
            val selectedForm = selectedFormMap[species.resourceIdentifier]?.let { formId -> validForms[formId] } ?: species.standardForm
            val config = SyncServerSettingsHandler.config
            val registerType = discoveredList[species.showdownId()]?.get(selectedForm.formOnlyShowdownId())?.status
            val hasCaught = registerType == DiscoveryRegister.RegisterType.CAUGHT
            val hasSeen = hasCaught || registerType == DiscoveryRegister.RegisterType.SEEN

            if ((!config.Collection_NeedCatch || hasCaught) && (!config.Collection_NeedSeen || hasSeen)) {
                playSound(CobblemonSounds.PC_CLICK)
                CobbledexGUI.openCobbledexScreen(selectedForm, selectedForm.aspects.toSet(), skipSound = true)
            }
            return true
        }

        return super.mouseClicked(mouseX, mouseY, button)
    }

    private fun loadSpecies(formData: FormData?, isDiscovered: Boolean = false, isShiny: Boolean = false)
    {
        val x = (width - CobbledexGUI.BASE_WIDTH) / 2
        val y = (height - CobbledexGUI.BASE_HEIGHT) / 2

        if(formData != null) {
            val aspects = formData.aspects.toMutableSet()
            if (isShiny) aspects.add("shiny")

            modelWidget = SilhouetteModelWidget(
                pX = x + 13,
                pY = y + 41,
                pWidth = CobbledexGUI.PORTRAIT_SIZE,
                pHeight = CobbledexGUI.PORTRAIT_SIZE,
                pokemon = RenderablePokemon(formData.species, aspects),
                baseScale = 1.8F,
                rotationY = 345F,
                offsetY = -10.0,
                isDiscovered = isDiscovered
            )

            typeWidget = TypeIcon(
                x = x + 39 + 3,
                y = y + 97 + 14,
                type = formData.primaryType,
                secondaryType = formData.secondaryType,
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