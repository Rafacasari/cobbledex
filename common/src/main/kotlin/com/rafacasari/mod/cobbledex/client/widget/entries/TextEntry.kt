package com.rafacasari.mod.cobbledex.client.widget.entries

import com.rafacasari.mod.cobbledex.client.widget.LongTextDisplay
import net.minecraft.client.Minecraft as MinecraftClient
import net.minecraft.client.gui.GuiGraphics as DrawContext
import net.minecraft.network.chat.Style
import net.minecraft.util.FormattedCharSequence as OrderedText

class TextEntry(private val line: OrderedText?, private val shadow: Boolean = false) : LongTextDisplay.TextDisplayEntry() {
    private fun drawText(
        context: DrawContext,
        text: OrderedText,
        x: Int,
        y: Int,
        colour: Int,
        shadow: Boolean,
        mouseX: Int?,
        mouseY: Int?
    ): Boolean {
        val textRenderer = MinecraftClient.getInstance().font
        val width = textRenderer.width(text)

        context.drawString(textRenderer, text, x, y, colour, shadow)

        return mouseX != null && mouseY != null &&
            mouseX >= x && mouseX <= x + width &&
            mouseY >= y && mouseY <= y + textRenderer.lineHeight
    }

    private fun drawOrderedText(
        context: DrawContext,
        text: OrderedText,
        x: Int,
        y: Int,
        colour: Int = 0x00FFFFFF,
        shadow: Boolean = false,
        mouseX: Int?,
        mouseY: Int?
    ) {
        val isHovered = drawText(context, text, x, y, colour, shadow, mouseX, mouseY)
        if (isHovered && mouseX != null) {
            val textRenderer = MinecraftClient.getInstance().font
            newHover = textRenderer.splitter.componentStyleAtWidth(text, mouseX - x)
        }
    }

    private var newHover: Style? = null
    var pendingHover: Style? = null

    override fun drawTooltip(context: DrawContext, mouseX: Int, mouseY: Int) = Unit

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
            drawOrderedText(context, it, rowLeft, rowTop, mouseX = mouseX, mouseY = mouseY, shadow = shadow)
        }
        pendingHover = newHover
    }
}
