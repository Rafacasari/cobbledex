package com.rafacasari.mod.cobbledex.client.widget

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.text.text
import com.rafacasari.mod.cobbledex.utils.MiscUtils.cobbledexResource
import net.minecraft.client.gui.GuiGraphics as DrawContext
import net.minecraft.client.gui.narration.NarrationElementOutput as NarrationMessageBuilder
import net.minecraft.client.gui.components.Button as ButtonWidget
import net.minecraft.client.sounds.SoundManager

// TODO: We can make this implement ImageButton
class ArrowButton(
    private val isLeft: Boolean,
    pX: Int, pY: Int,
    onPress: OnPress
) : ButtonWidget(pX, pY, 4, 7, "Arrow".text(), onPress, DEFAULT_NARRATION) {

    companion object {
        val LEFT_TEXTURE = cobbledexResource("textures/gui/cobbledex_left_arrow.png")
        val RIGHT_TEXTURE = cobbledexResource("textures/gui/cobbledex_right_arrow.png")
    }

    override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val matrices = context.pose()

        blitk(
            matrixStack = matrices,
            texture = if(isLeft) LEFT_TEXTURE else RIGHT_TEXTURE,
            x = x, y = y,
            width = width, height = height, alpha = if (isHovered) 1 else 0.75f
        )
    }

    override fun updateWidgetNarration(builder: NarrationMessageBuilder) {
        defaultButtonNarrationText(builder)
    }

    override fun playDownSound(soundManager: SoundManager) {

    }
}