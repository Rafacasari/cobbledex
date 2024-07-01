package com.rafacasari.mod.cobbledex.client.gui.menus

import com.cobblemon.mod.common.api.text.add
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.pokemon.FormData
import com.rafacasari.mod.cobbledex.client.widget.LongTextDisplay
import com.rafacasari.mod.cobbledex.utils.MiscUtils.cobbledexTextTranslation
import com.rafacasari.mod.cobbledex.utils.MiscUtils.format
import com.rafacasari.mod.cobbledex.utils.TypeChartUtils
import com.rafacasari.mod.cobbledex.utils.MiscUtils.withRGBColor

object BattleMenu {
    fun drawText(longTextDisplay: LongTextDisplay?, pokemon: FormData?) {
        if (pokemon == null || longTextDisplay == null) return

//        val config = SyncServerSettingsHandler.config
//        val registerType = discoveredList[pokemon.species.showdownId()]?.get(pokemon.formOnlyShowdownId())?.status
//        val hasCaught = registerType == DiscoveryRegister.RegisterType.CAUGHT
//        val hasSeen = hasCaught || registerType == DiscoveryRegister.RegisterType.SEEN

        val elementalMultipliers = ElementalTypes.all()
            .groupBy { TypeChartUtils.getModifier(it, pokemon.primaryType, pokemon.secondaryType) }
            .toSortedMap(Comparator.reverseOrder())

        if (elementalMultipliers.isNotEmpty()) {
            longTextDisplay.addText(cobbledexTextTranslation("battle.title").bold())
        }

        elementalMultipliers.filter { it.key != 1f }.forEach { elementalKey ->


            val key = "battle.damage_from.multiplier_${elementalKey.key.format().replace(".", "_")}"

            val translation = cobbledexTextTranslation(key, "%.2f". format(elementalKey.key))
            longTextDisplay.addText(translation)


            val typesText = elementalKey.value.fold("".text()) { acc, element ->
                if (elementalKey.value.first() != element)
                    acc.add(" ".text())

                acc.add(element.displayName.bold().withRGBColor(element.hue))

                return@fold acc
            }

            longTextDisplay.addText(typesText, false)
        }

    }
}