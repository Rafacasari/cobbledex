package com.rafacasari.mod.cobbledex.client.gui.menus

import com.cobblemon.mod.common.api.conditional.RegistryLikeTagCondition
import com.cobblemon.mod.common.api.text.*
import com.cobblemon.mod.common.pokemon.FormData
import com.cobblemon.mod.common.util.asTranslated
import com.rafacasari.mod.cobbledex.client.widget.LongTextDisplay
import com.rafacasari.mod.cobbledex.network.template.SerializableItemDrop
import com.rafacasari.mod.cobbledex.network.template.SerializablePokemonSpawnDetail
import com.rafacasari.mod.cobbledex.utils.BiomeUtils
import com.rafacasari.mod.cobbledex.utils.cobbledexTranslation
import com.rafacasari.mod.cobbledex.utils.format
import net.minecraft.client.MinecraftClient
import net.minecraft.client.world.ClientWorld
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.text.HoverEvent
import net.minecraft.text.Style
import net.minecraft.world.biome.Biome

object InfoMenu {

    fun drawText(longTextDisplay: LongTextDisplay?, pokemon: FormData?, spawnDetails: List<SerializablePokemonSpawnDetail>?, pokemonDrops: List<SerializableItemDrop>?) {
        if (longTextDisplay == null || pokemon == null) return

        pokemon.pokedex.forEach { pokedex ->
            longTextDisplay.addText(pokedex.asTranslated())
        }

        if (!pokemonDrops.isNullOrEmpty())
        {
            longTextDisplay.addText(cobbledexTranslation("cobbledex.texts.drops").bold())
            pokemonDrops.forEach { itemDrop ->
                val itemStack = ItemStack(Registries.ITEM.get(itemDrop.item))

                // Hacky way to show quantity/rolls in a better way
                val quantityRange = itemDrop.quantityRange
                val quantity: String = if (quantityRange.first == quantityRange.last) quantityRange.first.toString() else "${quantityRange.first}-${quantityRange.last}"

                val text = "item.${itemDrop.item.toTranslationKey()}".asTranslated()
                text.add(" | ${itemDrop.percentage.format()}% | ${quantity}x".text())

                longTextDisplay.addItemEntry(itemStack, text, false)
                //longTextDisplay.addText(text, false)

            }

        }

        val world: ClientWorld? = MinecraftClient.getInstance().world
        if (world != null && !spawnDetails.isNullOrEmpty()) {
            longTextDisplay.addText(cobbledexTranslation("cobbledex.texts.biomes").bold())

            val biomeRegistry = BiomeUtils.getBiomesRegistry(world)

            spawnDetails.forEach { spawn ->
                spawn.conditions?.forEach { cond ->
                    cond.biomes?.forEach { biomeCondition ->
                        if (biomeCondition is RegistryLikeTagCondition<Biome>) {
                            val tooltipText = biomeCondition.tag.id.toTranslationKey().asTranslated().bold()
                            val condition = biomeCondition.tag.id.toTranslationKey()
                            tooltipText.add(
                                "\nWeight: ${spawn.weight}".text().setStyle(Style.EMPTY.withBold(false))
                            )
                            if (spawn.levelRange != null) {
                                val levelRange = spawn.levelRange!!
                                tooltipText.add(
                                    "\nLevel Range: ${levelRange.first}-${levelRange.last}".text()
                                        .setStyle(Style.EMPTY.withBold(false))
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
                                val thunderInfo =
                                    if (cond.isThundering!!) "Should be thundering" else "Should not be thundering"
                                tooltipText.add("\n$thunderInfo".text().setStyle(Style.EMPTY.withBold(false)))
                            }

                            if (cond.minLight != null)
                                tooltipText.add(
                                    "\nMin Light: ${cond.minLight}".text().setStyle(Style.EMPTY.withBold(false))
                                )

                            if (cond.minSkyLight != null)
                                tooltipText.add(
                                    "\nMin Sky Light: ${cond.minSkyLight}".text()
                                        .setStyle(Style.EMPTY.withBold(false))
                                )

                            if (cond.maxLight != null)
                                tooltipText.add(
                                    "\nMax Light: ${cond.maxLight}".text().setStyle(Style.EMPTY.withBold(false))
                                )

                            if (cond.maxSkyLight != null)
                                tooltipText.add(
                                    "\nMax Sky Light: ${cond.maxSkyLight}".text()
                                        .setStyle(Style.EMPTY.withBold(false))
                                )

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
                                    tooltipText.add(
                                        "structure.$structureName".asTranslated()
                                            .setStyle(Style.EMPTY.withBold(false))
                                    )
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
                                                .setStyle(Style.EMPTY.withBold(false))
                                        )

                                    }
                                }
                            }

                            val hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, tooltipText)

                            longTextDisplay.addText(
                                condition.asTranslated().setStyle(Style.EMPTY.withHoverEvent(hoverEvent)),
                                false
                            )
                        }
                    }
                }

            }


        }
    }

}