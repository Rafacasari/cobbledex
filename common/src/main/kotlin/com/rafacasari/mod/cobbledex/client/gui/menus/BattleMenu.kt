package com.rafacasari.mod.cobbledex.client.gui.menus

import com.cobblemon.mod.common.api.text.add
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.pokemon.FormData
import com.rafacasari.mod.cobbledex.api.classes.DiscoveryRegister
import com.rafacasari.mod.cobbledex.client.gui.CobbledexCollectionGUI
import com.rafacasari.mod.cobbledex.client.widget.LongTextDisplay
import com.rafacasari.mod.cobbledex.network.client.handlers.SyncServerSettingsHandler
import com.rafacasari.mod.cobbledex.utils.TypeChartUtils
import com.rafacasari.mod.cobbledex.utils.withRGBColor
import net.minecraft.text.Text

object BattleMenu {
    fun drawText(longTextDisplay: LongTextDisplay?, pokemon: FormData?) {
        if (pokemon == null || longTextDisplay == null) return

        val config = SyncServerSettingsHandler.config
        val registerType = CobbledexCollectionGUI.discoveredList[pokemon.species.showdownId()]?.get(pokemon.formOnlyShowdownId())?.status
        val hasCaught = registerType == DiscoveryRegister.RegisterType.CAUGHT
        val hasSeen = hasCaught || registerType == DiscoveryRegister.RegisterType.SEEN


        val elementalMultipliers = ElementalTypes.all()
            .groupBy { TypeChartUtils.getModifier(it, pokemon.primaryType, pokemon.secondaryType) }
            .toSortedMap(Comparator.reverseOrder())

        elementalMultipliers.filter { it.key != 1f }.forEach { elementalKey ->

            val translation = Text.translatable("cobbledex.texts.battle.damage_from", elementalKey.key)
            longTextDisplay.addText(translation)

            val typesText = elementalKey.value.fold("".text()) { acc, element ->
                if (elementalKey.value.first() != element)
                    acc.add(" ".text())

                acc.add(element.displayName.bold().withRGBColor(element.hue))
            }

            longTextDisplay.addText(typesText, false)
        }

    }
}