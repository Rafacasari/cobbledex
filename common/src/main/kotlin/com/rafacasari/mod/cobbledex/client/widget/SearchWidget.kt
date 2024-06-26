package com.rafacasari.mod.cobbledex.client.widget

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.client.CobblemonResources
import com.cobblemon.mod.common.client.render.drawScaledText
import com.rafacasari.mod.cobbledex.utils.cobbledexResource
import com.rafacasari.mod.cobbledex.utils.cobbledexTextTranslation
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.text.Text

class SearchWidget(val posX: Number, val posY: Number, val callback: (() -> Unit)?): TextFieldWidget(MinecraftClient.getInstance().textRenderer,
    posX.toInt(), posY.toInt(), 76, 16, Text.of("Search Widget")) {

    companion object {
        private val SEARCH_BAR = cobbledexResource("textures/gui/collection/search_bar.png")
    }

    init {
        this.setMaxLength(16)
        this.setChangedListener {
            callback?.invoke()
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        return if (mouseX.toInt() in x..(x + width) && mouseY.toInt() in y..(y + height)) {
            isFocused = true
            true
        } else {
            false
        }
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val matrices = context.matrices

        blitk(
            matrixStack = matrices,
            texture = SEARCH_BAR,
            x = posX, y = posY,
            width = this.width,
            height = this.height
        )

        if (cursor != text.length) setCursorToEnd()

        val input = if (!isFocused && text.isEmpty()) cobbledexTextTranslation("search") else text.text()

        drawScaledText(
            context = context,
            font = CobblemonResources.DEFAULT_LARGE,
            text = input,
            x = posX.toFloat() + 11.5f,
            y = posY.toFloat() + 3.5f,
            shadow = false,
            opacity = if (!isFocused && text.isEmpty()) 0.15f else 1f
        )
    }
}
