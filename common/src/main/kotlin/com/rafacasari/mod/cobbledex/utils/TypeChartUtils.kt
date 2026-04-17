package com.rafacasari.mod.cobbledex.utils

import com.cobblemon.mod.common.battles.ai.strongBattleAI.AIUtility
import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.pokemon.FormData
import com.cobblemon.mod.common.pokemon.Pokemon

@Suppress("unused")
object TypeChartUtils {
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

        if (defenderType1 != null) multiplier *= AIUtility.getDamageMultiplier(type, defenderType1).toFloat()
        if (defenderType2 != null) multiplier *= AIUtility.getDamageMultiplier(type, defenderType2).toFloat()

        return multiplier
    }
}
