package com.rafacasari.mod.cobbledex.client.gui

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.spawning.CobblemonSpawnPools
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.cobblemon.mod.common.api.text.add
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.client.CobblemonResources
import com.cobblemon.mod.common.client.gui.ExitButton
import com.cobblemon.mod.common.client.gui.TypeIcon
import com.cobblemon.mod.common.client.gui.summary.widgets.ModelWidget
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.asTranslated
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.world.ClientWorld
import net.minecraft.sound.SoundEvent
import net.minecraft.text.Style
import net.minecraft.util.Identifier
import net.minecraft.world.World
import com.rafacasari.mod.cobbledex.Cobbledex
import com.rafacasari.mod.cobbledex.client.widget.LongTextDisplay
import com.rafacasari.mod.cobbledex.client.widget.PokemonEvolutionDisplay
import com.rafacasari.mod.cobbledex.utils.BiomeUtils
import com.rafacasari.mod.cobbledex.utils.TypeChart
import com.rafacasari.mod.cobbledex.utils.cobbledexTranslation

class CobbledexGUI(private val selectedPokemon: Pokemon?) : Screen(cobbledexTranslation("texts.title.cobbledex_gui")) {

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

        fun openCobbledexScreen(pokemon: Pokemon?) {
            playSound(CobblemonSounds.PC_ON)

            Instance = CobbledexGUI(pokemon)
            MinecraftClient.getInstance().setScreen(Instance)
        }

        fun playSound(soundEvent: SoundEvent) {
            MinecraftClient.getInstance().soundManager.play(PositionedSoundInstance.master(soundEvent, 1f))
        }

        var previewPokemon: Pokemon? = PokemonSpecies.getByPokedexNumber(1)?.create(1)

    }

    private var modelWidget: ModelWidget? = null
    private var evolutionDisplay: PokemonEvolutionDisplay? = null
    private var typeWidget: TypeIcon? = null
    private var longTextDisplay: LongTextDisplay? = null

    override fun init() {

        val x = (width - BASE_WIDTH) / 2
        val y = (height - BASE_HEIGHT) / 2

        this.addDrawableChild(ExitButton(pX = x + 315, pY = y + 172) { this.close() })


        longTextDisplay = LongTextDisplay(x + 79, y + 18, 179, 158, 2)
        addDrawableChild(longTextDisplay)


        super.init()

        // Should be the last thing to do.
        if (selectedPokemon == null)
        {
            this.setPreviewPokemon(previewPokemon)
        }
        else
        {
            this.setPreviewPokemon(selectedPokemon)
        }
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
            text = "Pokedex Number".text().bold(),
            x = x + 12F,
            y = y + 134.5f,
            centered = false,
            scale = 0.65F
        )


        drawScaledText(
            context = context,
            text = "Height".text().bold(),
            x = x + 12F,
            y = y + 156.5f,
            centered = false,
            scale = 0.65F
        )

        drawScaledText(
            context = context,
            text = "Weight".text().bold(),
            x = x + 12F,
            y = y + 178.5f,
            centered = false,
            scale = 0.65F
        )

        drawScaledText(
            context = context,
            text = "Evolutions".text().bold(),
            x = x + 302,
            y = y + 29,
            centered = true,
            scale = 0.8F
        )

        val pokemon = previewPokemon
        if (pokemon != null) {
            // Level
//            drawScaledText(
//                context = context,
//                font = CobblemonResources.DEFAULT_LARGE,
//                text = "N°".text().bold(),
//                x = x + 6,
//                y = y + 1.5,
//                shadow = true
//            )

            drawScaledText(
                context = context,
                font = CobblemonResources.DEFAULT_LARGE,
                text = "Cobbledex".text().bold(),
                x = x + 167,
                y = y + 7,
                shadow = false,
                centered = true
            )




            drawScaledText(
                context = context,
                font = CobblemonResources.DEFAULT_LARGE,
                text = pokemon.species.name.text().bold(),
                x = x + 13,
                y = y + 29,
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
//                font = CobblemonResources.DEFAULT_LARGE,
                text = pokemon.species.nationalPokedexNumber.toString().text(),
                x = x + 12,
                y = y + 143.5f,
                shadow = false,
                scale =  0.65f
            )

            drawScaledText(
                context = context,
                text = ((pokemon.species.height / 10).toString() + "m").text(),
                x = x + 12,
                y = y + 165.5f,
                centered = false,
                scale = 0.65F
            )

            drawScaledText(
                context = context,
                text = ((pokemon.species.weight / 10).toString() + "kg").text(),
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
                    val primaryType = pokemon.form.primaryType
                    stringBuilder.append(primaryType.displayName.setStyle(Style.EMPTY.withBold(true).withColor(primaryType.hue)))

                    val secondType = pokemon.form.secondaryType
                    if (secondType != null) {
                        stringBuilder.append(" & ".text().setStyle(Style.EMPTY.withBold(true)))
                        stringBuilder.append(secondType.displayName.setStyle(Style.EMPTY.withBold(true).withColor(secondType.hue)))
                    }

                    context.drawTooltip(
                        MinecraftClient.getInstance().textRenderer,
                        stringBuilder,
                        mouseX,
                        mouseY)
                }


            }
        }


    }



    override fun shouldPause(): Boolean {
        return false
    }

    override fun shouldCloseOnEsc(): Boolean {
        return true
    }

    override fun close() {
        Instance = null
        playSound(CobblemonSounds.PC_OFF)
        super.close()
    }

    fun getSpawnDetails(pokemon: Pokemon) : List<SpawnDetail> {

        val spawnDetails = CobblemonSpawnPools.WORLD_SPAWN_POOL.filter {
                x -> x.id.startsWith("${pokemon.species.resourceIdentifier.path}-")
        }

        return spawnDetails
    }

    fun setPreviewPokemon(pokemon: Pokemon?)
    {
        longTextDisplay?.clear()

        if (pokemon != null)
        {
            pokemon.form.pokedex.forEach {
                pokedex -> longTextDisplay?.add(pokedex.asTranslated())
            }

            val weaknessList = ElementalTypes.all().map {
                    t -> t to TypeChart.getEffectiveness(t, pokemon.types)
            }.filter { (_, effectiveness) ->
                effectiveness > 0
            }

            val resistantList = ElementalTypes.all().map {
                    t -> t to TypeChart.getEffectiveness(t, pokemon.types)
            }.filter { (_, effectiveness) ->
                effectiveness < 0
            }

            val immuneList = ElementalTypes.all().map {
                    t -> t to TypeChart.getImmunity(t, pokemon.types)
            }.filter { (_, isImmune) ->
                !isImmune
            }


            // Break a line
            if (weaknessList.isNotEmpty() || resistantList.isNotEmpty())
                longTextDisplay?.add("".text())


            if (weaknessList.isNotEmpty()) {
                val mutableText =  cobbledexTranslation("cobbledex.texts.weakness")
                for (elementalType in weaknessList)
                {
                    mutableText.add(" ".text())
                    mutableText.add(elementalType.first.displayName.setStyle(Style.EMPTY
                        .withBold(true)
                        .withColor(elementalType.first.hue)))
//                    mutableText.add("(${elementalType.second}x)")
                }
                longTextDisplay?.add(mutableText, false)
            }

            if (resistantList.isNotEmpty()) {
                val mutableText = cobbledexTranslation("cobbledex.texts.resistant")
                for (elementalType in resistantList)
                {
                    mutableText.add(" ".text())
                    mutableText.add(elementalType.first.displayName.setStyle(Style.EMPTY
                        .withBold(true)
                        .withColor(elementalType.first.hue)))
//                    mutableText.add("(${elementalType.second}x)")
                }
                longTextDisplay?.add(mutableText, false)
            }

            if (immuneList.isNotEmpty()) {
                val mutableText = cobbledexTranslation("cobbledex.texts.immune")
                for (elementalType in immuneList)
                {
                    mutableText.add(" ".text())
                    mutableText.add(elementalType.first.displayName.setStyle(Style.EMPTY
                        .withBold(true)
                        .withColor(elementalType.first.hue)))
                }
                longTextDisplay?.add(mutableText, false)
            }

            val world: ClientWorld? = MinecraftClient.getInstance().world
            if (world != null) {

                val biomes = BiomeUtils.getAllBiomes(world as World)
                val validBiomes = biomes.filter { biome ->
                    getSpawnDetails(pokemon).any() { s ->
                        s.conditions.any { c ->
                            BiomeUtils.canSpawnAt(biome.biome, world, c)
                        }
                    }
                }

                longTextDisplay?.add(cobbledexTranslation("cobbledex.texts.biomes").bold(), true)
                validBiomes.forEach { biome ->
                    longTextDisplay?.add("biome.${biome.identifier.toTranslationKey()}".asTranslated(), false)
                }
            }
        }

        val x = (width - BASE_WIDTH) / 2
        val y = (height - BASE_HEIGHT) / 2

        // re-initialize
        if (evolutionDisplay == null) {
            evolutionDisplay = PokemonEvolutionDisplay(x + 260, y + 37)
            addDrawableChild(evolutionDisplay)
        }
        evolutionDisplay?.selectPokemon(pokemon)


        if (pokemon != null) {
            previewPokemon = pokemon


            modelWidget = ModelWidget(
                pX = x + 13,
                pY = y + 41,
                pWidth = PORTRAIT_SIZE,
                pHeight = PORTRAIT_SIZE,
                pokemon = pokemon.asRenderablePokemon(),
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
}