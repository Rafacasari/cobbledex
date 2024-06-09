package com.rafacasari.mod.cobbledex.utils

import com.mojang.datafixers.util.Either
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import com.rafacasari.mod.cobbledex.Cobbledex

fun cobbledexResource(path: String) = Identifier(Cobbledex.MOD_ID, path)
fun cobbledexTranslation(key: String) = Text.translatable(key)
fun logInfo(text: String) = Cobbledex.LOGGER.info(text)
fun logWarn(text: String) = Cobbledex.LOGGER.warn(text)
fun logError(text: String) = Cobbledex.LOGGER.error(text)
fun logDebug(text: String) = Cobbledex.LOGGER.debug(text)

fun <L, R> Either<L, R>.fold(ifLeft: (L) -> String, ifRight: (R) -> String): String {
    return if (this.left().isPresent) {
        ifLeft(this.left().get())
    } else {
        ifRight(this.right().get())
    }
}