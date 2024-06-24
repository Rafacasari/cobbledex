package com.rafacasari.mod.cobbledex.client.widget

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.text.text
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.sound.SoundManager
import net.minecraft.util.Identifier

class ImageButton(
    private val texture: Identifier,
    width: Int, height: Int,
    pX: Int, pY: Int,
    onPress: PressAction
) : ButtonWidget(pX, pY, width, height, "ImageButton".text(), onPress, DEFAULT_NARRATION_SUPPLIER) {

    
    override fun renderButton(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        if (context == null) return

        val matrices = context.matrices

        blitk(
            matrixStack = matrices,
            texture = texture,
            x = x, y = y,
            width = width, height = height,
            alpha = if (isHovered) 1 else 0.75f
        )
    }

    override fun playDownSound(soundManager: SoundManager) {
        soundManager.play(PositionedSoundInstance.master(CobblemonSounds.GUI_CLICK, 1f))
    }
}