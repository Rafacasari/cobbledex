package com.rafacasari.mod.cobbledex.utils

import net.minecraft.text.Text
import net.minecraft.util.Identifier
import com.rafacasari.mod.cobbledex.Cobbledex
import net.minecraft.text.MutableText

fun cobbledexResource(path: String) = Identifier(Cobbledex.MOD_ID, path)
fun cobbledexTranslation(key: String) = Text.translatable(key)
fun logInfo(text: String) = Cobbledex.LOGGER.info(text)
fun logWarn(text: String) = Cobbledex.LOGGER.warn(text)
fun logError(text: String) = Cobbledex.LOGGER.error(text)
fun logDebug(text: String) = Cobbledex.LOGGER.debug(text)

fun MutableText.withRGBColor(color: Int) = also { it.style = it.style.withColor(color) }

fun Float.format(): String = if (this % 1 == 0f) this.toInt().toString() else this.toString()
