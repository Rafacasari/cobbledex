package com.rafacasari.mod.cobbledex.client.widget

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.text.text
import net.minecraft.client.gui.GuiGraphics as DrawContext
import net.minecraft.client.gui.narration.NarrationElementOutput as NarrationMessageBuilder
import net.minecraft.client.gui.components.Button as ButtonWidget
import net.minecraft.client.resources.sounds.SimpleSoundInstance as PositionedSoundInstance
import net.minecraft.client.sounds.SoundManager
import net.minecraft.resources.ResourceLocation as Identifier

class ImageButton(
    private val texture: Identifier,
    width: Int, height: Int,
    pX: Int, pY: Int,
    onPress: OnPress
) : ButtonWidget(pX, pY, width, height, "ImageButton".text(), onPress, DEFAULT_NARRATION) {

    override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val matrices = context.pose()

        blitk(
            matrixStack = matrices,
            texture = texture,
            x = x, y = y,
            width = width, height = height,
            alpha = if (isHovered) 1 else 0.75f
        )
    }

    override fun updateWidgetNarration(builder: NarrationMessageBuilder) {
        defaultButtonNarrationText(builder)
    }

    override fun playDownSound(soundManager: SoundManager) {
        soundManager.play(PositionedSoundInstance.forUI(CobblemonSounds.GUI_CLICK, 1f))
    }
}
