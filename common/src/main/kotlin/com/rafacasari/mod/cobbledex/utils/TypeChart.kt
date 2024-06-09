package com.rafacasari.mod.cobbledex.utils
//
//import com.cobblemon.mod.common.api.types.ElementalType
//import com.rafacasari.mod.cobbledex.Cobbledex
//
//data class Type(val damageTaken: Map<String, Int>)
//
//object TypeChart {
//    private val types = mapOf("Bug" to Type(
//            damageTaken = mapOf(
//                "Bug" to 0, "Dark" to 0, "Dragon" to 0, "Electric" to 0, "Fairy" to 0,
//                "Fighting" to 2, "Fire" to 1, "Flying" to 1, "Ghost" to 0, "Grass" to 2,
//                "Ground" to 2, "Ice" to 0, "Normal" to 0, "Poison" to 0, "Psychic" to 0,
//                "Rock" to 1, "Steel" to 0, "Stellar" to 0, "Water" to 0
//            )
//        ), "Dark" to Type(
//            damageTaken = mapOf(
//                "prankster" to 3, "Bug" to 1, "Dark" to 2, "Dragon" to 0, "Electric" to 0,
//                "Fairy" to 1, "Fighting" to 1, "Fire" to 0, "Flying" to 0, "Ghost" to 2,
//                "Grass" to 0, "Ground" to 0, "Ice" to 0, "Normal" to 0, "Poison" to 0,
//                "Psychic" to 3, "Rock" to 0, "Steel" to 0, "Stellar" to 0, "Water" to 0
//            )
//        ), "Dragon" to Type(
//            damageTaken = mapOf(
//                "Bug" to 0, "Dark" to 0, "Dragon" to 1, "Electric" to 2, "Fairy" to 1,
//                "Fighting" to 0, "Fire" to 2, "Flying" to 0, "Ghost" to 0, "Grass" to 2,
//                "Ground" to 0, "Ice" to 1, "Normal" to 0, "Poison" to 0, "Psychic" to 0,
//                "Rock" to 0, "Steel" to 0, "Stellar" to 0, "Water" to 2
//            )
//        ), "Electric" to Type(
//            damageTaken = mapOf(
//                "par" to 3, "Bug" to 0, "Dark" to 0, "Dragon" to 0, "Electric" to 2,
//                "Fairy" to 0, "Fighting" to 0, "Fire" to 0, "Flying" to 2, "Ghost" to 0,
//                "Grass" to 0, "Ground" to 1, "Ice" to 0, "Normal" to 0, "Poison" to 0,
//                "Psychic" to 0, "Rock" to 0, "Steel" to 2, "Stellar" to 0, "Water" to 0
//            )
//        ), "Fairy" to Type(
//            damageTaken = mapOf(
//                "Bug" to 2, "Dark" to 2, "Dragon" to 3, "Electric" to 0, "Fairy" to 0,
//                "Fighting" to 2, "Fire" to 0, "Flying" to 0, "Ghost" to 0, "Grass" to 0,
//                "Ground" to 0, "Ice" to 0, "Normal" to 0, "Poison" to 1, "Psychic" to 0,
//                "Rock" to 0, "Steel" to 1, "Stellar" to 0, "Water" to 0
//            )
//        ), "Fighting" to Type(
//            damageTaken = mapOf(
//                "Bug" to 2, "Dark" to 2, "Dragon" to 0, "Electric" to 0, "Fairy" to 1,
//                "Fighting" to 0, "Fire" to 0, "Flying" to 1, "Ghost" to 0, "Grass" to 0,
//                "Ground" to 0, "Ice" to 0, "Normal" to 0, "Poison" to 0, "Psychic" to 1,
//                "Rock" to 2, "Steel" to 0, "Stellar" to 0, "Water" to 0
//            )
//        ), "Fire" to Type(
//            damageTaken = mapOf(
//                "brn" to 3, "Bug" to 2, "Dark" to 0, "Dragon" to 0, "Electric" to 0,
//                "Fairy" to 2, "Fighting" to 0, "Fire" to 2, "Flying" to 0, "Ghost" to 0,
//                "Grass" to 2, "Ground" to 1, "Ice" to 2, "Normal" to 0, "Poison" to 0,
//                "Psychic" to 0, "Rock" to 1, "Steel" to 2, "Stellar" to 0, "Water" to 1
//            )
//        ), "Flying" to Type(
//            damageTaken = mapOf(
//                "Bug" to 2, "Dark" to 0, "Dragon" to 0, "Electric" to 1, "Fairy" to 0,
//                "Fighting" to 2, "Fire" to 0, "Flying" to 0, "Ghost" to 0, "Grass" to 2,
//                "Ground" to 3, "Ice" to 1, "Normal" to 0, "Poison" to 0, "Psychic" to 0,
//                "Rock" to 1, "Steel" to 0, "Stellar" to 0, "Water" to 0
//            )
//        ), "Ghost" to Type(
//            damageTaken = mapOf(
//                "trapped" to 3, "Bug" to 2, "Dark" to 1, "Dragon" to 0, "Electric" to 0,
//                "Fairy" to 0, "Fighting" to 3, "Fire" to 0, "Flying" to 0, "Ghost" to 1,
//                "Grass" to 0, "Ground" to 0, "Ice" to 0, "Normal" to 3, "Poison" to 2,
//                "Psychic" to 0, "Rock" to 0, "Steel" to 0, "Stellar" to 0, "Water" to 0
//            )
//        ), "Grass" to Type(
//            damageTaken = mapOf(
//                "powder" to 3, "Bug" to 1, "Dark" to 0, "Dragon" to 0, "Electric" to 2,
//                "Fairy" to 0, "Fighting" to 0, "Fire" to 1, "Flying" to 1, "Ghost" to 0,
//                "Grass" to 2, "Ground" to 2, "Ice" to 1, "Normal" to 0, "Poison" to 1,
//                "Psychic" to 0, "Rock" to 0, "Steel" to 0, "Stellar" to 0, "Water" to 2
//            )
//        ), "Ground" to Type(
//            damageTaken = mapOf(
//                "sandstorm" to 3, "Bug" to 0, "Dark" to 0, "Dragon" to 0, "Electric" to 3,
//                "Fairy" to 0, "Fighting" to 0, "Fire" to 0, "Flying" to 0, "Ghost" to 0,
//                "Grass" to 1, "Ground" to 0, "Ice" to 1, "Normal" to 0, "Poison" to 2,
//                "Psychic" to 0, "Rock" to 2, "Steel" to 0, "Stellar" to 0, "Water" to 1
//            )
//        ), "Ice" to Type(
//            damageTaken = mapOf(
//                "hail" to 3, "Bug" to 0, "Dark" to 0, "Dragon" to 0, "Electric" to 0,
//                "Fairy" to 0, "Fighting" to 1, "Fire" to 1, "Flying" to 0, "Ghost" to 0,
//                "Grass" to 2, "Ground" to 2, "Ice" to 2, "Normal" to 0, "Poison" to 0,
//                "Psychic" to 0, "Rock" to 1, "Steel" to 1, "Stellar" to 0, "Water" to 0
//            )
//        ), "Normal" to Type(
//            damageTaken = mapOf(
//                "Bug" to 0, "Dark" to 0, "Dragon" to 0, "Electric" to 0, "Fairy" to 0,
//                "Fighting" to 1, "Fire" to 0, "Flying" to 0, "Ghost" to 3, "Grass" to 0,
//                "Ground" to 0, "Ice" to 0, "Normal" to 0, "Poison" to 0, "Psychic" to 0,
//                "Rock" to 0, "Steel" to 0, "Stellar" to 0, "Water" to 0
//            )
//        ), "Poison" to Type(
//            damageTaken = mapOf(
//                "Bug" to 2, "Dark" to 0, "Dragon" to 0, "Electric" to 0, "Fairy" to 2,
//                "Fighting" to 2, "Fire" to 0, "Flying" to 0, "Ghost" to 1, "Grass" to 2,
//                "Ground" to 1, "Ice" to 0, "Normal" to 0, "Poison" to 2, "Psychic" to 1,
//                "Rock" to 0, "Steel" to 0, "Stellar" to 0, "Water" to 0
//            )
//        ), "Psychic" to Type(
//            damageTaken = mapOf(
//                "Bug" to 1, "Dark" to 1, "Dragon" to 0, "Electric" to 0, "Fairy" to 0,
//                "Fighting" to 2, "Fire" to 0, "Flying" to 0, "Ghost" to 1, "Grass" to 0,
//                "Ground" to 0, "Ice" to 0, "Normal" to 0, "Poison" to 0, "Psychic" to 2,
//                "Rock" to 0, "Steel" to 0, "Stellar" to 0, "Water" to 0
//            )
//        ), "Rock" to Type(
//            damageTaken = mapOf(
//                "sandstorm" to 3, "Bug" to 2, "Dark" to 0, "Dragon" to 0, "Electric" to 0,
//                "Fairy" to 0, "Fighting" to 1, "Fire" to 2, "Flying" to 2, "Ghost" to 0,
//                "Grass" to 1, "Ground" to 1, "Ice" to 0, "Normal" to 2, "Poison" to 2,
//                "Psychic" to 0, "Rock" to 0, "Steel" to 1, "Stellar" to 0, "Water" to 1
//            )
//        ), "Steel" to Type(
//            damageTaken = mapOf(
//                "sandstorm" to 3, "Bug" to 2, "Dark" to 0, "Dragon" to 2, "Electric" to 1,
//                "Fairy" to 2, "Fighting" to 1, "Fire" to 1, "Flying" to 2, "Ghost" to 0,
//                "Grass" to 2, "Ground" to 1, "Ice" to 2, "Normal" to 2, "Poison" to 3,
//                "Psychic" to 2, "Rock" to 2, "Steel" to 2, "Stellar" to 0, "Water" to 0
//            )
//        ), "Stellar" to Type(
//            damageTaken = mapOf(
//                "Bug" to 0, "Dark" to 0, "Dragon" to 0, "Electric" to 0, "Fairy" to 0,
//                "Fighting" to 0, "Fire" to 0, "Flying" to 0, "Ghost" to 0, "Grass" to 0,
//                "Ground" to 0, "Ice" to 0, "Normal" to 0, "Poison" to 0, "Psychic" to 0,
//                "Rock" to 0, "Steel" to 0, "Stellar" to 0, "Water" to 0
//            )
//        ), "Water" to Type(
//            damageTaken = mapOf(
//                "Bug" to 0, "Dark" to 0, "Dragon" to 0, "Electric" to 1, "Fairy" to 0,
//                "Fighting" to 0, "Fire" to 2, "Flying" to 0, "Ghost" to 0, "Grass" to 1,
//                "Ground" to 2, "Ice" to 2, "Normal" to 0, "Poison" to 0, "Psychic" to 0,
//                "Rock" to 2, "Steel" to 2, "Stellar" to 0, "Water" to 2
//            )
//        )
//    )
//
//
//    fun getImmunity(source: ElementalType, target: Any): Boolean {
//        val sourceType: String = getFormatedName(source.name)
//        val targetTyping: Any = when (target) {
//            is String -> target
//            is ElementalType -> getFormatedName(target.name)
//            is Iterable<*> -> target.map { value -> if(value is ElementalType) getFormatedName(value.name) else value }.toList()
//            else -> {
//                target
//            }
//        }
//
//        if (targetTyping is List<*>) {
//            for (type in targetTyping) {
//                if (!getImmunity(source, type as String)) return false
//            }
//            return true
//        }
//
//        val typeData = types[targetTyping as String]
//        return typeData?.damageTaken?.get(sourceType) != 3
//    }
//
//    fun getEffectiveness(source: ElementalType, target: Iterable<ElementalType>) : Int {
//        return getEffectivenessInternal(source, target)
//    }
//
//
//    private fun getFormatedName(name: String) : String {
//        return name[0].uppercase() + name.substring(1)
//    }
//
//    private fun getEffectivenessInternal(source: ElementalType, target: Any): Int {
//        val sourceType: String = getFormatedName(source.name)
//
//        val targetTyping: Any = when (target) {
//            is String -> target
//            is ElementalType -> getFormatedName(target.name)
//            is Iterable<*> -> target.map { value -> if(value is ElementalType) getFormatedName(value.name) else value }.toList()
//            else -> {
//                Cobbledex.LOGGER.error("Invalid target for TypeChart.getEffectiveness")
//                target
//            }
//        }
//
//        var totalTypeMod = 0
//        if (targetTyping is List<*>) {
//            for (type in targetTyping) {
//                if (type != null && type is String) {
//                    totalTypeMod += getEffectivenessInternal(source, type)
//                }
//            }
//            return totalTypeMod
//        }
//
//        val typeData = types[targetTyping as String]
//        return when (typeData?.damageTaken?.get(sourceType)) {
//            1 -> 1 // super-effective
//            2 -> -1 // resist
//            else -> 0
//        }
//    }
//}