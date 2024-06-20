package com.rafacasari.mod.cobbledex.client.widget.entries


import com.rafacasari.mod.cobbledex.client.widget.LongTextDisplay
import net.minecraft.client.gui.DrawContext
class EmptyEntry : LongTextDisplay.TextDisplayEntry() {

    override fun drawTooltip(context: DrawContext, mouseX: Int, mouseY: Int) {

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
    )
    {

    }
}