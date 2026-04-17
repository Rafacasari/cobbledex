package com.rafacasari.mod.cobbledex.client.widget

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.util.cobblemonResource
import com.rafacasari.mod.cobbledex.utils.MiscUtils.toMutableText
import com.rafacasari.mod.cobbledex.utils.MiscUtils.withRGBColor
import net.minecraft.ChatFormatting as Formatting
import net.minecraft.client.Minecraft as MinecraftClient
import net.minecraft.client.gui.GuiGraphics as DrawContext
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput as NarrationMessageBuilder
import net.minecraft.network.chat.Component as Text
import net.minecraft.network.chat.MutableComponent as MutableText

class TypeIconTooltip(
    x: Int,
    y: Int,
    var primaryType: ElementalType,
    var secondaryType: ElementalType? = null,
    val centeredX: Boolean = false,
    val small: Boolean = false,
    val secondaryOffset: Float = 15F,
    val doubleCenteredOffset: Float = 7.5F,
) : AbstractWidget(x, y, TYPE_ICON_DIAMETER, TYPE_ICON_DIAMETER, Text.empty()) {
    companion object {
        private const val TYPE_ICON_DIAMETER = 36
        private const val SCALE = 0.5F

        private val typesResource = cobblemonResource("textures/gui/types.png")
        private val smallTypesResource = cobblemonResource("textures/gui/types_small.png")
    }

    private val diameter = if (small) TYPE_ICON_DIAMETER / 2 else TYPE_ICON_DIAMETER

    fun renderTooltip(context: DrawContext, mouseX: Int, mouseY: Int) {
        val offsetX = if (centeredX) diameter / 2 * SCALE + if (secondaryType != null) doubleCenteredOffset else 0f else 0f
        if (mouseX.toFloat() in x - offsetX..(x.toFloat() + offsetX + width) && mouseY.toFloat() in y.toFloat()..(y + 16f)) {
            val text = mutableListOf<MutableText>()
            text.add(primaryType.displayName.withRGBColor(primaryType.hue).withStyle(Formatting.BOLD))

            secondaryType?.let { secondType ->
                text.add(text(" & ").withStyle(Formatting.BOLD))
                text.add(secondType.displayName.withRGBColor(secondType.hue).bold())
            }

            context.renderTooltip(MinecraftClient.getInstance().font, text.toMutableText(false), mouseX, mouseY)
        }
    }

    override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val texture = if (small) smallTypesResource else typesResource
        val offsetX = if (centeredX) ((diameter / 2) * SCALE) + if (secondaryType != null) doubleCenteredOffset else 0F else 0F

        secondaryType?.let {
            blitk(
                matrixStack = context.pose(),
                texture = texture,
                x = (x.toFloat() + secondaryOffset - offsetX) / SCALE,
                y = y.toFloat() / SCALE,
                height = diameter,
                width = diameter,
                uOffset = diameter * it.textureXMultiplier.toFloat() + 0.1,
                textureWidth = diameter * 18,
                scale = SCALE
            )
        }

        blitk(
            matrixStack = context.pose(),
            texture = texture,
            x = (x.toFloat() - offsetX) / SCALE,
            y = y.toFloat() / SCALE,
            height = diameter,
            width = diameter,
            uOffset = diameter * primaryType.textureXMultiplier.toFloat() + 0.1,
            textureWidth = diameter * 18,
            scale = SCALE
        )
    }

    override fun updateWidgetNarration(builder: NarrationMessageBuilder) = Unit
}
