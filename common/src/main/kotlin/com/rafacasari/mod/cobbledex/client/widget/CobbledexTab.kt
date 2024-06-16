package com.rafacasari.mod.cobbledex.client.widget

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.client.CobblemonResources
import com.cobblemon.mod.common.client.render.drawScaledText
import com.rafacasari.mod.cobbledex.utils.cobbledexResource
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.sound.SoundManager
import net.minecraft.text.MutableText

class CobbledexTab(
    private val xOffset: Int, private val yOffset: Int,
    pX: Int, pY: Int,
    private val label: MutableText,
    onPress: PressAction
): ButtonWidget(pX, pY, BUTTON_WIDTH, BUTTON_HEIGHT, label, onPress, DEFAULT_NARRATION_SUPPLIER) {

    companion object {
        const val TAB_WIDTH = 110
        const val TAB_HEIGHT = 11
        const val BUTTON_WIDTH = 36
        const val BUTTON_HEIGHT = 11
    }

    private var isActive = false

    override fun renderButton(context: DrawContext, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        val matrices = context.matrices

        // Draw Highlight if is active
        if (isActive) {
            val widthLimit = x + width
            val heightLimit = y + height
            context.enableScissor(x, y, widthLimit, heightLimit)

            blitk(
                matrixStack = matrices,
                texture = cobbledexResource("textures/gui/cobbledex_highlighted_tab.png"),
                x = xOffset + 114, y = yOffset + 178,
                width = TAB_WIDTH, height = TAB_HEIGHT
            )

            context.disableScissor()
        }

        drawScaledText(
            context = context,
            font = CobblemonResources.DEFAULT_LARGE,
            text = label.bold(),
            x = x + (width / 2),
            y = y + 1,
            centered = true,
            shadow = true
        )
    }

    override fun playDownSound(soundManager: SoundManager) {
        // Ignore original sound
    }

    fun setActive(state: Boolean = true) {
        isActive = state
    }
}
