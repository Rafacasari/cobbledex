package com.rafacasari.mod.cobbledex.utils

import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.pokemon.FormData
import com.cobblemon.mod.common.pokemon.Pokemon
import com.rafacasari.mod.cobbledex.cobblemon.showdown.ShowdownService

@Suppress("unused")
object TypeChartUtils {
    private var typeChart: HashMap<String, HashMap<String, Int>>? = null

    /**
     * Get the damage multiplier for [type] based on [Pokemon]
     */
    fun getModifier(type: ElementalType, pokemon: Pokemon): Float = getModifier(type, pokemon.primaryType, pokemon.secondaryType)

    /**
     * Get the damage multiplier for [type] based on [FormData]
     */
    fun getModifier(type: ElementalType, formData: FormData): Float = getModifier(type, formData.primaryType, formData.secondaryType)

    /**
     * Get the damage multiplier for [type] based on [defenderType1] and [defenderType2]
     */
    fun getModifier(type: ElementalType, defenderType1: ElementalType?, defenderType2: ElementalType?): Float {
        var multiplier = 1f

        if (defenderType1 != null) multiplier *= getDamageTaken(type.name, defenderType1.name).toFloat()
        if (defenderType2 != null) multiplier *= getDamageTaken(type.name, defenderType2.name).toFloat()

        return multiplier
    }

    private fun getDamageTaken(attackType: String, defenderType: String?): Number {
        if (typeChart == null)
            typeChart = ShowdownService.getTypeChart()

        return typeChart?.let {
            val damageTaken = it[defenderType] ?: return@let 1
            val damageType = damageTaken[attackType] ?: return@let 1

            return@let getMultiplier(damageType)
        } ?: 1
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