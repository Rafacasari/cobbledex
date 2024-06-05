package rafacasari.cobbledex

import net.minecraft.item.Item
import net.minecraft.util.Rarity
import rafacasari.cobbledex.items.CobbledexItem

object CobbledexConstants {

    @Suppress("SameParameterValue")
    private fun buildErrorMessage(name: String): String {
        return "${Cobbledex.MOD_ID}.errors.$name"
    }

    val NotAPokemon by lazy { buildErrorMessage("NotAPokemon") }

    val Cobbledex_Item = CobbledexItem(Item.Settings().maxCount(1).rarity(Rarity.COMMON))
}