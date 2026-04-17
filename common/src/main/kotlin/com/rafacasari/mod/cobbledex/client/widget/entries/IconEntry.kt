package com.rafacasari.mod.cobbledex.client.widget.entries

import com.cobblemon.mod.common.api.gui.blitk
import com.rafacasari.mod.cobbledex.client.widget.LongTextDisplay
import net.minecraft.client.Minecraft as MinecraftClient
import net.minecraft.client.gui.GuiGraphics as DrawContext
import net.minecraft.resources.ResourceLocation as Identifier
import net.minecraft.util.FormattedCharSequence as OrderedText

class IconEntry(
    val icon: Identifier,
    val text: OrderedText,
    val xOffset: Number,
    val yOffset: Number,
    val iconOriginalWidth: Int,
    val iconOriginalHeight: Int,
    val iconScale: Float = 1f
) : LongTextDisplay.TextDisplayEntry() {
    private fun drawItemName(context: DrawContext, text: OrderedText, x: Int, y: Int, mouseX: Int?, mouseY: Int?): Boolean {
        val textRenderer = MinecraftClient.getInstance().font
        val width = textRenderer.width(text)

        context.drawString(textRenderer, text, x, y, 0xFFFFFF, false)

        return mouseX != null && mouseY != null &&
            mouseX >= x && mouseX <= x + width &&
            mouseY >= y && mouseY <= y + textRenderer.lineHeight
    }

    private fun drawItem(context: DrawContext, x: Int, y: Int) {
        blitk(
            matrixStack = context.pose(),
            texture = icon,
            x = x / iconScale + xOffset.toFloat(),
            y = y / iconScale + yOffset.toFloat(),
            height = iconOriginalHeight,
            width = iconOriginalWidth,
            scale = iconScale
        )
    }

    override fun render(
        context: DrawContext,
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
        drawItem(context, x, y)
        drawItemName(context, text, (x + 10.5f).toInt(), y, mouseX, mouseY)
    }

    override fun drawTooltip(context: DrawContext, mouseX: Int, mouseY: Int) = Unit
}
