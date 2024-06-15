package com.rafacasari.mod.cobbledex.client.gui

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.conditional.RegistryLikeTagCondition
import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.text.*
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.client.CobblemonResources
import com.cobblemon.mod.common.client.gui.ExitButton
import com.cobblemon.mod.common.client.gui.TypeIcon
import com.cobblemon.mod.common.client.gui.summary.widgets.ModelWidget
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.pokemon.Species
import com.cobblemon.mod.common.util.asTranslated
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.world.ClientWorld
import net.minecraft.sound.SoundEvent
import net.minecraft.text.Style
import net.minecraft.util.Identifier
import com.rafacasari.mod.cobbledex.Cobbledex
import com.rafacasari.mod.cobbledex.client.widget.LongTextDisplay
import com.rafacasari.mod.cobbledex.client.widget.PokemonEvolutionDisplay
import com.rafacasari.mod.cobbledex.network.server.packets.RequestCobbledexPacket
import com.rafacasari.mod.cobbledex.network.template.SerializablePokemonSpawnDetail
import com.rafacasari.mod.cobbledex.utils.TypeChartUtils
import com.rafacasari.mod.cobbledex.utils.*
import net.minecraft.text.HoverEvent
import net.minecraft.world.biome.Biome

class CobbledexGUI(private val selectedPokemon: Species?) : Screen(cobbledexTranslation("cobbledex.texts.cobbledex")) {

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

        fun openCobbledexScreen(pokemon: Species? = null) {
            playSound(CobblemonSounds.PC_ON)

            Instance = CobbledexGUI(pokemon)
            //MinecraftClient.getInstance().setScreenAndRender(Instance)
            MinecraftClient.getInstance().setScreen(Instance)
        }


        fun playSound(soundEvent: SoundEvent) {
            MinecraftClient.getInstance().soundManager.play(PositionedSoundInstance.master(soundEvent, 1f))
        }

        var previewPokemon: Species? = null
    }

    private var modelWidget: ModelWidget? = null
    private var evolutionDisplay: PokemonEvolutionDisplay? = null
    private var typeWidget: TypeIcon? = null
    private var longTextDisplay: LongTextDisplay? = null

    override fun init() {

        val x = (width - BASE_WIDTH) / 2
        val y = (height - BASE_HEIGHT) / 2

        this.addDrawableChild(ExitButton(pX = x + 315, pY = y + 172) { this.close() })

        evolutionDisplay = PokemonEvolutionDisplay(x + 260, y + 37)
        addDrawableChild(evolutionDisplay)


        longTextDisplay = LongTextDisplay(x + 79, y + 18, 179, 158, 2)
        addDrawableChild(longTextDisplay)

        super.init()

        // Should be the last thing to do.
        if (selectedPokemon == null)
        {
            if (previewPokemon == null)
                previewPokemon = PokemonSpecies.getByPokedexNumber(1)

            this.setPreviewPokemon(previewPokemon)
        }
        else
            this.setPreviewPokemon(selectedPokemon)
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
                text = pokemon.nationalPokedexNumber.toString().text(),
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
                    val primaryType = pokemon.standardForm.primaryType
                    stringBuilder.append(primaryType.displayName.setStyle(Style.EMPTY.withBold(true).withColor(primaryType.hue)))

                    val secondType = pokemon.standardForm.secondaryType
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

    fun setPreviewPokemon(pokemon: Species?)
    {
        longTextDisplay?.clear()

        if (pokemon != null)
        {
            pokemon.standardForm.pokedex.forEach {
                pokedex -> longTextDisplay?.add(pokedex.asTranslated())
            }

            val elementalTypes = ElementalTypes.all().map {
                t -> t to TypeChartUtils.getModifier(t, pokemon.primaryType, pokemon.secondaryType)
            }.groupBy {
                t -> t.second
            }.toList().sortedByDescending { it.first }


            elementalTypes.forEach { elementalKey ->
                if (elementalKey.first < 1 || elementalKey.first > 1) {
                    longTextDisplay?.add("${elementalKey.first}x damage from".text())
                    val mutableText = "".text()
                    var isFirst = true
                    elementalKey.second.forEach {
                        if (!isFirst)
                            mutableText.add(" ".text())

                        isFirst = false

                        mutableText.add(
                            it.first.displayName.setStyle(
                                Style.EMPTY
                                    .withBold(true)
                                    .withColor(it.first.hue)
                            )
                        )
                    }

                    longTextDisplay?.add(mutableText, false)
                }
            }
        }

        val x = (width - BASE_WIDTH) / 2
        val y = (height - BASE_HEIGHT) / 2

        evolutionDisplay?.clearEvolutions()
        if (lastLoadedSpecies != null && lastLoadedSpecies == pokemon && lastLoadedSpawnDetails != null)
        {
            setSpawnDetails(lastLoadedSpecies, lastLoadedSpawnDetails!!, true)
        }
        else if (pokemon != null) {
            RequestCobbledexPacket(pokemon.resourceIdentifier).sendToServer()
        }

        if (pokemon != null) {
            previewPokemon = pokemon

            modelWidget = ModelWidget(
                pX = x + 13,
                pY = y + 41,
                pWidth = PORTRAIT_SIZE,
                pHeight = PORTRAIT_SIZE,
                pokemon = RenderablePokemon(pokemon, pokemon.standardForm.aspects.toSet()),
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

    fun setEvolutions(evolutions: List<Species>) {
        evolutionDisplay?.selectEvolutions(evolutions)
    }

    private var lastLoadedSpecies: Species? = null
    private var lastLoadedSpawnDetails: List<SerializablePokemonSpawnDetail>? = null

    fun setSpawnDetails(species: Species?, spawnDetails: List<SerializablePokemonSpawnDetail>, fromCache: Boolean = false) {
        if (!fromCache) {
            lastLoadedSpawnDetails = spawnDetails
            lastLoadedSpecies = species
        }

        if (spawnDetails.isEmpty()) return

        longTextDisplay?.add(cobbledexTranslation("cobbledex.texts.biomes").bold())

        val world: ClientWorld? = MinecraftClient.getInstance().world
        if (world != null) {
            val biomeRegistry = BiomeUtils.getBiomesRegistry(world)

            spawnDetails.forEach { spawn ->

                val biomes = spawn.conditions?.filter {
                    it.biomes != null
                }?.mapNotNull {
                    it.biomes
                }?.flatten()?.map {
                    it as RegistryLikeTagCondition<Biome>
                }

                spawn.conditions?.forEach { cond ->
                    cond.biomes?.forEach {
                        biomeCondition ->
                        if (biomeCondition is RegistryLikeTagCondition<Biome>)
                        {
                            val tooltipText =  biomeCondition.tag.id.toTranslationKey().asTranslated()
                            val condition = biomeCondition.tag.id.toTranslationKey()
                            val conditionMutableText = condition.asTranslated()

                            tooltipText.add("\nWeight: ${spawn.weight}".text().setStyle(Style.EMPTY.withBold(false)))
                            if (spawn.levelRange != null) {
                                val levelRange = spawn.levelRange!!
                                tooltipText.add(
                                    "\nLevel Range: ${levelRange.first}-${levelRange.last}".text().setStyle(Style.EMPTY.withBold(false))
                                )
                            }

                            if (cond.canSeeSky != null) {
                                val skyInfo = if (cond.canSeeSky!!) "Should see sky" else "Should NOT see sky"
                                tooltipText.add(
                                    "\n$skyInfo".text().setStyle(Style.EMPTY.withBold(false))
                                )
                            }

                            if (cond.isRaining != null) {
                                val rainInfo = if (cond.isRaining!!) "Weather: Raining" else "Weather: Clear"
                                tooltipText.add(
                                    "\n$rainInfo".text().setStyle(Style.EMPTY.withBold(false))
                                )
                            }

                            if (cond.isThundering != null) {
                                val thunderInfo = if (cond.isThundering!!) "Should be thundering" else "Should not be thundering"
                                tooltipText.add(
                                    "\n$thunderInfo".text().setStyle(Style.EMPTY.withBold(false))
                                )
                            }

                            if (cond.minLight != null)
                                tooltipText.add("\nMin Light: ${cond.minLight}".text().setStyle(Style.EMPTY.withBold(false)))

                            if (cond.minSkyLight != null)
                                tooltipText.add("\nMin Sky Light: ${cond.minSkyLight}".text().setStyle(Style.EMPTY.withBold(false)))

                            if (cond.maxLight != null)
                                tooltipText.add("\nMax Light: ${cond.maxLight}".text().setStyle(Style.EMPTY.withBold(false)))

                            if (cond.maxSkyLight != null)
                                tooltipText.add("\nMax Sky Light: ${cond.maxSkyLight}".text().setStyle(Style.EMPTY.withBold(false)))

                            // TODO: Implement the following conditions
                            //  cond.timeRange
                            //  cond.moonPhase
                            //  cond.minY cond.maxY
                            //  There is also X and Z but I'm not sure if I need to implement it? I don't know what these are supposed to do

                            val structureConditions = spawn.conditions?.mapNotNull { structureCondition ->
                                structureCondition.structures
                            }?.flatten()

                            if (!structureConditions.isNullOrEmpty()) {
                                tooltipText.add("\n\nNeed structure:".text().bold().darkGreen())
                                structureConditions.forEach { structure ->
                                    val structureName = structure.toTranslationKey()

                                    tooltipText.add("\n".text())
                                    tooltipText.add("structure.$structureName".asTranslated().setStyle(Style.EMPTY.withBold(false)))
                                }
                            }

                            val antiConditionBiomes = spawn.antiConditions?.mapNotNull { x -> x.biomes }?.flatten()?.filterIsInstance<RegistryLikeTagCondition<Biome>>() ?: listOf()
                            // Too much stuff to write, we can skip it!
                            if (!condition.endsWith("is_overworld") && !condition.endsWith("is_nether")) {

                                //val antiConditions = spawn.antiConditions?.mapNotNull { y -> y.biomes }?.flatten() ?: listOf()
                                val availableBiomes = BiomeUtils.getAllBiomes(world).filter { b ->
                                    biomeCondition.fits(b.biome, biomeRegistry)&& !antiConditionBiomes.any { anti -> anti.fits(b.biome, biomeRegistry) }
                                }.map { "biome.${it.identifier.toTranslationKey()}".asTranslated() }
//
                                if (availableBiomes.isNotEmpty()) {
                                    tooltipText.add("\n\nBiomes:".text().bold().blue())
                                    availableBiomes.forEach { biome ->
                                        tooltipText.add("\n".text())
                                        tooltipText.add(biome.setStyle(Style.EMPTY.withBold(false)))
                                    }
                                }

                            } else {

                                if (antiConditionBiomes.isNotEmpty()) {
                                    tooltipText.add("\n\nBlacklisted Biomes:".text().bold().darkRed())
                                    antiConditionBiomes.forEach { b ->

                                        tooltipText.add("\n".text())
                                        tooltipText.add(
                                            b.tag.id.toTranslationKey().asTranslated().darkRed()
                                                .setStyle(Style.EMPTY.withBold(false)))

                                    }
                                }
                            }

                            val hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, tooltipText)

                            longTextDisplay?.add(conditionMutableText.setStyle(Style.EMPTY.withHoverEvent(hoverEvent)), false)
                        }
                    }
                }

                biomes?.forEach { biomeCondition ->

                }


            }

        }
    }
}