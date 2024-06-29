package com.rafacasari.mod.cobbledex

import com.rafacasari.mod.cobbledex.api.classes.DiscoveryRegister
import net.minecraft.item.Item
import net.minecraft.util.Rarity
import com.rafacasari.mod.cobbledex.items.CobbledexItem

object CobbledexConstants {
    object Client {
        var discoveredList: MutableMap<String, MutableMap<String, DiscoveryRegister>> = mutableMapOf()
    }

    val COBBLEDEX_ITEM = CobbledexItem(Item.Settings().maxCount(1).rarity(Rarity.COMMON))
}