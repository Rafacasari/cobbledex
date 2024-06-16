package com.rafacasari.mod.cobbledex.client.gui.menus

import com.cobblemon.mod.common.api.text.add
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.pokemon.Species
import com.rafacasari.mod.cobbledex.client.widget.LongTextDisplay
import com.rafacasari.mod.cobbledex.utils.TypeChartUtils
import com.rafacasari.mod.cobbledex.utils.withRGBColor

object BattleMenu {
    fun drawText(longTextDisplay: LongTextDisplay?, pokemon: Species?) {
        if (pokemon == null || longTextDisplay == null) return


        val elementalMultipliers = ElementalTypes.all()
            .groupBy { TypeChartUtils.getModifier(it, pokemon.primaryType, pokemon.secondaryType) }
            .toSortedMap(Comparator.reverseOrder())

        elementalMultipliers.filter { it.key != 1f }.forEach { elementalKey ->

            longTextDisplay.add("${elementalKey.key}x damage from".text())

            val typesText = elementalKey.value.fold("".text()) { acc, element ->
                if (elementalKey.value.first() != element)
                    acc.add(" ".text())

                acc.add(element.displayName.bold().withRGBColor(element.hue))
            }


            longTextDisplay.add(typesText, false)
        }

    }
}