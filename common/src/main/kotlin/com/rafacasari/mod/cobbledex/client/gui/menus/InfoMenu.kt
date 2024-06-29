package com.rafacasari.mod.cobbledex.client.gui.menus

import com.cobblemon.mod.common.api.conditional.RegistryLikeTagCondition
import com.cobblemon.mod.common.api.text.*
import com.cobblemon.mod.common.pokemon.FormData
import com.cobblemon.mod.common.util.asTranslated
import com.rafacasari.mod.cobbledex.CobbledexConstants.Client.discoveredList
import com.rafacasari.mod.cobbledex.api.classes.DiscoveryRegister
import com.rafacasari.mod.cobbledex.client.widget.LongTextDisplay
import com.rafacasari.mod.cobbledex.network.client.handlers.SyncServerSettingsHandler
import com.rafacasari.mod.cobbledex.network.template.SerializableItemDrop
import com.rafacasari.mod.cobbledex.network.template.SerializablePokemonSpawnDetail
import com.rafacasari.mod.cobbledex.utils.BiomeUtils
import com.rafacasari.mod.cobbledex.utils.MiscUtils.addEmptyLine
import com.rafacasari.mod.cobbledex.utils.MiscUtils.cobbledexTextTranslation
import com.rafacasari.mod.cobbledex.utils.MiscUtils.format
import com.rafacasari.mod.cobbledex.utils.MiscUtils.toMutableText
import net.minecraft.client.MinecraftClient
import net.minecraft.client.world.ClientWorld
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.text.*
import net.minecraft.text.Text
import net.minecraft.world.biome.Biome

object InfoMenu {

    fun drawText(
        longTextDisplay: LongTextDisplay?,
        pokemon: FormData?,
        spawnDetails: List<SerializablePokemonSpawnDetail>?,
        pokemonDrops: List<SerializableItemDrop>?
    ) {
        if (longTextDisplay == null || pokemon == null) return

        pokemon.pokedex.forEach { pokedex ->
            longTextDisplay.addText(pokedex.asTranslated())
        }

        val config = SyncServerSettingsHandler.config
        val registerType = discoveredList[pokemon.species.showdownId()]?.get(pokemon.formOnlyShowdownId())?.status
        val hasCaught = registerType == DiscoveryRegister.RegisterType.CAUGHT
        val hasSeen = hasCaught || registerType == DiscoveryRegister.RegisterType.SEEN

        longTextDisplay.addText(cobbledexTextTranslation("drops").bold())
        if (!config.ItemDrops_IsEnabled)
            longTextDisplay.addText(cobbledexTextTranslation("blocked_by_server"), false)
        else if (config.ItemDrops_NeedSeen && !hasSeen)
            longTextDisplay.addText(cobbledexTextTranslation("need_seen", cobbledexTextTranslation("need.view_drops")), false)
        else if (config.ItemDrops_NeedCatch && !hasCaught)
            longTextDisplay.addText(cobbledexTextTranslation("need_catch", cobbledexTextTranslation("need.view_drops")), false)

        else if (!pokemonDrops.isNullOrEmpty()) {
            pokemonDrops.forEach { itemDrop ->
                val itemStack = ItemStack(Registries.ITEM.get(itemDrop.item))

                // Hacky way to show quantity/rolls in a better way
                val quantityRange = itemDrop.quantityRange
                val quantity: String =
                    if (quantityRange.first == quantityRange.last) quantityRange.first.toString() else "${quantityRange.first}-${quantityRange.last}"

                val translation = Text.translatable(
                    "cobbledex.texts.drops.item",
                    itemStack.name,
                    itemDrop.percentage.format(),
                    quantity
                )
                longTextDisplay.addItemEntry(itemStack, translation, false)
            }

        } else longTextDisplay.addText(cobbledexTextTranslation("no_drops_found"), false)


        if (!config.HowToFind_IsEnabled) {
            longTextDisplay.addText(cobbledexTextTranslation("biomes").bold())
            longTextDisplay.addText(cobbledexTextTranslation("blocked_by_server"), false)
        }
        else if (config.HowToFind_NeedSeen && !hasSeen)
            longTextDisplay.addText(cobbledexTextTranslation("need_seen", cobbledexTextTranslation("need.view_spawns")), true)
        else if (config.HowToFind_NeedCatch && !hasCaught)
            longTextDisplay.addText(cobbledexTextTranslation("need_catch", cobbledexTextTranslation("need.view_spawns")), true)
        else {
            val world: ClientWorld? = MinecraftClient.getInstance().world
            if (world != null && !spawnDetails.isNullOrEmpty() && (!config.HowToFind_NeedSeen || hasSeen) && (!config.HowToFind_NeedCatch || hasCaught)) {
                longTextDisplay.addText(cobbledexTextTranslation("biomes").bold())

                val biomeRegistry = BiomeUtils.getBiomesRegistry(world)

                spawnDetails.forEach { spawn ->
                    spawn.conditions?.forEach { cond ->
                        cond.biomes?.forEach { biomeCondition ->
                            if (biomeCondition is RegistryLikeTagCondition<Biome>) {
                                val tooltipText = mutableListOf<MutableText>()
                                tooltipText.add(biomeCondition.tag.id.toTranslationKey().asTranslated().bold())
                                val condition = biomeCondition.tag.id.toTranslationKey()
                                tooltipText.add(
                                    "Weight: ${spawn.weight}".text()
                                )
                                if (spawn.levelRange != null) {
                                    val levelRange = spawn.levelRange!!
                                    tooltipText.add("Level Range: ${levelRange.first} - ${levelRange.last}".text())
                                }

                                if (cond.canSeeSky != null) {
                                    val skyInfo = if (cond.canSeeSky!!) "Should see sky" else "Should NOT see sky"
                                    tooltipText.add(skyInfo.text())
                                }

                                if (cond.isRaining != null) {
                                    val rainInfo = if (cond.isRaining!!) "Weather: Raining" else "Weather: Clear"
                                    tooltipText.add(rainInfo.text())
                                }

                                if (cond.isThundering != null) {
                                    val thunderInfo =
                                        if (cond.isThundering!!) "Should be thundering" else "Should not be thundering"
                                    tooltipText.add(thunderInfo.text())
                                }

                                var lightString = ""
                                if (cond.minLight != null)
                                    lightString = if (cond.maxLight == null) "Min Light: ${cond.minLight}"
                                    else "Light: ${cond.minLight} - ${cond.maxLight}"
                                else if (cond.maxLight != null)
                                    lightString = "Max Light: ${cond.maxLight}"

                                if (lightString != "")
                                    tooltipText.add(lightString.text())


                                var skyLightString = ""
                                if (cond.minSkyLight != null)
                                    skyLightString = if (cond.maxSkyLight == null) "Min Sky Light: ${cond.minSkyLight}"
                                    else "Sky Light: ${cond.minSkyLight} - ${cond.maxSkyLight}"
                                else if (cond.maxSkyLight != null)
                                    skyLightString = "Max Sky Light: ${cond.maxSkyLight}"

                                if (skyLightString != "")
                                    tooltipText.add(skyLightString.text())


                                // TODO: Implement the following conditions
                                //  cond.timeRange
                                //  cond.moonPhase
                                //  cond.minY cond.maxY
                                //  There is also X and Z but I'm not sure if I need to implement it? I don't know what these are supposed to do

                                val structureConditions = spawn.conditions?.mapNotNull { structureCondition ->
                                    structureCondition.structures
                                }?.flatten()

                                if (!structureConditions.isNullOrEmpty()) {
                                    tooltipText.addEmptyLine()
                                    tooltipText.add("Need structure:".text().bold().darkGreen())
                                    structureConditions.forEach { structure ->
                                        val structureName = structure.toTranslationKey()

                                        tooltipText.add("structure.$structureName".asTranslated())
                                    }
                                }

                                val antiConditionBiomes = spawn.antiConditions?.mapNotNull { x -> x.biomes }?.flatten()
                                    ?.filterIsInstance<RegistryLikeTagCondition<Biome>>() ?: listOf()
                                // Too much stuff to write, we can skip it!
                                if (!condition.endsWith("is_overworld") && !condition.endsWith("is_nether")) {

                                    val availableBiomes = BiomeUtils.getAllBiomes(world).filter { b ->
                                        biomeCondition.fits(
                                            b.biome,
                                            biomeRegistry
                                        ) && !antiConditionBiomes.any { anti -> anti.fits(b.biome, biomeRegistry) }
                                    }.map { "biome.${it.identifier.toTranslationKey()}".asTranslated() }

                                    if (availableBiomes.isNotEmpty()) {
                                        tooltipText.addEmptyLine()
                                        tooltipText.add("Biomes:".text().bold().blue())
                                        availableBiomes.forEach { biome ->
                                            tooltipText.add(biome)
                                        }
                                    }

                                } else {

                                    if (antiConditionBiomes.isNotEmpty()) {
                                        tooltipText.addEmptyLine()
                                        tooltipText.add("Blacklisted Biomes:".text().bold().darkRed())
                                        antiConditionBiomes.forEach { b ->

                                            tooltipText.add(b.tag.id.toTranslationKey().asTranslated().darkRed())

                                        }
                                    }
                                }

                                val hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, tooltipText.toMutableText())
                                longTextDisplay.addText(
                                    condition.asTranslated().setStyle(Style.EMPTY.withHoverEvent(hoverEvent)), false
                                )
                            }
                        }
                    }
                }
            }
        }
        // Where to find end
    }
}