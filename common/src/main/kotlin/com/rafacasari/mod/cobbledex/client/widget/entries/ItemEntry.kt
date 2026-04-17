package com.rafacasari.mod.cobbledex.client.widget.entries

import com.rafacasari.mod.cobbledex.client.widget.LongTextDisplay
import net.minecraft.client.Minecraft as MinecraftClient
import net.minecraft.client.gui.GuiGraphics as DrawContext
import net.minecraft.util.FormattedCharSequence as OrderedText
import net.minecraft.world.item.ItemStack

class ItemEntry(val item: ItemStack, val text: OrderedText, val disableTooltip: Boolean = false) : LongTextDisplay.TextDisplayEntry() {
    companion object {
        const val ITEM_SCALE = 0.5F
        const val ITEM_RENDER_SIZE = 8
        const val ITEM_OFFSET_Y = 1
        const val TEXT_OFFSET_X = 11
    }

    private fun drawItemName(context: DrawContext, text: OrderedText, x: Int, y: Int, mouseX: Int?, mouseY: Int?): Boolean {
        val textRenderer = MinecraftClient.getInstance().font
        val width = textRenderer.width(text)

        context.drawString(textRenderer, text, x, y, 0xFFFFFF, false)

        return mouseX != null && mouseY != null &&
            mouseX >= x && mouseX <= x + width &&
            mouseY >= y && mouseY <= y + textRenderer.lineHeight
    }

    private fun drawItem(context: DrawContext, stack: ItemStack, x: Int, y: Int) {
        if (!stack.isEmpty) {
            val matrices = context.pose()
            matrices.pushPose()
            matrices.translate(x.toDouble(), (y + ITEM_OFFSET_Y).toDouble(), 0.0)
            matrices.scale(ITEM_SCALE, ITEM_SCALE, 1F)
            context.renderItem(stack, 0, 0)
            matrices.popPose()
        }
    }

    var isItemHovered = false
    var isNameHovered = false

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
        drawItem(context, item, x, y)
        isNameHovered = drawItemName(context, text, x + TEXT_OFFSET_X, y, mouseX, mouseY)
        isItemHovered = mouseX in x..<(x + ITEM_RENDER_SIZE) &&
            mouseY in (y + ITEM_OFFSET_Y)..<(y + ITEM_OFFSET_Y + ITEM_RENDER_SIZE)
    }

    override fun drawTooltip(context: DrawContext, mouseX: Int, mouseY: Int) {
        if (!disableTooltip) {
            context.renderTooltip(MinecraftClient.getInstance().font, item, mouseX, mouseY)
        }
    }

    override fun isMouseOver(mouseX: Double, mouseY: Double): Boolean = isItemHovered || isNameHovered
}
