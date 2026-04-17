package com.rafacasari.mod.cobbledex.client.widget

import com.cobblemon.mod.common.api.gui.blitk
import com.rafacasari.mod.cobbledex.utils.MiscUtils.cobbledexResource
import com.rafacasari.mod.cobbledex.utils.MiscUtils.cobbledexTextTranslation
import net.minecraft.client.Minecraft as MinecraftClient
import net.minecraft.client.gui.GuiGraphics as DrawContext
import net.minecraft.client.gui.components.EditBox as TextFieldWidget
import net.minecraft.network.chat.Component as Text

class SearchWidget(posX: Number, posY: Number, callback: (() -> Unit)?) : TextFieldWidget(
    MinecraftClient.getInstance().font,
    posX.toInt() + HORIZONTAL_PADDING,
    posY.toInt() + VERTICAL_PADDING,
    TEXT_FIELD_WIDTH,
    TEXT_FIELD_HEIGHT,
    Text.literal("Search Widget")
) {

    companion object {
        private const val BACKGROUND_WIDTH = 76
        private const val BACKGROUND_HEIGHT = 16
        private const val HORIZONTAL_PADDING = 5
        private const val VERTICAL_PADDING = 4
        private const val TEXT_FIELD_WIDTH = BACKGROUND_WIDTH - (HORIZONTAL_PADDING * 2)
        private const val TEXT_FIELD_HEIGHT = 10
        private val SEARCH_BAR = cobbledexResource("textures/gui/collection/search_bar.png")
    }

    private val backgroundX = posX.toInt()
    private val backgroundY = posY.toInt()

    init {
        setMaxLength(16)
        setBordered(false)
        setHint(cobbledexTextTranslation("search"))
        setResponder {
            callback?.invoke()
        }
    }

    override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        blitk(
            matrixStack = context.pose(),
            texture = SEARCH_BAR,
            x = backgroundX,
            y = backgroundY,
            width = BACKGROUND_WIDTH,
            height = BACKGROUND_HEIGHT
        )

        if (cursorPosition != value.length) {
            moveCursorToEnd(false)
        }

        super.renderWidget(context, mouseX, mouseY, delta)
    }

    override fun isMouseOver(mouseX: Double, mouseY: Double): Boolean {
        return active && visible &&
            mouseX >= backgroundX && mouseX < backgroundX + BACKGROUND_WIDTH &&
            mouseY >= backgroundY && mouseY < backgroundY + BACKGROUND_HEIGHT
    }
}
