package com.rafacasari.mod.cobbledex.utils

import com.cobblemon.mod.common.api.types.ElementalType
import com.rafacasari.mod.cobbledex.cobblemon.showdown.ShowdownService


object TypeChartUtils {

    private var typeChart: HashMap<String, HashMap<String, Int>>? = null

    fun getModifier(type: ElementalType, defenderType1: ElementalType?, defenderType2: ElementalType?): Float {
        var multiplier = 1f

        if (defenderType1 != null) multiplier *= getDamageTaken(type.name, defenderType1.name).toFloat()
        if (defenderType2 != null) multiplier *= getDamageTaken(type.name, defenderType2.name).toFloat()

        return multiplier
    }

    private fun getDamageTaken(moveName: String, typeName: String?): Number {
        if (typeChart == null)
            typeChart = ShowdownService.getTypeChart()

        val damageTaken = typeChart!![typeName] ?: return 1

        val damageType = damageTaken[moveName] ?: return 1

        return getMultiplier(damageType)
    }

    private fun getMultiplier(damage: Int): Float {
        return when (damage) {
            1 -> 2f
            2 -> 0.5f
            3 -> 0f
            else -> 1f
        }
    }
}