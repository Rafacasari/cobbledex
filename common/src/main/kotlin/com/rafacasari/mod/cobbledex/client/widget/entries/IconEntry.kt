package com.rafacasari.mod.cobbledex.client.widget.entries

import com.cobblemon.mod.common.api.gui.blitk
import com.rafacasari.mod.cobbledex.client.widget.LongTextDisplay
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.OrderedText
import net.minecraft.util.Colors
import net.minecraft.util.Identifier

class IconEntry(val icon: Identifier, val text: OrderedText, val xOffset: Number, val yOffset: Number, val iconOriginalWidth: Int, val iconOriginalHeight: Int, val iconScale: Float = 1f) : LongTextDisplay.TextDisplayEntry() {
    private fun drawItemName(context: DrawContext, text: OrderedText, x: Number, y: Number, pMouseX: Int? = null, pMouseY: Int? = null): Boolean {
        val textRenderer = MinecraftClient.getInstance().textRenderer
        val width = textRenderer.getWidth(text)

        context.drawText(textRenderer, text, x.toInt(), y.toInt(), Colors.WHITE, false)

        // Return isHovered
        return pMouseY != null && pMouseX != null &&
                pMouseX.toInt() >= x.toInt() && pMouseX.toInt() <= x.toInt() + width &&
                pMouseY.toInt() >= y.toInt() && pMouseY.toInt() <= y.toInt() + textRenderer.fontHeight
    }

    private fun drawItem(context: DrawContext, x: Int, y: Int) {
        blitk(
            matrixStack = context.matrices,
            texture = icon,
            x = x / iconScale + xOffset.toFloat(),
            y = y / iconScale + yOffset.toFloat(),
            height = iconOriginalHeight,
            width = iconOriginalWidth,
            scale = iconScale
        )
    }


    override fun render(
        context: DrawContext?,
        index: Int,
        y: Int,
        x: Int,
        entryWidth: Int,
        entryHeight: Int,
        mouseX: Int,
        mouseY: Int,
        hovered: Boolean,
        tickDelta: Float
    ) {
        if (context == null) return

        drawItem(context, x, y)
        drawItemName(context, text, x + 10.5f, y, mouseX, mouseY)
    }

    override fun drawTooltip(context: DrawContext, mouseX: Int, mouseY: Int) {
    }
}