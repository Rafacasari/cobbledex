package com.rafacasari.mod.cobbledex.client.widget

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.client.CobblemonResources
import com.cobblemon.mod.common.client.render.drawScaledText
import com.rafacasari.mod.cobbledex.utils.MiscUtils.cobbledexResource
import net.minecraft.client.gui.GuiGraphics as DrawContext
import net.minecraft.client.gui.narration.NarrationElementOutput as NarrationMessageBuilder
import net.minecraft.client.gui.components.Button as ButtonWidget
import net.minecraft.client.sounds.SoundManager
import net.minecraft.network.chat.MutableComponent as MutableText

class CobbledexTab(
    private val xOffset: Int, private val yOffset: Int,
    pX: Int, pY: Int,
    private val label: MutableText,
    onPress: ButtonWidget.OnPress
): ButtonWidget(pX, pY, BUTTON_WIDTH, BUTTON_HEIGHT, label, onPress, DEFAULT_NARRATION) {

    companion object {
        const val TAB_WIDTH = 110
        const val TAB_HEIGHT = 11
        const val BUTTON_WIDTH = 36
        const val BUTTON_HEIGHT = 11
    }

    private var isActive = false

    override fun renderWidget(context: DrawContext, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        val matrices = context.pose()

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
            shadow = true,
            maxCharacterWidth = BUTTON_WIDTH
        )
    }

    override fun updateWidgetNarration(builder: NarrationMessageBuilder) {
        defaultButtonNarrationText(builder)
    }

    override fun playDownSound(soundManager: SoundManager) {
        // Ignore original sound
    }

    fun setActive(state: Boolean = true) {
        isActive = state
    }
}
