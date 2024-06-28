package com.rafacasari.mod.cobbledex.utils

import com.cobblemon.mod.common.api.text.add
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import com.rafacasari.mod.cobbledex.Cobbledex
import net.minecraft.text.MutableText
import net.minecraft.text.TextContent

fun cobbledexResource(path: String) = Identifier(Cobbledex.MOD_ID, path)
fun cobbledexTranslation(key: String) = Text.translatable(key)
fun logInfo(text: String) = Cobbledex.LOGGER.info(text)
fun logWarn(text: String) = Cobbledex.LOGGER.warn(text)
fun logError(text: String) = Cobbledex.LOGGER.error(text)
fun logDebug(text: String) = Cobbledex.LOGGER.debug(text)

fun MutableText.withRGBColor(color: Int) = also { it.style = it.style.withColor(color) }
fun Text.bold() = also { (it as MutableText).style = it.style.withBold(true) }

fun Float.format(): String = if (this % 1 == 0f) this.toInt().toString() else this.toString()

fun cobbledexTextTranslation(key: String, vararg arg: Any): MutableText {
    return Text.translatable("cobbledex.texts.$key", *arg)
}

fun MutableList<MutableText>.toMutableText(): MutableText = MutableText.of(TextContent.EMPTY).also {
    this.forEachIndexed { index, x ->
        if (index + 1 < this.size)
            x.add("\n")
        it.append(x)
    }
}

fun MutableList<MutableText>.addEmptyLine() = this.add(Text.empty())
fun MutableList<Text>.emptyLine() = this.add(Text.empty())