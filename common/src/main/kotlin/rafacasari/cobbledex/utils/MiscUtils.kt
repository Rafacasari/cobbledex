package rafacasari.cobbledex.utils

import net.minecraft.text.Text
import net.minecraft.util.Identifier
import rafacasari.cobbledex.CobbledexMod

fun cobbledexResource(path: String) = Identifier(CobbledexMod.MOD_ID, path)
fun cobbledexTranslation(key: String) = Text.translatable(key)