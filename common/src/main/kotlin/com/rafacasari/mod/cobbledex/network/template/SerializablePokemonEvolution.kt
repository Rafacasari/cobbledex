package com.rafacasari.mod.cobbledex.network.template

import com.cobblemon.mod.common.api.conditional.RegistryLikeTagCondition
import com.cobblemon.mod.common.api.pokemon.evolution.Evolution
import com.cobblemon.mod.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cobblemon.mod.common.pokemon.evolution.requirements.*
import com.cobblemon.mod.common.pokemon.evolution.requirements.template.EntityQueryRequirement
import com.cobblemon.mod.common.pokemon.evolution.variants.BlockClickEvolution
import com.cobblemon.mod.common.pokemon.evolution.variants.ItemInteractionEvolution
import com.cobblemon.mod.common.pokemon.evolution.variants.LevelUpEvolution
import com.cobblemon.mod.common.pokemon.evolution.variants.TradeEvolution
import com.rafacasari.mod.cobbledex.network.server.IEncodable
import com.rafacasari.mod.cobbledex.utils.PacketUtils.readIntRange
import com.rafacasari.mod.cobbledex.utils.PacketUtils.readNullableIdentifier
import com.rafacasari.mod.cobbledex.utils.PacketUtils.readNullableInt
import com.rafacasari.mod.cobbledex.utils.PacketUtils.readNullableIntRange
import com.rafacasari.mod.cobbledex.utils.PacketUtils.readNullableString
import com.rafacasari.mod.cobbledex.utils.PacketUtils.writeIntRange
import com.rafacasari.mod.cobbledex.utils.PacketUtils.writeNullableIdentifier
import com.rafacasari.mod.cobbledex.utils.PacketUtils.writeNullableInt
import com.rafacasari.mod.cobbledex.utils.PacketUtils.writeNullableIntRange
import com.rafacasari.mod.cobbledex.utils.PacketUtils.writeNullableString
import com.rafacasari.mod.cobbledex.utils.logInfo
import net.minecraft.item.Item
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

class SerializablePokemonEvolution() : IEncodable {

    lateinit var requirements: List<SerializableEvolutionRequirement>


    constructor(evolution: Evolution) : this() {
        when(evolution) {
            is BlockClickEvolution -> {
                evolution.requiredContext
            }

            is ItemInteractionEvolution -> {

            }

            is LevelUpEvolution -> {

            }

            is TradeEvolution -> {

            }

            // No context available, so it's just conditions?
            else -> {

                logInfo("No context reader for $evolution")
            }
        }

        requirements = evolution.requirements.map {
            SerializableEvolutionRequirement(it)
        }
    }

    override fun encode(buffer: PacketByteBuf) {

    }

    companion object {
        fun decode(reader: PacketByteBuf) : SerializablePokemonEvolution
        {
            val evolution = SerializablePokemonEvolution()

            return evolution
        }
    }
}

enum class EvolutionRequirementType {
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
    PLAYER_AS_ADVANCEMENT,
    POKEMON_PROPERTIES,
    PROPERTY_RANGE,
    RECOIL,
    STAT_EQUAL,
    TIME_RANGE,
    USE_MOVE,
    ENTITY_QUERY,
}

class SerializableEvolutionRequirement(): IEncodable {


    lateinit var type: EvolutionRequirementType
    var value: Int? = null
    var intRange: IntRange? = null
    var stringValue: String? = null
    var identifier: Identifier? = null
    var anyRequirement: List<SerializableEvolutionRequirement>? = null

    var listIntRange: List<IntRange>? = null

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
                identifier = (requirement.itemCondition.item as RegistryLikeTagCondition<Item>).tag.id
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
                stringValue = requirement.target.asString(" ")
            }

            is PlayerHasAdvancementRequirement -> {
                type = EvolutionRequirementType.PLAYER_AS_ADVANCEMENT
                identifier = requirement.requiredAdvancement
            }

            is PokemonPropertiesRequirement -> {
                type = EvolutionRequirementType.POKEMON_PROPERTIES
                // Should use PokemonProperties.parse(stringValue) to read!
                stringValue = requirement.target.asString(" ")
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
                stringValue = "${requirement.statOne}-${requirement.statTwo}"
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

            is EntityQueryRequirement -> {
                type = EvolutionRequirementType.ENTITY_QUERY
                logInfo("No data available for EntityQuery")
            }

            // Don't seem to be requirement?
//            is BlockClickEvolution -> {
//                type = EvolutionRequirementType.BLOCK_CLICK
//                identifier = (requirement.requiredContext as RegistryLikeTagCondition<Block>).tag.id
//            }
//
//            // Don't seem to be requirement?
//            is ItemInteractionEvolution -> {
//                type = EvolutionRequirementType.ITEM_INTERACTION
//                identifier = (requirement.requiredContext.item as RegistryLikeTagCondition<Item>).tag.id
//            }

            else -> {
                logInfo("No serializer found for $requirement")
            }
        }

    }

    override fun encode(buffer: PacketByteBuf) {

        buffer.writeEnumConstant(type)
        buffer.writeNullableInt(value)
        buffer.writeNullableIntRange(intRange)
        buffer.writeNullableString(stringValue)
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
    }

    companion object
    {
        fun decode(reader: PacketByteBuf) : SerializableEvolutionRequirement {
            val requirement = SerializableEvolutionRequirement()

            requirement.type = reader.readEnumConstant(EvolutionRequirementType::class.java)
            requirement.value = reader.readNullableInt()
            requirement.intRange = reader.readNullableIntRange()
            requirement.stringValue = reader.readNullableString()
            requirement.identifier = reader.readNullableIdentifier()

            requirement.anyRequirement = reader.readList {
                listReader -> decode(listReader)
            }

            requirement.listIntRange = if(reader.readBoolean()) reader.readList {
                listReader -> listReader.readIntRange()
            } else null


            return requirement
        }
    }
}