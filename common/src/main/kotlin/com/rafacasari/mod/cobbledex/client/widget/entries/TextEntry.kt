package com.rafacasari.mod.cobbledex.client.widget.entries

import com.rafacasari.mod.cobbledex.client.widget.LongTextDisplay
import com.rafacasari.mod.cobbledex.mixins.TextHandlerAccessor
import com.rafacasari.mod.cobbledex.utils.logError
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.OrderedText
import net.minecraft.text.Style

class TextEntry(private val line: OrderedText?, private val shadow: Boolean = false) : LongTextDisplay.TextDisplayEntry() {
    private fun drawText(
        context: DrawContext,
        text: OrderedText,
        x: Number,
        y: Number,
        colour: Int,
        shadow: Boolean = true,
        pMouseX: Int? = null,
        pMouseY: Int? = null
    ): Boolean {
        val textRenderer = MinecraftClient.getInstance().textRenderer
        val width = textRenderer.getWidth(text)

        context.drawText(textRenderer, text, x.toInt(), y.toInt(), colour, shadow)

        // Return isHovered
        return pMouseY != null && pMouseX != null &&
                pMouseX.toInt() >= x.toInt() && pMouseX.toInt() <= x.toInt() + width &&
                pMouseY.toInt() >= y.toInt() && pMouseY.toInt() <= y.toInt() + textRenderer.fontHeight
    }


    private fun drawOrderedText(
        context: DrawContext,
        text: OrderedText,
        x: Number,
        y: Number,
        colour: Int = 0x00FFFFFF,
        shadow: Boolean = false,
        pMouseX: Int? = null,
        pMouseY: Int? = null
    ) {

        val isHovered = drawText(
            context = context,
            text = text,
            x = x.toFloat(), y = y.toFloat(),
            colour = colour,
            shadow = shadow,
            pMouseX = pMouseX, pMouseY = pMouseY
        )

        if (isHovered) {
            val textRenderer = MinecraftClient.getInstance().textRenderer
            var hoveredStyle: Style? = null
            var charX = x.toFloat()
            val charY = y.toFloat()

            text.accept { _, style, codePoint ->
                try {
                    val widthRetriever = (textRenderer.textHandler as TextHandlerAccessor).cobbledexWidthRetriever
                    val charWidth = widthRetriever.getWidth(codePoint, style)

                    if (pMouseX!! >= charX && pMouseX < charX + charWidth && pMouseY!! >= charY && pMouseY < charY + textRenderer.fontHeight) {
                        hoveredStyle = style
                    }

                    charX += charWidth
                } catch (e: Exception) {
                    logError(e.toString())
                }
                true
            }

            hoveredStyle?.let {
                newHover = it
                //context.drawHoverEvent(textRenderer, it, pMouseX!!, pMouseY!!)
            }
        }
    }

    private var newHover: Style? = null
    var pendingHover: Style? = null
    override fun drawTooltip(context: DrawContext, mouseX: Int, mouseY: Int) {

    }

    override fun render(
        context: DrawContext,
        index: Int,
        rowTop: Int,
        rowLeft: Int,
        rowWidth: Int,
        rowHeight: Int,
        mouseX: Int,
        mouseY: Int,
        isHovered: Boolean,
        partialTicks: Float
    ) {
        newHover = null

        line?.let {
            drawOrderedText(context, it, rowLeft, rowTop, pMouseX = mouseX, pMouseY = mouseY, shadow = shadow)
        }

        pendingHover = newHover
    }
}