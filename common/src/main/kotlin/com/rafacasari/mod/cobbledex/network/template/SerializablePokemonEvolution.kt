package com.rafacasari.mod.cobbledex.network.template

import com.cobblemon.mod.common.api.conditional.RegistryLikeCondition
import com.cobblemon.mod.common.api.conditional.RegistryLikeTagCondition
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.pokemon.evolution.Evolution
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.api.text.plus
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.pokemon.evolution.variants.BlockClickEvolution
import com.cobblemon.mod.common.pokemon.evolution.variants.ItemInteractionEvolution
import com.cobblemon.mod.common.pokemon.evolution.variants.LevelUpEvolution
import com.cobblemon.mod.common.pokemon.evolution.variants.TradeEvolution
import com.cobblemon.mod.common.util.asTranslated
import com.rafacasari.mod.cobbledex.client.widget.LongTextDisplay
import com.rafacasari.mod.cobbledex.network.server.IEncodable
import com.rafacasari.mod.cobbledex.network.template.SerializablePokemonEvolution.PokemonEvolutionType.*
import com.rafacasari.mod.cobbledex.utils.PacketUtils.readNullableIdentifier
import com.rafacasari.mod.cobbledex.utils.PacketUtils.readNullableString
import com.rafacasari.mod.cobbledex.utils.PacketUtils.writeNullableIdentifier
import com.rafacasari.mod.cobbledex.utils.PacketUtils.writeNullableString
import com.rafacasari.mod.cobbledex.utils.logInfo
import com.rafacasari.mod.cobbledex.utils.logWarn
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

class SerializablePokemonEvolution() : IEncodable {

    enum class PokemonEvolutionType {
        BlockClick,
        ItemInteraction,
        LevelUp,
        Trade,
        Unknown
    }

    lateinit var requirements: List<SerializableEvolutionRequirement>

    fun drawInfo(longTextDisplay: LongTextDisplay) {
        species?.let { pokemon ->
            longTextDisplay.addPokemon(pokemon, resultAspects, pokemon.translatedName, true)

            when (evolutionType) {
                BlockClick -> {
                    logInfo("${pokemon.name} evolution is BlockClick")
                    requiredContextIdentifier?.let { itemIdentifier ->
                        val item = Registries.BLOCK.get(itemIdentifier)
                        val itemStack = ItemStack(item)
                        longTextDisplay.addItemEntry(itemStack, "Right click a ".text() + item.translationKey.asTranslated().bold())
                    }
                }
                ItemInteraction -> {
                    requiredContextIdentifier?.let { itemIdentifier ->
                        val item = Registries.ITEM.get(itemIdentifier)

                        val itemStack = ItemStack(item)
                        longTextDisplay.addItemEntry(itemStack, "Use a ".text() + item.translationKey.asTranslated().bold(), false)
                    }
                }


                LevelUp -> {
                    // Seems level up isn't actually level up, it's just a passive thing that check for the **conditions**
                }

                Trade -> {
                    longTextDisplay.addText("Trade to evolve".text())
                    // TODO: Does it need a specific trade?
                }

                Unknown -> {
                    // What we do here?
                }
            }

            if (requirements.isNotEmpty()) {
                longTextDisplay.addText("Conditions: ".text().bold(), false)
                requirements.forEach { req -> req.addText(longTextDisplay) }
            }
        }
    }

    lateinit var evolutionType: PokemonEvolutionType

    private var speciesIdentifier: Identifier? = null
    lateinit var resultAspects: Set<String>

    var consumeHeldItem: Boolean = false
    var requiredContextIdentifier: Identifier? = null

    // Used just on Trade context
    private var tradePokemonString: String? = null

    // Lazy properties
    val species by lazy {
        speciesIdentifier?.let {
            PokemonSpecies.getByIdentifier(it)
        }
    }

    val tradePokemon by lazy {
        tradePokemonString?.let {
            PokemonProperties.parse(it)
        }
    }



    constructor(evolution: Evolution) : this() {
        // species should always have a value, but since it's not a guaranteed result \
        evolution.result.species?.let { speciesIdentifier = PokemonSpecies.getByName(it)?.resourceIdentifier }
        resultAspects = evolution.result.aspects

        // Common variables (available in all types-)
        consumeHeldItem = evolution.consumeHeldItem
        requirements = evolution.requirements.map {
            SerializableEvolutionRequirement(it)
        }

        when(evolution) {
            is BlockClickEvolution -> {
                evolutionType = BlockClick
                val block = evolution.requiredContext
                if (block is RegistryLikeTagCondition<Block>)
                    requiredContextIdentifier = block.tag.id
                else logWarn("Is not a RegistryLikeTagCondition")
            }

            is ItemInteractionEvolution -> {
                evolutionType = ItemInteraction
                val item: RegistryLikeCondition<Item> = evolution.requiredContext.item
                logInfo("ID: " + evolution.id)
                // Let's start cache-ing!
                requiredContextIdentifier = speciesIdentifier?.let { speciesId ->
                    // val pair = Pair(speciesId, resultAspects)
                    // Check for cache, using Identifier and Aspects as a unique-key
                    if(evolutionItemCache.containsKey(evolution.id))
                        return@let evolutionItemCache[evolution.id]
                    else
                    {
                        logInfo("Using from cache")
                        // Cache not found, let's create it and store
                        evolutionItemCache[evolution.id] = Registries.ITEM.ids.firstOrNull {
                            item.fits(Registries.ITEM.get(it), Registries.ITEM)
                        }

                        return@let evolutionItemCache[evolution.id]
                    }
                }



//                if (item is RegistryLikeTagCondition<Item>) {
//                    // this is never called because it's not a RegistryLikeTagCondition
//                    requiredContextIdentifier = item.tag.id
//                }
//
//                //requiredContextIdentifier = (item as RegistryLikeTagCondition<Item>).tag.id
//                logInfo(if (requiredContextIdentifier == null) "Item is null" else "Item not null")
            }

            is LevelUpEvolution -> {
                evolutionType = LevelUp
                logInfo("ID: " + evolution.id)
                // Don't have parameter? Seems just need the level up condition in evolution.requirements
            }

            is TradeEvolution -> {
                evolutionType = Trade
                tradePokemonString = evolution.requiredContext.asString()
            }

            else -> {
                evolutionType = Unknown
                logInfo("No context reader for $evolution")
            }
        }


    }

    override fun encode(buffer: PacketByteBuf) {

        buffer.writeEnumConstant(evolutionType)
        buffer.writeNullableIdentifier(speciesIdentifier)
        buffer.writeCollection(resultAspects) {
                buff, value -> buff.writeString(value)
        }

        buffer.writeBoolean(consumeHeldItem)
        buffer.writeNullableIdentifier(requiredContextIdentifier)
        buffer.writeNullableString(tradePokemonString)

        buffer.writeCollection(requirements) {
            buff, value -> value.encode(buff)
        }
    }

    companion object {
        val evolutionItemCache: MutableMap<String, Identifier?> = mutableMapOf()

        fun decode(reader: PacketByteBuf) : SerializablePokemonEvolution
        {
            val evolution = SerializablePokemonEvolution()
            evolution.evolutionType = reader.readEnumConstant(PokemonEvolutionType::class.java)
            evolution.speciesIdentifier = reader.readNullableIdentifier()
            evolution.resultAspects = reader.readList { it.readString() }.toSet()

            evolution.consumeHeldItem = reader.readBoolean()
            evolution.requiredContextIdentifier = reader.readNullableIdentifier()
            evolution.tradePokemonString = reader.readNullableString()

            evolution.requirements = reader.readList {
                SerializableEvolutionRequirement.decode(it)
            }

            return evolution
        }
    }
}