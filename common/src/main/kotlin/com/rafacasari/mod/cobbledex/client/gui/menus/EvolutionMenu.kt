package com.rafacasari.mod.cobbledex.client.gui.menus

import com.rafacasari.mod.cobbledex.client.widget.LongTextDisplay
import com.rafacasari.mod.cobbledex.network.template.SerializablePokemonEvolution

object EvolutionMenu {


    fun drawText(longTextDisplay: LongTextDisplay?, evolution: SerializablePokemonEvolution) {
        if (longTextDisplay == null) return

        evolution.drawInfo(longTextDisplay)
    }
}