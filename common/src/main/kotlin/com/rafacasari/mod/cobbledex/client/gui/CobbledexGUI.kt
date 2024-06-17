package com.rafacasari.mod.cobbledex.client.gui

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.text.*
import com.cobblemon.mod.common.client.CobblemonResources
import com.cobblemon.mod.common.client.gui.ExitButton
import com.cobblemon.mod.common.client.gui.TypeIcon
import com.cobblemon.mod.common.client.gui.summary.widgets.ModelWidget
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.pokemon.FormData
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.pokemon.Species
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.sound.SoundEvent
import net.minecraft.text.Style
import net.minecraft.util.Identifier
import com.rafacasari.mod.cobbledex.Cobbledex
import com.rafacasari.mod.cobbledex.client.gui.menus.BattleMenu
import com.rafacasari.mod.cobbledex.client.gui.menus.InfoMenu
import com.rafacasari.mod.cobbledex.client.widget.CobbledexTab
import com.rafacasari.mod.cobbledex.client.widget.LongTextDisplay
import com.rafacasari.mod.cobbledex.client.widget.PokemonEvolutionDisplay
import com.rafacasari.mod.cobbledex.network.server.packets.RequestCobbledexPacket
import com.rafacasari.mod.cobbledex.network.template.SerializableItemDrop
import com.rafacasari.mod.cobbledex.network.template.SerializablePokemonSpawnDetail
import com.rafacasari.mod.cobbledex.utils.*

enum class CobbledexMenu {
    Info, Battle, Evolutions
}

class CobbledexGUI(var selectedPokemon: FormData?, var selectedAspects: Set<String>? = null) : Screen(cobbledexTranslation("cobbledex.texts.cobbledex")) {

    companion object
    {
        const val BASE_WIDTH: Int = 349
        const val BASE_HEIGHT: Int = 205
        const val SCALE = 0.5F

        const val PORTRAIT_SIZE = 58

        private val MAIN_BACKGROUND: Identifier = Identifier(Cobbledex.MOD_ID, "textures/gui/new_cobbledex_background.png")
        private val PORTRAIT_BACKGROUND: Identifier = Identifier(Cobbledex.MOD_ID, "textures/gui/portrait_background.png")

        private val TYPE_SPACER: Identifier = Identifier(Cobbledex.MOD_ID, "textures/gui/type_spacer.png")
        private val TYPE_SPACER_DOUBLE: Identifier = Identifier(Cobbledex.MOD_ID, "textures/gui/type_spacer_double.png")

        var Instance : CobbledexGUI? = null


        fun openCobbledexScreen(pokemon: FormData? = null, aspects: Set<String>? = null) {
            playSound(CobblemonSounds.PC_ON)

            Instance = CobbledexGUI(pokemon, aspects)
            MinecraftClient.getInstance().setScreen(Instance)
        }


        fun playSound(soundEvent: SoundEvent) {
            MinecraftClient.getInstance().soundManager.play(PositionedSoundInstance.master(soundEvent, 1f))
        }

        var previewPokemon: FormData? = null
        var selectedTab : CobbledexMenu = CobbledexMenu.Info

    }


    private var modelWidget: ModelWidget? = null
    private var evolutionDisplay: PokemonEvolutionDisplay? = null
    private var typeWidget: TypeIcon? = null
    private var longTextDisplay: LongTextDisplay? = null

    private lateinit var infoTabButton: CobbledexTab
    private lateinit var battleTabButton: CobbledexTab
    private lateinit var evolveTabButton: CobbledexTab

    override fun init() {

        val x = (width - BASE_WIDTH) / 2
        val y = (height - BASE_HEIGHT) / 2

        this.addDrawableChild(ExitButton(pX = x + 315, pY = y + 172) { this.close() })

        evolutionDisplay = PokemonEvolutionDisplay(x + 260, y + 37)
        addDrawableChild(evolutionDisplay)


        longTextDisplay = LongTextDisplay(x + 79, y + 18, 179, 158, 2)
        addDrawableChild(longTextDisplay)

        super.init()

        // setPreview to the target or cached Pokémon
        // Should be after essential widgets initialization (evolutionDisplay and longTextDisplay)
        if (selectedPokemon == null)
        {
            // If there is no selected Pokémon and no preview, set to default
            if (previewPokemon == null)
                previewPokemon = PokemonSpecies.getByPokedexNumber(1)?.standardForm

            this.setPreviewPokemon(previewPokemon)
        }
        else {
            // Return to info menu
//            if (selectedPokemon != previewPokemon)
//                selectedTab = CobbledexMenu.Info

            this.setPreviewPokemon(selectedPokemon, selectedAspects)
        }

        // Initialize Tabs
        infoTabButton = CobbledexTab(x, y,x + 114, y + 178, "Info".text()) {
            selectedTab = CobbledexMenu.Info
            defaultTabClickEvent()
        }

        battleTabButton = CobbledexTab(x, y,x + 151, y + 178, "Battle".text()) {
            selectedTab = CobbledexMenu.Battle
            defaultTabClickEvent()
        }

        evolveTabButton = CobbledexTab(x, y,x + 188, y + 178, "Evolve".text()) {
            selectedTab = CobbledexMenu.Evolutions
            defaultTabClickEvent()
        }

        // Add our tabs as a drawable child
        addDrawableChild(infoTabButton)
        addDrawableChild(battleTabButton)
        addDrawableChild(evolveTabButton)

        // Update menu will highlight the current tab and draw text into longTextDisplay
        // (using cached data or client-side data)
        updateMenu()
    }

    private fun defaultTabClickEvent() {
        longTextDisplay?.resetScrollPosition()
        playSound(CobblemonSounds.GUI_CLICK)
        updateMenu()
    }

    private fun updateMenu() {
        longTextDisplay?.clear()

        infoTabButton.setActive(selectedTab == CobbledexMenu.Info)
        battleTabButton.setActive(selectedTab == CobbledexMenu.Battle)
        evolveTabButton.setActive(selectedTab == CobbledexMenu.Evolutions)

        when(selectedTab) {
            CobbledexMenu.Info -> {
                // Use cache to load spawnDetails
                // We don't need to worry about being null, server should send information and packet handler will update automatically
                updateInfoPage(lastLoadedSpecies, lastLoadedSpawnDetails, lastLoadedPokemonDrops, true)
            }

            CobbledexMenu.Battle -> {
                BattleMenu.drawText(longTextDisplay, previewPokemon)
            }

            CobbledexMenu.Evolutions -> {
                longTextDisplay?.addText("Coming soon...".text())
            }
        }

        setEvolutions(null, true)
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {

        val matrices = context.matrices
        renderBackground(context)

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

        // Render Background
        blitk(
            matrixStack = matrices,
            texture = MAIN_BACKGROUND,
            x = x,
            y = y,
            width = BASE_WIDTH,
            height = BASE_HEIGHT
        )


        drawScaledText(
            context = context,
            text = cobbledexTranslation("cobbledex.texts.pokedex_number").bold(),
            x = x + 12F,
            y = y + 134.5f,
            centered = false,
            scale = 0.65F
        )


        drawScaledText(
            context = context,
            text = cobbledexTranslation("cobbledex.texts.height").bold(),
            x = x + 12F,
            y = y + 156.5f,
            centered = false,
            scale = 0.65F
        )

        drawScaledText(
            context = context,
            text = cobbledexTranslation("cobbledex.texts.weight").bold(),
            x = x + 12F,
            y = y + 178.5f,
            centered = false,
            scale = 0.65F
        )

        drawScaledText(
            context = context,
            text = cobbledexTranslation("cobbledex.texts.evolutions").bold(),
            x = x + 302,
            y = y + 29,
            centered = true,
            scale = 0.8F
        )

        drawScaledText(
            context = context,
            font = CobblemonResources.DEFAULT_LARGE,
            text = cobbledexTranslation("cobbledex.texts.cobbledex").bold(),
            x = x + 169.5F,
            y = y + 7.35F,
            shadow = false,
            centered = true,
            scale = 1.06f
        )


        val pokemon = previewPokemon
        if (pokemon != null) {

            drawScaledText(
                context = context,
                font = CobblemonResources.DEFAULT_LARGE,
                text = pokemon.name.text().bold(),
                x = x + 13,
                y = y + 28.3F,
                shadow = false
            )

            blitk(
                matrixStack = matrices,
                texture = if (pokemon.secondaryType == null) TYPE_SPACER else TYPE_SPACER_DOUBLE,
                x = (x + 5.5 + 3) / SCALE,
                y = (y + 100 + 14) / SCALE,
                width = 134,
                height = 24,
                scale = SCALE
            )


            typeWidget?.render(context)

            drawScaledText(
                context = context,
                text = pokemon.species.nationalPokedexNumber.toString().text(),
                x = x + 12,
                y = y + 143.5f,
                shadow = false,
                scale =  0.65f
            )

            drawScaledText(
                context = context,
                text = ((pokemon.height / 10).toString() + "m").text(),
                x = x + 12,
                y = y + 165.5f,
                centered = false,
                scale = 0.65F
            )

            drawScaledText(
                context = context,
                text = ((pokemon.weight / 10).toString() + "kg").text(),
                x = x + 12,
                y = y + 187.5f,
                centered = false,
                scale = 0.65F
            )

        }

        super.render(context, mouseX, mouseY, delta)

        if (pokemon != null && typeWidget != null) {
            val typeX : Float? = typeWidget?.x?.toFloat()
            val typeY : Float? = typeWidget?.y?.toFloat()
            val space : Float = if(pokemon.secondaryType != null) 16f else 8f
            if (typeX != null && typeY != null) {
                val itemHovered =
                    mouseX.toFloat() in typeX - space..(typeX + space) && mouseY.toFloat() in typeY..(typeY + 16)
                if (itemHovered) {


                    val stringBuilder = "".text()
                    val primaryType = pokemon.primaryType
                    stringBuilder.append(primaryType.displayName.setStyle(Style.EMPTY.withBold(true).withColor(primaryType.hue)))

                    val secondType = pokemon.secondaryType
                    if (secondType != null) {
                        stringBuilder.append(" & ".text().setStyle(Style.EMPTY.withBold(true)))
                        stringBuilder.append(secondType.displayName.setStyle(Style.EMPTY.withBold(true).withColor(secondType.hue)))
                    }

                    context.drawTooltip(MinecraftClient.getInstance().textRenderer, stringBuilder, mouseX, mouseY)
                }
            }
        }
    }

    override fun shouldPause(): Boolean = false
    override fun shouldCloseOnEsc(): Boolean = true

    override fun close() {
        Instance = null
        playSound(CobblemonSounds.PC_OFF)
        super.close()
    }

    fun setPreviewPokemon(pokemon: FormData?, pokemonAspects: Set<String>? = null)
    {
        val x = (width - BASE_WIDTH) / 2
        val y = (height - BASE_HEIGHT) / 2

        evolutionDisplay?.clearEvolutions()

        // Request Pokémon Info to server and load into cache in Packet Handler (See ReceiveCobbledexPacketHandler)
        if (pokemon != null && (lastLoadedSpecies == null || lastLoadedSpecies != pokemon.species))
            RequestCobbledexPacket(pokemon.species.resourceIdentifier).sendToServer()

        if (pokemon != null) {
            previewPokemon = pokemon

            modelWidget = ModelWidget(
                pX = x + 13,
                pY = y + 41,
                pWidth = PORTRAIT_SIZE,
                pHeight = PORTRAIT_SIZE,
                pokemon = RenderablePokemon(pokemon.species, pokemonAspects ?: setOf()),
                baseScale = 1.8F,
                rotationY = 345F,
                offsetY = -10.0
            )

            val typeOffset = 14f
            typeWidget = TypeIcon(
                x = x + 39 + 3,
                y = y + 97 + 14,
                type = pokemon.primaryType,
                secondaryType = pokemon.secondaryType,
                doubleCenteredOffset = typeOffset / 2,
                secondaryOffset = typeOffset,
                small = false,
                centeredX = true
            )

        } else {
            previewPokemon = null
            modelWidget = null
            typeWidget = null
        }
    }

    private var lastLoadedEvolutions: List<Pair<Species, Set<String>>>? = null
    fun setEvolutions(evolutions: List<Pair<Species, Set<String>>>?, fromCache: Boolean = false) {
        if (!fromCache)
            lastLoadedEvolutions = evolutions

        if (fromCache)
        {
            evolutionDisplay?.selectEvolutions(evolutions ?: lastLoadedEvolutions)
            return
        }

        evolutionDisplay?.selectEvolutions(evolutions)
    }

    private var lastLoadedSpecies: Species? = null
    private var lastLoadedSpawnDetails: List<SerializablePokemonSpawnDetail>? = null
    private var lastLoadedPokemonDrops: List<SerializableItemDrop>? = null

    fun updateInfoPage(species: Species?, spawnDetails: List<SerializablePokemonSpawnDetail>?, itemDrops: List<SerializableItemDrop>?, fromCache: Boolean = false) {
        if (!fromCache) {
            // If our call isn't from cache, then we can update the current cache
            lastLoadedSpawnDetails = spawnDetails
            lastLoadedSpecies = species
            lastLoadedPokemonDrops = itemDrops

            // Recall updateMenu to use cached values if needed
            updateMenu()
        } else {
            // We should make sure that the current cache is actually the selected Pokémon
            if (previewPokemon != null) {
                val details = if (previewPokemon!!.species == species) spawnDetails else null
                val drops = if (previewPokemon!!.species == species) itemDrops else null

                InfoMenu.drawText(
                    longTextDisplay,
                    previewPokemon,
                    details, drops
                )
            }
        }
    }
}