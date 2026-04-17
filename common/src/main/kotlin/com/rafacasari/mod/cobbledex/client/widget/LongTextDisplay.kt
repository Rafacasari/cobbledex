package com.rafacasari.mod.cobbledex.client.widget

import com.cobblemon.mod.common.pokemon.Species
import com.rafacasari.mod.cobbledex.client.widget.entries.EmptyEntry
import com.rafacasari.mod.cobbledex.client.widget.entries.IconEntry
import com.rafacasari.mod.cobbledex.client.widget.entries.ItemEntry
import com.rafacasari.mod.cobbledex.client.widget.entries.PokemonEntry
import com.rafacasari.mod.cobbledex.client.widget.entries.TextEntry
import net.minecraft.client.Minecraft as MinecraftClient
import net.minecraft.client.gui.GuiGraphics as DrawContext
import net.minecraft.client.gui.components.ContainerObjectSelectionList as AlwaysSelectedEntryListWidget
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.narration.NarratableEntry
import net.minecraft.world.item.ItemStack
import net.minecraft.network.chat.MutableComponent as MutableText
import net.minecraft.network.chat.Component as Text
import net.minecraft.resources.ResourceLocation as Identifier

class LongTextDisplay(
    private val frameX: Int = 0,
    private val frameY: Int = 0,
    private val frameWidth: Int,
    private val frameHeight: Int,
    private val padding: Int = 3,
    private val scrollbarSize: Int = 2
) : AlwaysSelectedEntryListWidget<LongTextDisplay.TextDisplayEntry>(
    MinecraftClient.getInstance(),
    frameWidth,
    frameHeight,
    frameY,
    LINE_HEIGHT
) {

    companion object {
        const val LINE_HEIGHT = 10
        const val SCROLLBAR_PADDING = 8
    }

    init {
        correctSize()
    }

    private fun correctSize() {
        x = frameX
        y = frameY
        setRectangle(frameWidth, frameHeight, frameX, frameY)
        clampScrollAmount()
    }

    private fun addText(entry: TextEntry): Int {
        return super.addEntry(entry)
    }

    fun addText(entry: MutableText, breakLine: Boolean = true, shadow: Boolean = false) {
        if (breakLine && super.itemCount > 0) {
            addEmptyEntry()
        }

        val textRenderer = MinecraftClient.getInstance().font
        textRenderer.split(entry, rowWidth - SCROLLBAR_PADDING).forEach {
            addText(TextEntry(it, shadow))
        }
    }

    fun addItemEntry(item: ItemStack, entry: Text, breakLine: Boolean = true, disableTooltip: Boolean = false) {
        if (breakLine && super.itemCount > 0) {
            addEmptyEntry()
        }

        val textRenderer = MinecraftClient.getInstance().font
        val reorderedTexts = textRenderer.split(entry, rowWidth - SCROLLBAR_PADDING - ItemEntry.TEXT_OFFSET_X)

        reorderedTexts.forEach {
            if (reorderedTexts.first() == it) {
                super.addEntry(ItemEntry(item, it, disableTooltip))
            } else {
                addText(TextEntry(it, false))
            }
        }
    }

    fun addIcon(
        icon: Identifier,
        entry: Text,
        width: Int,
        height: Int,
        xOffset: Number = 0,
        yOffset: Number = 0,
        scale: Float = 1f,
        breakLine: Boolean = true
    ) {
        if (breakLine && super.itemCount > 0) {
            addEmptyEntry()
        }

        val textRenderer = MinecraftClient.getInstance().font
        val reorderedTexts = textRenderer.split(entry, rowWidth - SCROLLBAR_PADDING - 11)

        reorderedTexts.forEach {
            if (reorderedTexts.first() == it) {
                super.addEntry(IconEntry(icon, it, xOffset, yOffset, width, height, scale))
            } else {
                addText(TextEntry(it, false))
            }
        }
    }

    fun addPokemon(pokemon: Species, aspects: Set<String>, translatedName: MutableText, breakLine: Boolean = false) {
        if (breakLine && super.itemCount > 0) {
            addEmptyEntry()
        }

        super.addEntry(PokemonEntry(pokemon, aspects, translatedName.visualOrderText))
        super.addEntry(EmptyEntry())
    }

    fun clear() {
        super.clearEntries()
    }

    override fun getRowWidth(): Int {
        return frameWidth - (padding * 2)
    }

    override fun getRowLeft(): Int {
        return frameX + padding
    }

    override fun getScrollbarPosition(): Int {
        return frameX + frameWidth - padding - scrollbarSize
    }

    override fun renderListBackground(context: DrawContext) {}

    override fun renderSelection(
        context: DrawContext,
        top: Int,
        width: Int,
        height: Int,
        outerColor: Int,
        innerColor: Int
    ) = Unit

    override fun renderListSeparators(context: DrawContext) = Unit

    override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, partialTicks: Float) {
        correctSize()

        context.enableScissor(
            frameX,
            frameY,
            frameX + frameWidth,
            frameY + frameHeight
        )
        super.renderWidget(context, mouseX, mouseY, partialTicks)
        context.disableScissor()

        val currentEntry = hovered
        if (currentEntry != null && currentEntry.isMouseOver(mouseX.toDouble(), mouseY.toDouble())) {
            currentEntry.drawTooltip(context, mouseX, mouseY)
        }

        if (currentEntry is TextEntry && currentEntry.pendingHover != null) {
            val textRenderer = MinecraftClient.getInstance().font
            context.renderComponentHoverEffect(textRenderer, currentEntry.pendingHover, mouseX, mouseY)
        }
    }

    fun resetScrollPosition() {
        setScrollAmount(0.0)
    }

    fun addEmptyEntry() {
        super.addEntry(EmptyEntry())
    }

    abstract class TextDisplayEntry : Entry<TextDisplayEntry>() {
        override fun children(): List<GuiEventListener> = emptyList()
        override fun narratables(): List<NarratableEntry> = emptyList()
        abstract fun drawTooltip(context: DrawContext, mouseX: Int, mouseY: Int)
    }
}