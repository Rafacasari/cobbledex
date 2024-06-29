package com.rafacasari.mod.cobbledex.client.gui.menus

import com.cobblemon.mod.common.pokemon.FormData
import com.rafacasari.mod.cobbledex.CobbledexConstants.Client.discoveredList
import com.rafacasari.mod.cobbledex.api.classes.DiscoveryRegister
import com.rafacasari.mod.cobbledex.client.widget.LongTextDisplay
import com.rafacasari.mod.cobbledex.network.client.handlers.SyncServerSettingsHandler
import com.rafacasari.mod.cobbledex.network.template.SerializablePokemonEvolution
import com.rafacasari.mod.cobbledex.utils.MiscUtils.cobbledexTextTranslation

object EvolutionMenu {


    fun drawText(longTextDisplay: LongTextDisplay?, pokemon: FormData?, evolutions: List<SerializablePokemonEvolution>?) {
        if (longTextDisplay == null || pokemon == null || evolutions == null) return

        val config = SyncServerSettingsHandler.config
        val registerType = discoveredList[pokemon.species.showdownId()]?.get(pokemon.formOnlyShowdownId())?.status
        val hasCaught = registerType == DiscoveryRegister.RegisterType.CAUGHT
        val hasSeen = hasCaught || registerType == DiscoveryRegister.RegisterType.SEEN

        if (!config.ShowEvolutions_IsEnabled)
            longTextDisplay.addText(cobbledexTextTranslation("blocked_by_server"), false)
        else if (config.ShowEvolutions_NeedSeen && !hasSeen)
            longTextDisplay.addText(cobbledexTextTranslation("need_seen", cobbledexTextTranslation("need.view_evolutions")), false)
        else if (config.ShowEvolutions_NeedCatch && !hasCaught)
            longTextDisplay.addText(cobbledexTextTranslation("need_catch", cobbledexTextTranslation("need.view_evolutions")), false)
        else if (evolutions.isEmpty())
            longTextDisplay.addText(cobbledexTextTranslation("no_evolution_found", pokemon.species.translatedName))
        else evolutions.forEach { it.drawInfo(longTextDisplay) }

    }
}