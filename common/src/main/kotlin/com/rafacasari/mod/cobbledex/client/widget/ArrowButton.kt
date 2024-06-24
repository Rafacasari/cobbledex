package com.rafacasari.mod.cobbledex.client.widget

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.text.text
import com.rafacasari.mod.cobbledex.utils.cobbledexResource
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.sound.SoundManager

// TODO: We can make this implement ImageButton
class ArrowButton(
    private val isLeft: Boolean,
    pX: Int, pY: Int,
    onPress: PressAction
) : ButtonWidget(pX, pY, 4, 7, "Arrow".text(), onPress, DEFAULT_NARRATION_SUPPLIER) {

    companion object {
        val LEFT_TEXTURE = cobbledexResource("textures/gui/cobbledex_left_arrow.png")
        val RIGHT_TEXTURE = cobbledexResource("textures/gui/cobbledex_right_arrow.png")
    }

    override fun renderButton(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        if (context == null) return

        val matrices = context.matrices


        blitk(
            matrixStack = matrices,
            texture = if(isLeft) LEFT_TEXTURE else RIGHT_TEXTURE,
            x = x, y = y,
            width = width, height = height, alpha = if (isHovered) 1 else 0.75f
        )
    }

    override fun playDownSound(soundManager: SoundManager?) {

    }
}