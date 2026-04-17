package com.rafacasari.mod.cobbledex.client.widget

import net.minecraft.client.Minecraft as MinecraftClient
import net.minecraft.client.gui.GuiGraphics as DrawContext
import net.minecraft.client.gui.components.ContainerObjectSelectionList as AlwaysSelectedEntryListWidget

abstract class CobbledexScrollList<T : AlwaysSelectedEntryListWidget.Entry<T>>(
    private val listX: Int,
    private val listY: Int,
    slotHeight: Int
) : AlwaysSelectedEntryListWidget<T>(
    MinecraftClient.getInstance(),
    WIDTH,
    HEIGHT,
    listY + 1,
    slotHeight
) {
    companion object {
        const val WIDTH = 85
        const val HEIGHT = 128
        const val SLOT_WIDTH = 80
    }

    init {
        correctSize()
    }

    override fun getRowWidth(): Int {
        return SLOT_WIDTH
    }

    override fun getScrollbarPosition(): Int {
        return listX + width - 1
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
            listX,
            listY + 2,
            listX + width,
            listY + 2 + height
        )
        super.renderWidget(context, mouseX, mouseY, partialTicks)
        context.disableScissor()
    }

    private fun correctSize() {
        x = listX
        y = listY + 1
        setRectangle(WIDTH, HEIGHT, listX, listY + 1)
        clampScrollAmount()
    }
}
