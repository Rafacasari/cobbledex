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
import com.cobblemon.mod.common.registry.ItemIdentifierCondition
import com.cobblemon.mod.common.util.asTranslated
import com.rafacasari.mod.cobbledex.client.widget.LongTextDisplay
import com.rafacasari.mod.cobbledex.network.server.IEncodable
import com.rafacasari.mod.cobbledex.network.template.SerializablePokemonEvolution.PokemonEvolutionType.*
import com.rafacasari.mod.cobbledex.utils.PacketUtils.readNullableIdentifier
import com.rafacasari.mod.cobbledex.utils.PacketUtils.readNullableString
import com.rafacasari.mod.cobbledex.utils.PacketUtils.writeNullableIdentifier
import com.rafacasari.mod.cobbledex.utils.PacketUtils.writeNullableString
import com.rafacasari.mod.cobbledex.utils.bold
import com.rafacasari.mod.cobbledex.utils.logInfo
import com.rafacasari.mod.cobbledex.utils.logWarn
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.registry.Registries
import net.minecraft.text.Text
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
                        val translation = Text.translatable("cobbledex.evolution.use_item", itemStack.name.bold())
                        longTextDisplay.addItemEntry(itemStack, translation, false)
                    }
                }

                LevelUp -> {
                    // Seems level up isn't actually level up, it's just a passive thing that check for the **conditions**
                }

                Trade -> {

                    val item = Registries.ITEM.get(Identifier("cobblemon", "link_cable"))
                    val itemStack = ItemStack(item)

                    val translation =  tradePokemon?.species?.let { speciesName ->
                        val tradeSpecies = PokemonSpecies.getByName(speciesName)
                        if (tradeSpecies != null)
                            Text.translatable("cobbledex.evolution.trade_specific", tradeSpecies.translatedName.bold(), itemStack.name.bold())
                        Text.translatable("cobbledex.evolution.trade_specific", speciesName.text().bold(), itemStack.name.bold())
                    } ?: Text.translatable("cobbledex.evolution.trade_any", itemStack.name.bold())

                    longTextDisplay.addItemEntry(itemStack, translation, false, disableTooltip = false)
                }

                Unknown -> {
                    // What we do here?
                }
            }

            if (requirements.isNotEmpty()) {
//                longTextDisplay.addText("Conditions: ".text().bold(), false)
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
                if (item is ItemIdentifierCondition)
                    requiredContextIdentifier = item.identifier

            }

            is LevelUpEvolution -> {
                evolutionType = LevelUp
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