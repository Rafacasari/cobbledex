package com.rafacasari.mod.cobbledex.utils

import com.rafacasari.mod.cobbledex.Cobbledex
import net.minecraft.network.chat.Component as Text
import net.minecraft.network.chat.MutableComponent as MutableText
import net.minecraft.resources.ResourceLocation as Identifier
import net.minecraft.network.chat.ClickEvent

@Suppress("unused")
/**
 * A utility class with functions for Cobbledex or un-categorized functions
 */
object MiscUtils {
    /**
     * Returns an [Identifier] using **Cobbledex MOD ID** as namespace
     */
    fun cobbledexResource(path: String): Identifier = Identifier.fromNamespaceAndPath(Cobbledex.MOD_ID, path)
    fun translatable(key: String): MutableText = Text.translatable(key)

    fun logInfo(text: String) = Cobbledex.LOGGER.info(text)
    fun logWarn(text: String) = Cobbledex.LOGGER.warn(text)
    fun logError(text: String) = Cobbledex.LOGGER.error(text)
    fun logDebug(text: String) = Cobbledex.LOGGER.debug(text)

    fun MutableText.withRGBColor(color: Int): MutableText = setStyle(style.withColor(color))
    fun Text.bold(): MutableText = copy().withStyle { it.withBold(true) }

    fun Float.format(): String = if (this % 1 == 0f) this.toInt().toString() else this.toString()

    fun MutableText.openUrl(url: String): MutableText =
        setStyle(style.withClickEvent(ClickEvent(ClickEvent.Action.OPEN_URL, url)))

    fun Identifier.toTranslationKey(): String = "$namespace.${path.replace('/', '.')}"

    fun cobbledexTextTranslation(key: String, vararg arg: Any): MutableText {
        return Text.translatable("cobbledex.texts.$key", *arg)
    }

    fun MutableList<MutableText>.toMutableText(skipLine: Boolean = true): MutableText =
        Text.empty().also {
            this.forEachIndexed { index, x ->
                it.append(x)
                // Add new line
                if (skipLine && index + 1 < this.size) it.append("\n")
            }
        }

    fun MutableList<MutableText>.appendWithSeparator(separator: String): MutableText =
        Text.empty().also {
            this.forEachIndexed { index, x ->
                it.append(x)
                // Add new line
                if (index + 1 < this.size) it.append(separator)
            }
        }

    fun MutableList<MutableText>.addEmptyLine() = this.add(Text.empty())
    fun MutableList<Text>.emptyLine() = this.add(Text.empty())
}
