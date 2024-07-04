package com.rafacasari.mod.cobbledex.network.template

import com.cobblemon.mod.common.api.conditional.RegistryLikeTagCondition
import com.cobblemon.mod.common.api.moves.Moves
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.api.spawning.TimeRange
import com.cobblemon.mod.common.api.spawning.condition.MoonPhase
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.pokemon.evolution.requirements.*
import com.cobblemon.mod.common.registry.ItemIdentifierCondition
import com.cobblemon.mod.common.util.asTranslated
import com.rafacasari.mod.cobbledex.client.widget.LongTextDisplay
import com.rafacasari.mod.cobbledex.network.IEncodable
import com.rafacasari.mod.cobbledex.utils.MiscUtils.appendWithSeparator
import com.rafacasari.mod.cobbledex.utils.PacketUtils.readIntRange
import com.rafacasari.mod.cobbledex.utils.PacketUtils.readNullableBool
import com.rafacasari.mod.cobbledex.utils.PacketUtils.readNullableIdentifier
import com.rafacasari.mod.cobbledex.utils.PacketUtils.readNullableInt
import com.rafacasari.mod.cobbledex.utils.PacketUtils.readNullableIntRange
import com.rafacasari.mod.cobbledex.utils.PacketUtils.readNullableString
import com.rafacasari.mod.cobbledex.utils.PacketUtils.writeIntRange
import com.rafacasari.mod.cobbledex.utils.PacketUtils.writeNullableBool
import com.rafacasari.mod.cobbledex.utils.PacketUtils.writeNullableIdentifier
import com.rafacasari.mod.cobbledex.utils.PacketUtils.writeNullableInt
import com.rafacasari.mod.cobbledex.utils.PacketUtils.writeNullableIntRange
import com.rafacasari.mod.cobbledex.utils.PacketUtils.writeNullableString
import com.rafacasari.mod.cobbledex.utils.MiscUtils.cobbledexResource
import com.rafacasari.mod.cobbledex.utils.MiscUtils.logInfo
import com.rafacasari.mod.cobbledex.utils.MiscUtils.withRGBColor
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.registry.Registries
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.text.TextContent
import net.minecraft.util.Identifier
import net.minecraft.world.biome.Biome
import net.minecraft.world.gen.structure.Structure

class SerializableEvolutionRequirement(): IEncodable {


    // TODO: Add a "Margin Left" parameter, exclusively to use on ANY_REQUIREMENT
    fun addText(longTextDisplay: LongTextDisplay, padding: Int = 0) {

        when (type) {
            EvolutionRequirementType.DAMAGE_TAKEN -> {
                longTextDisplay.addText(Text.translatable("cobbledex.evolution.damage_taken", value.toString().text().bold()), false)
            }

            EvolutionRequirementType.HELD_ITEM -> {
               if (identifier != null) {
                   val itemStack = ItemStack(Registries.ITEM.get(identifier))
                   longTextDisplay.addItemEntry(itemStack, Text.translatable("cobbledex.evolution.held_item", itemStack.translationKey.asTranslated().bold()), false)
               } else {
                   longTextDisplay.addText( Text.translatable("cobbledex.evolution.held_item", "UNKNOWN".text().bold()), false)
               }
            }

            EvolutionRequirementType.FRIENDSHIP -> {

                val translation = Text.translatable("cobbledex.evolution.friendship", value.toString().text().bold())
                longTextDisplay.addIcon(HEART_ICON, translation, 16, 16, xOffset = -3.5f, yOffset = -2.5f, scale = 0.65f, breakLine = false)
            }

            EvolutionRequirementType.ANY_REQUIREMENT -> {
                intRange?.let {
                    anyRequirement?.let { x ->
                        if (x.size > 1)
                            longTextDisplay.addText(Text.translatable("cobbledex.evolution.any_requirement"), false)

                        x.forEach { req -> req.addText(longTextDisplay, if(x.size > 1) padding + 3 else padding) }
                    }
                }
            }

            EvolutionRequirementType.ATTACK_DEFENSE_RATIO -> {
                attackDefenceRatio?.let { ratio ->
                    val translationKey = when(ratio) {
                        AttackDefenceRatioRequirement.AttackDefenceRatio.ATTACK_HIGHER -> "cobbledex.evolution.attack_defence_ratio.attack_higher"
                        AttackDefenceRatioRequirement.AttackDefenceRatio.DEFENCE_HIGHER -> "cobbledex.evolution.attack_defence_ratio.defence_higher"
                        AttackDefenceRatioRequirement.AttackDefenceRatio.EQUAL -> "cobbledex.evolution.attack_defence_ratio.attack_higher"
                    }

                    longTextDisplay.addText(Text.translatable(translationKey), false)
                }
            }

            EvolutionRequirementType.BATTLE_CRITICAL_HITS -> {
                longTextDisplay.addText(Text.translatable("cobbledex.evolution.battle_critical_hits", value.toString().text().bold()), false)
            }

            EvolutionRequirementType.BLOCKS_TRAVELED -> {
                longTextDisplay.addText(Text.translatable("cobbledex.evolution.blocks_traveled", value.toString().text().bold()), false)
            }



            EvolutionRequirementType.LEVEL -> {
                intRange?.let {
                    //longTextDisplay.addText(Text.translatable("cobbledex.evolution.level", it.first.toString().text().bold()), false)
                    val translation = Text.translatable("cobbledex.evolution.level", it.first.toString().text().bold())
                    longTextDisplay.addIcon(LEVEL_ICON, translation, 16, 16, xOffset = -3.5f, yOffset = -2.5f, scale = 0.65f, breakLine = false)
                }
            }

            EvolutionRequirementType.MOVE_SET -> {
                stringValue?.let { moveName ->
                    val move = Moves.getByName(moveName)
                    if (move != null) {
                        longTextDisplay.addText(Text.translatable("cobbledex.evolution.move_set", move.displayName.withRGBColor(move.elementalType.hue).bold()), false)
                    } else
                    {
                        // Unrecognized move, use name instead
                        longTextDisplay.addText(Text.translatable("cobbledex.evolution.move_set", moveName.text().bold()), false)
                    }
                }
            }

            EvolutionRequirementType.MOVE_TYPE -> {
                stringValue?.let { typeName ->
                    val type = ElementalTypes.get(typeName)
                    if (type != null) {
                        longTextDisplay.addText(Text.translatable("cobbledex.evolution.move_type", type.displayName.withRGBColor(type.hue).bold()), false)
                    } else
                    {
                        // Unrecognized move, use name instead
                        longTextDisplay.addText(Text.translatable("cobbledex.evolution.move_type", typeName.text().bold()), false)
                    }
                }
            }

            EvolutionRequirementType.DEFEAT, EvolutionRequirementType.PARTY_MEMBER, EvolutionRequirementType.POKEMON_PROPERTIES -> {


                stringValue?.let { propertiesString ->
                    val properties = PokemonProperties.parse(propertiesString)

                    val text: MutableList<MutableText> = mutableListOf()
                    var containsSpecies = false

                    properties.species?.let { speciesName ->
                        val species = PokemonSpecies.getByName(speciesName)
                        if (species != null) {
                            containsSpecies = true
                            text.add(species.translatedName.bold())
                        }
                    }

                    if (!containsSpecies)
                        properties.teraType?.let { teraType ->
                            ElementalTypes.get(teraType)?.let { type ->
                                text.add(
                                    Text.translatable(
                                        "cobbledex.evolution.party_member.type",
                                        type.displayName.bold().withRGBColor(type.hue)
                                    )
                                )
                            }
                        }

                    properties.gender?.let { gender ->
                        text.add(text(gender.name))
                    }

                    properties.shiny?.let { shiny ->
                        text.add(text(if (shiny) "Shiny" else "Non-Shiny"))
                    }

                    properties.friendship?.let { friendship ->
                        text.add(text("Friendship: $friendship"))
                    }

                    properties.level?.let { level ->
                        text.add(text("Level: $level"))
                    }



                    if (text.isNotEmpty()) {
                        if (type == EvolutionRequirementType.DEFEAT)
                        {
                            longTextDisplay.addText(Text.translatable(
                                "cobbledex.evolution.defeat_specific",
                                text.appendWithSeparator(" "),
                                value.toString().text()), false)
                        }
                        else if (type == EvolutionRequirementType.PARTY_MEMBER)
                            longTextDisplay.addText(
                                Text.translatable(
                                    "cobbledex.evolution.party_member",
                                    text.appendWithSeparator(" ")
                                ), false
                            )
                        else
                            longTextDisplay.addText(
                                Text.translatable(
                                    "cobbledex.evolution.properties",
                                    text.appendWithSeparator(", ")
                                ), false
                            )
                    } else {
                        when (type) {
                            EvolutionRequirementType.DEFEAT -> longTextDisplay.addText(Text.translatable("cobbledex.evolution.defeat", value.toString().text().bold()), false)
                            EvolutionRequirementType.PARTY_MEMBER -> longTextDisplay.addText(Text.translatable("cobbledex.evolution.party_member", propertiesString), false)
                            EvolutionRequirementType.POKEMON_PROPERTIES -> longTextDisplay.addText(Text.translatable("cobbledex.evolution.properties", propertiesString), false)
                            else -> {}
                        }

                        logInfo("Seems text for $type is empty.\n${propertiesString}")
                    }
                }

            }

            EvolutionRequirementType.PLAYER_HAS_ADVANCEMENT -> {
                identifier?.let { advancement ->
                    longTextDisplay.addText(Text.translatable("cobbledex.evolution.player_has_advancement", advancement.toTranslationKey().asTranslated().bold()), false)
                }
            }

            EvolutionRequirementType.PROPERTY_RANGE -> {
                // Need stringValue between intRange
                val property = stringValue
                val valueRange = intRange?.let { value -> "${value.first} - ${value.last}" }

                if (property != null && valueRange != null)
                    longTextDisplay.addText(Text.translatable("cobbledex.evolution.property_range", property.text().bold(), valueRange.text().bold()))

            }

            EvolutionRequirementType.RECOIL -> {
                longTextDisplay.addText(Text.translatable("cobbledex.evolution.recoil", value.toString().text().bold()), false)
            }

            EvolutionRequirementType.STAT_EQUAL -> {
                val statOne = stringValue?.let { stat -> Stats.getStat(stat) }
                val statTwo = extraStringValue?.let { stat -> Stats.getStat(stat) }

                if (statOne != null && statTwo != null) {
                    val stat1 = MutableText.of(TextContent.EMPTY).append(statOne.displayName).bold()
                    val stat2 = MutableText.of(TextContent.EMPTY).append(statTwo.displayName).bold()
                    longTextDisplay.addText(Text.translatable("cobbledex.evolution.stat_equal", stat1, stat2), false)
                }
            }

            EvolutionRequirementType.STAT_COMPARE -> {
                val statOne = stringValue?.let { stat -> Stats.getStat(stat) }
                val statTwo = extraStringValue?.let { stat -> Stats.getStat(stat) }

                if (statOne != null && statTwo != null) {
                    val statLow = MutableText.of(TextContent.EMPTY).append(statOne.displayName).bold()
                    val statHigher = MutableText.of(TextContent.EMPTY).append(statTwo.displayName).bold()
                    longTextDisplay.addText(Text.translatable("cobbledex.evolution.stat_compare", statHigher, statLow), false)
                }
            }

            EvolutionRequirementType.TIME_RANGE -> {
                listIntRange?.let { ranges ->

                    val times = TimeRange.timeRanges.mapNotNull { time ->
                        if(time.value.ranges == ranges) time.key else null
                    }

                    val item = Registries.ITEM.get(Identifier("minecraft", "clock"))
                    val itemStack = ItemStack(item)
                    times.forEach { time ->
                        val translation = Text.translatable("cobbledex.evolution.time_range", time.text().bold())
                        longTextDisplay.addItemEntry(itemStack, translation, breakLine = false, disableTooltip = true)
                        //longTextDisplay.addText(translation, false)
                    }
                }
            }

            EvolutionRequirementType.USE_MOVE -> {
                val times = (value ?: 1).toString().text().bold()
                stringValue?.let { moveName ->
                    val move = Moves.getByName(moveName)
                    if (move != null)
                        longTextDisplay.addText(Text.translatable("cobbledex.evolution.use_move", move.displayName.withRGBColor(move.elementalType.hue).bold(), times), false)
                    else
                        longTextDisplay.addText(Text.translatable("cobbledex.evolution.use_move", moveName.text().bold(), times), false)

                }
            }

            EvolutionRequirementType.WEATHER_REQUIREMENT -> {
                if (isRaining != null || isThundering != null) {
                    val weather = when {
                        isRaining == true && isThundering == true -> "cobbledex.texts.weather.raining_and_thundering"
                        isRaining == true -> "cobbledex.texts.weather.raining"
                        isThundering == true -> "cobbledex.texts.weather.thundering"
                        else -> "cobbledex.texts.weather.clear"
                    }

                    longTextDisplay.addText(Text.translatable("cobbledex.evolution.weather",  Text.translatable(weather).bold()), false)
                }
            }

            EvolutionRequirementType.BIOME -> {
                // TODO: Add a new entry type into longTextDisplay "BiomeEntry" when you hover it show all possible biomes
                identifier?.let { biomeCondition ->
                    val translation = biomeCondition.toTranslationKey().replace("minecraft", "cobblemon").asTranslated().bold()
                    longTextDisplay.addText(Text.translatable("cobbledex.evolution.biome.condition", translation), false)
                }

                negativeIdentifier?.let { biomeAntiCondition ->
                    val translation = biomeAntiCondition.toTranslationKey().replace("minecraft", "cobblemon").asTranslated().bold()
                    longTextDisplay.addText(Text.translatable("cobbledex.evolution.biome.anti_condition", translation), false)
                }
            }

            EvolutionRequirementType.WORLD -> {
                identifier?.let { worldIdentifier ->
                    val translation = worldIdentifier.toTranslationKey().replace("minecraft", "cobblemon").asTranslated().bold()
                    longTextDisplay.addText(Text.translatable("cobbledex.evolution.world", translation), false)
                }
            }

            EvolutionRequirementType.MOON_PHASE -> {
                value?.let { phase ->
                    longTextDisplay.addText(Text.translatable("cobbledex.evolution.moon_phase", MoonPhase.entries[phase].toString().text().bold()), false)
                }
            }

            EvolutionRequirementType.STRUCTURE -> {
                identifier?.let { structureCondition ->
                    val translation = structureCondition.toTranslationKey().asTranslated().bold()
                    longTextDisplay.addText(Text.translatable("cobbledex.evolution.structure.condition", translation), false)
                }

                negativeIdentifier?.let { structureAntiCondition ->
                    val translation = structureAntiCondition.toTranslationKey().asTranslated().bold()
                    longTextDisplay.addText(Text.translatable("cobbledex.evolution.structure.anti_condition", translation), false)
                }
            }

            // Unrecognized type
            EvolutionRequirementType.UNKNOWN -> {
                stringValue?.let { typeName ->
                    longTextDisplay.addText("This condition has not been registered yet (${typeName})".text(), false)
                }
            }

            //            EvolutionRequirementType.POKEMON_PROPERTIES -> TODO()
//            EvolutionRequirementType.AREA_REQUIREMENT -> TODO()
            else -> {
                 longTextDisplay.addText("No text available for condition $type".text(), false)
            }

        }

    }

    enum class EvolutionRequirementType {
        // When type isn't detected, stringValue = expectedTypeName
        UNKNOWN,

        // Completed with X (value) Damage Taken
        DAMAGE_TAKEN,
        // Completed when giving a specific item (identifier) to Pokémon hold
        HELD_ITEM,
        // Completed when Pokémon reach X (value.toInt) friendship
        FRIENDSHIP,
        // Completed when any of the child requirements (anyRequirement) is complete
        ANY_REQUIREMENT,
        ATTACK_DEFENSE_RATIO,
        BATTLE_CRITICAL_HITS,
        BLOCKS_TRAVELED,
        DEFEAT,
        LEVEL,
        MOVE_SET,
        MOVE_TYPE,
        PARTY_MEMBER,
        PLAYER_HAS_ADVANCEMENT,
        POKEMON_PROPERTIES,
        PROPERTY_RANGE,
        RECOIL,
        STAT_EQUAL,
        STAT_COMPARE,
        TIME_RANGE,
        USE_MOVE,

        AREA_REQUIREMENT,
        WEATHER_REQUIREMENT,
        BIOME,
        WORLD,
        MOON_PHASE,
        STRUCTURE,
    }

    lateinit var type: EvolutionRequirementType
    var value: Int? = null
    var intRange: IntRange? = null
    var stringValue: String? = null
    var extraStringValue: String? = null

    var identifier: Identifier? = null
    var anyRequirement: List<SerializableEvolutionRequirement>? = null

    var listIntRange: List<IntRange>? = null

    var isRaining : Boolean? = null
    var isThundering : Boolean? = null

    // BiomeCondition
    var negativeIdentifier: Identifier? = null

    val attackDefenceRatio: AttackDefenceRatioRequirement.AttackDefenceRatio? by lazy {
        if (value != null)
            return@lazy AttackDefenceRatioRequirement.AttackDefenceRatio.entries[value!!]
        else
            return@lazy null
    }

    constructor (requirement: EvolutionRequirement) : this() {


        when(requirement)
        {
            is DamageTakenRequirement -> {
                type = EvolutionRequirementType.DAMAGE_TAKEN
                value = requirement.amount
            }

            is HeldItemRequirement -> {
                type = EvolutionRequirementType.HELD_ITEM
                val item = requirement.itemCondition.item
                if (item is ItemIdentifierCondition)
                    identifier = item.identifier

            }

            is FriendshipRequirement -> {
                type = EvolutionRequirementType.FRIENDSHIP
                value = requirement.amount
            }

            is AnyRequirement -> {
                type = EvolutionRequirementType.ANY_REQUIREMENT
                anyRequirement = requirement.possibilities.map { SerializableEvolutionRequirement(it) }
            }

            is AttackDefenceRatioRequirement -> {
                type = EvolutionRequirementType.ATTACK_DEFENSE_RATIO
                value = requirement.ratio.ordinal
            }

            is BattleCriticalHitsRequirement -> {
                type = EvolutionRequirementType.BATTLE_CRITICAL_HITS
                value = requirement.amount
            }

            is BlocksTraveledRequirement -> {
                type = EvolutionRequirementType.BLOCKS_TRAVELED
                value = requirement.amount
            }

            is DefeatRequirement -> {
                type = EvolutionRequirementType.DEFEAT
                value = requirement.amount
                stringValue = requirement.target.originalString
            }

            is LevelRequirement -> {
                type = EvolutionRequirementType.LEVEL
                intRange = IntRange(requirement.minLevel, requirement.maxLevel)
            }

            is MoveSetRequirement -> {
                type = EvolutionRequirementType.MOVE_SET
                stringValue = requirement.move.name
            }

            is MoveTypeRequirement -> {
                type = EvolutionRequirementType.MOVE_TYPE
                stringValue = requirement.type.name
            }

            is PartyMemberRequirement -> {
                type = EvolutionRequirementType.PARTY_MEMBER
                // Should use PokemonProperties.parse(stringValue) to read!

                stringValue = requirement.target.originalString
            }

            is PlayerHasAdvancementRequirement -> {
                type = EvolutionRequirementType.PLAYER_HAS_ADVANCEMENT
                identifier = requirement.requiredAdvancement
            }

            is PokemonPropertiesRequirement -> {
                type = EvolutionRequirementType.POKEMON_PROPERTIES
                // Should use PokemonProperties.parse(stringValue) to read!
                stringValue = requirement.target.originalString
            }

            is PropertyRangeRequirement -> {
                type = EvolutionRequirementType.PROPERTY_RANGE
                stringValue = requirement.feature
                intRange = requirement.range
            }

            is RecoilRequirement -> {
                type = EvolutionRequirementType.RECOIL
                value = requirement.amount
            }

            is StatEqualRequirement -> {
                type = EvolutionRequirementType.STAT_EQUAL
                stringValue = requirement.statOne
                extraStringValue = requirement.statTwo
            }

            is StatCompareRequirement -> {
                type = EvolutionRequirementType.STAT_COMPARE
                stringValue = requirement.lowStat
                extraStringValue = requirement.highStat
            }

            is TimeRangeRequirement -> {
                type = EvolutionRequirementType.TIME_RANGE
                listIntRange = requirement.range.ranges
            }

            is UseMoveRequirement -> {
                type = EvolutionRequirementType.USE_MOVE
                stringValue = requirement.move.name
                value = requirement.amount
            }

//            is AreaRequirement -> {
//                type = EvolutionRequirementType.AREA_REQUIREMENT
//                // TODO: Implement this
//            }

            is WeatherRequirement -> {
                type = EvolutionRequirementType.WEATHER_REQUIREMENT
                isRaining = requirement.isRaining
                isThundering = requirement.isThundering
            }

            is BiomeRequirement -> {
                type = EvolutionRequirementType.BIOME
                requirement.biomeCondition?.let { biomeCondition ->
                    if (biomeCondition is RegistryLikeTagCondition<Biome>)
                        identifier = biomeCondition.tag.id
                }

                requirement.biomeAnticondition?.let { biomeAntiCondition ->
                    if (biomeAntiCondition is RegistryLikeTagCondition<Biome>)
                        negativeIdentifier = biomeAntiCondition.tag.id
                }
            }

            is WorldRequirement -> {
                type = EvolutionRequirementType.WORLD
                identifier = requirement.identifier
            }

            is MoonPhaseRequirement -> {
                type = EvolutionRequirementType.MOON_PHASE
                value = requirement.moonPhase.ordinal
            }

            is StructureRequirement -> {
                type = EvolutionRequirementType.STRUCTURE
                requirement.structureCondition?.let { structure ->
                    if(structure is RegistryLikeTagCondition<Structure>)
                        identifier = structure.tag.id
                }

                requirement.structureAnticondition?.let { antiStructure ->
                    if(antiStructure is RegistryLikeTagCondition<Structure>)
                        negativeIdentifier = antiStructure.tag.id
                }
            }

            else -> {
                type = EvolutionRequirementType.UNKNOWN
                stringValue = requirement::class.simpleName
                logInfo("No serializer found for $requirement")
            }
        }

    }

    override fun encode(buffer: PacketByteBuf) {

        buffer.writeEnumConstant(type)
        buffer.writeNullableInt(value)
        buffer.writeNullableIntRange(intRange)
        buffer.writeNullableString(stringValue)
        buffer.writeNullableString(extraStringValue)
        buffer.writeNullableIdentifier(identifier)

        val anyReq = anyRequirement ?: listOf()
        buffer.writeCollection(anyReq) {
                buff, value -> value.encode(buff)
        }

        buffer.writeBoolean(listIntRange != null)
        listIntRange?.let {
            buffer.writeCollection(it) {
                    buff, value -> buff.writeIntRange(value)
            }
        }

        buffer.writeNullableBool(isRaining)
        buffer.writeNullableBool(isThundering)
        buffer.writeNullableIdentifier(negativeIdentifier)
    }

    companion object
    {
        val HEART_ICON = cobbledexResource("textures/gui/icons/friendship.png")
        val LEVEL_ICON = cobbledexResource("textures/gui/icons/level_up.png")
        fun decode(reader: PacketByteBuf) : SerializableEvolutionRequirement {
            val requirement = SerializableEvolutionRequirement()

            requirement.type = reader.readEnumConstant(EvolutionRequirementType::class.java)
            requirement.value = reader.readNullableInt()
            requirement.intRange = reader.readNullableIntRange()
            requirement.stringValue = reader.readNullableString()
            requirement.extraStringValue = reader.readNullableString()
            requirement.identifier = reader.readNullableIdentifier()

            requirement.anyRequirement = reader.readList {
                    listReader -> decode(listReader)
            }

            requirement.listIntRange = if(reader.readBoolean()) reader.readList {
                    listReader -> listReader.readIntRange()
            } else null

            requirement.isRaining = reader.readNullableBool()
            requirement.isThundering = reader.readNullableBool()
            requirement.negativeIdentifier = reader.readNullableIdentifier()

            return requirement
        }
    }
}