package com.rafacasari.mod.cobbledex.client.widget

import com.cobblemon.mod.common.api.text.text
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
import net.minecraft.text.MutableText
import net.minecraft.text.OrderedText
import net.minecraft.text.Style
import net.minecraft.util.Language
import com.rafacasari.mod.cobbledex.mixins.TextHandlerAccessor
import com.rafacasari.mod.cobbledex.utils.logError


class LongTextDisplay (
    private val x: Int = 0,
    private val y: Int = 0,
    private val frameWidth: Int,
    private val frameHeight: Int,
    private val padding : Int = 3,
    private val scrollbarSize: Int = 2
): AlwaysSelectedEntryListWidget<LongTextDisplay.DialogueLine>(
    MinecraftClient.getInstance(),
    frameWidth,
    frameHeight, // height
    0, // top
    frameHeight, // bottom
    LINE_HEIGHT
) {

    private var scrolling = false

    init {
        correctSize()
        setRenderHorizontalShadows(false)
        setRenderBackground(false)
        setRenderSelection(false)
    }

    private fun correctSize() {
        updateSize(frameWidth, frameHeight, y, y + frameHeight)
        setLeftPos(x + padding)
    }

    companion object {
        const val LINE_HEIGHT = 10
        const val SCROLLBAR_PADDING = 8
//        const val LINE_WIDTH = 180
    }

    private fun add(entry: DialogueLine): Int {
        return super.addEntry(entry)
    }

    fun add(entry: MutableText, breakLine: Boolean = true, shadow: Boolean = false) {

        if (breakLine && super.getEntryCount() > 0) {
            super.addEntry(DialogueLine(null))
        }

        val textRenderer = MinecraftClient.getInstance().textRenderer
        Language.getInstance().reorder(textRenderer.textHandler.wrapLines(entry, rowWidth - SCROLLBAR_PADDING, entry.style)).forEach {
            add(DialogueLine(it, shadow))
        }
    }

    fun clear() {
        super.clearEntries()
    }

    override fun getRowWidth(): Int {
        return frameWidth - (padding * 2)
    }

    override fun getScrollbarPositionX(): Int {
        return left + width - padding - scrollbarSize
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, partialTicks: Float) {
        correctSize()

        // We need scissor to cut the rest of our scrollbar to make it look prettier ✨
        context.enableScissor(
            x,
            y,
            x + frameWidth,
            y + frameWidth
        )
        super.render(context, mouseX, mouseY, partialTicks)
        context.disableScissor()

        if (hoveredEntry?.pendingHover != null) {
            val textRenderer = MinecraftClient.getInstance().textRenderer
            context.drawHoverEvent(textRenderer, hoveredEntry?.pendingHover, mouseX, mouseY)
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        updateScrollState(mouseX, mouseY)
        if (scrolling) {
            focused = getEntryAtPosition(mouseX, mouseY)
            isDragging = true
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        if (scrolling) {
            if (mouseY < top) {
                scrollAmount = 0.0
            } else if (mouseY > bottom) {
                scrollAmount = maxScroll.toDouble()
            } else {
                scrollAmount += deltaY
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
    }

    private fun updateScrollState(mouseX: Double, mouseY: Double) {
        scrolling = mouseX >= (this.scrollbarPositionX - 3).toDouble()
                && mouseX < (this.scrollbarPositionX + 3).toDouble()
                && mouseY >= top
                && mouseY < bottom
    }

    class DialogueLine(private val line: OrderedText?, val shadow: Boolean = false) : Entry<DialogueLine>() {
        override fun getNarration() = "".text()

        private fun drawText(context: DrawContext, text: OrderedText, x: Number, y: Number, colour: Int, shadow: Boolean = true, pMouseX: Int? = null, pMouseY: Int? = null): Boolean {
            val textRenderer = MinecraftClient.getInstance().textRenderer
            val width = textRenderer.getWidth(text)

            context.drawText(textRenderer, text, x.toInt(), y.toInt(), colour, shadow)

            // Return isHovered
            return pMouseY != null && pMouseX != null &&
                    pMouseX.toInt() >= x.toInt() && pMouseX.toInt() <= x.toInt() + width &&
                    pMouseY.toInt() >= y.toInt() && pMouseY.toInt() <= y.toInt() + textRenderer.fontHeight
        }


        private fun drawOrderedText(context: DrawContext, text: OrderedText, x: Number, y: Number, colour: Int = 0x00FFFFFF, shadow: Boolean = false, pMouseX: Int? = null, pMouseY: Int? = null) {

            val isHovered = drawText(
                context = context,
                text = text,
                x = x.toFloat(), y = y.toFloat(),
                colour = colour,
                shadow = shadow,
                pMouseX = pMouseX, pMouseY = pMouseY)

            if (isHovered) {
                val textRenderer = MinecraftClient.getInstance().textRenderer
                var hoveredStyle: Style? = null
                var charX = x.toFloat()
                val charY = y.toFloat()

                text.accept { _, style, codePoint ->
                    try {
                        val widthRetriever = (textRenderer.textHandler as TextHandlerAccessor).widthRetriever
                        val charWidth = widthRetriever.getWidth(codePoint, style)

                        if (pMouseX!! >= charX && pMouseX < charX + charWidth && pMouseY!! >= charY && pMouseY < charY + textRenderer.fontHeight) {
                            hoveredStyle = style
                        }

                        charX += charWidth
                    }
                    catch (e: Exception)
                    {
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

        override fun render(context: DrawContext, index: Int, rowTop: Int, rowLeft: Int, rowWidth: Int, rowHeight: Int, mouseX: Int, mouseY: Int, isHovered: Boolean, partialTicks: Float) {
            newHover = null

            line?.let {
                drawOrderedText(context, it, rowLeft, rowTop, pMouseX = mouseX, pMouseY = mouseY, shadow = shadow)
            }

            pendingHover = newHover
        }
    }
}