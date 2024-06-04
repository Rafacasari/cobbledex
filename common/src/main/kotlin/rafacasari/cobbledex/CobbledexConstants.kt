package rafacasari.cobbledex

import net.minecraft.item.Item
import net.minecraft.util.Rarity
import rafacasari.cobbledex.items.CobbledexItem

object CobbledexConstants {

    private fun buildErrorMessage(name: String): String {
        return "${CobbledexMod.MOD_ID}.errors.$name"
    }

    val NotAPokemon by lazy { buildErrorMessage("NotAPokemon") }

    val Cobbledex_Item = CobbledexItem(Item.Settings().maxCount(1).rarity(Rarity.COMMON))
}