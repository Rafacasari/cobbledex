package rafacasari.cobbledex.utils

import net.minecraft.text.Text
import net.minecraft.util.Identifier
import rafacasari.cobbledex.Cobbledex

fun cobbledexResource(path: String) = Identifier(Cobbledex.MOD_ID, path)
fun cobbledexTranslation(key: String) = Text.translatable(key)
fun logInfo(text: String) = Cobbledex.LOGGER.info(text)
fun logWarn(text: String) = Cobbledex.LOGGER.warn(text)
fun logError(text: String) = Cobbledex.LOGGER.error(text)