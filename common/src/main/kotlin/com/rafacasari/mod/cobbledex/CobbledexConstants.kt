package com.rafacasari.mod.cobbledex

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.rafacasari.mod.cobbledex.api.classes.DiscoveryRegister
import net.minecraft.item.Item
import net.minecraft.util.Rarity
import com.rafacasari.mod.cobbledex.items.CobbledexItem

object CobbledexConstants {
    object Client {
        var discoveredList: MutableMap<String, MutableMap<String, DiscoveryRegister>> = mutableMapOf()

        val totalPokemonDiscovered: Int
            get() = discoveredList.size


        val totalPokemonCaught: Int
            get() = discoveredList.filter {
                    it.value.any { form -> form.value.status == DiscoveryRegister.RegisterType.CAUGHT}
                }.size

        val totalPokemonCount: Int
            get() = PokemonSpecies.implemented.size
    }

    val COBBLEDEX_ITEM = CobbledexItem(Item.Settings().maxCount(1).rarity(Rarity.COMMON))
}