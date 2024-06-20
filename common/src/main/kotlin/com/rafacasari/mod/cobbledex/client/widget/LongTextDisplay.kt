package com.rafacasari.mod.cobbledex.client.widget

import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.pokemon.Species
import com.rafacasari.mod.cobbledex.client.widget.entries.EmptyEntry
import com.rafacasari.mod.cobbledex.client.widget.entries.ItemEntry
import com.rafacasari.mod.cobbledex.client.widget.entries.PokemonEntry
import com.rafacasari.mod.cobbledex.client.widget.entries.TextEntry
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
import net.minecraft.item.ItemStack
import net.minecraft.text.MutableText
import net.minecraft.text.OrderedText
import net.minecraft.text.Text
import net.minecraft.util.Language

class LongTextDisplay (
    private val x: Int = 0,
    private val y: Int = 0,
    private val frameWidth: Int,
    private val frameHeight: Int,
    private val padding : Int = 3,
    private val scrollbarSize: Int = 2
): AlwaysSelectedEntryListWidget<LongTextDisplay.TextDisplayEntry>(
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

    private fun addText(entry: TextEntry): Int {
        return super.addEntry(entry)
    }

    fun addText(entry: MutableText, breakLine: Boolean = true, shadow: Boolean = false) {

        if (breakLine && super.getEntryCount() > 0) {
            addEmptyEntry()
        }

        val textRenderer = MinecraftClient.getInstance().textRenderer
        Language.getInstance().reorder(textRenderer.textHandler.wrapLines(entry, rowWidth - SCROLLBAR_PADDING, entry.style)).forEach {
            addText(TextEntry(it, shadow))
        }
    }

    fun addItemEntry(item: ItemStack, entry: Text, breakLine: Boolean = true) {

        if (breakLine && super.getEntryCount() > 0)
            addEmptyEntry()

        val textRenderer = MinecraftClient.getInstance().textRenderer
        val reorderedTexts = Language.getInstance()
            .reorder(textRenderer.textHandler.wrapLines(entry, rowWidth - SCROLLBAR_PADDING - 11, entry.style))

        reorderedTexts.forEach {
            if (reorderedTexts.first() == it)
                addItemEntryInternal(item, it)
            else addText(TextEntry(it, false))
        }
    }

    private fun addItemEntryInternal(item: ItemStack, text: OrderedText) : Int
    {
        return super.addEntry(ItemEntry(item, text))
    }

    fun addPokemon(pokemon: Species, aspects: Set<String>, translatedName: MutableText, breakLine: Boolean = false) {

        if (breakLine && super.getEntryCount() > 0)
            addEmptyEntry()

        super.addEntry(PokemonEntry(pokemon, aspects, translatedName.asOrderedText()))
        super.addEntry(EmptyEntry())

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

        val currentEntry = hoveredEntry
        if (currentEntry != null && currentEntry.isMouseOver(mouseX.toDouble(), mouseY.toDouble())) {

            currentEntry.drawTooltip(context, mouseX, mouseY)
        }

        if (hoveredEntry != null && hoveredEntry is TextEntry) {
            val hoveredTextEntry = hoveredEntry as TextEntry
            if (hoveredTextEntry.pendingHover != null) {
                val textRenderer = MinecraftClient.getInstance().textRenderer
                context.drawHoverEvent(textRenderer, hoveredTextEntry.pendingHover, mouseX, mouseY)
            }
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

    fun resetScrollPosition() {
        scrollAmount = 0.0
    }

    fun addEmptyEntry() {
        super.addEntry(EmptyEntry())
    }

    abstract class TextDisplayEntry : Entry<TextDisplayEntry>() {
        override fun getNarration(): Text = "".text()
        abstract fun drawTooltip(context: DrawContext, mouseX: Int, mouseY: Int)
    }
}