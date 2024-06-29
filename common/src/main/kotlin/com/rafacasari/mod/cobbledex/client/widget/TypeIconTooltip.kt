package com.rafacasari.mod.cobbledex.client.widget

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.util.cobblemonResource
import com.rafacasari.mod.cobbledex.utils.MiscUtils.toMutableText
import com.rafacasari.mod.cobbledex.utils.MiscUtils.withRGBColor
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Drawable
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.ScreenPos
import net.minecraft.client.gui.ScreenRect
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.gui.widget.Widget
import net.minecraft.text.MutableText
import net.minecraft.util.Formatting
import java.util.function.Consumer

class TypeIconTooltip(
    private var x: Int,
    private var y: Int,
    var primaryType: ElementalType,
    var secondaryType: ElementalType? = null,
    val centeredX: Boolean = false,
    val small: Boolean = false,
    val secondaryOffset: Float = 15F,
    val doubleCenteredOffset: Float = 7.5F,
) : Drawable, Widget, Element, Selectable {
    companion object {
        private const val TYPE_ICON_DIAMETER = 36
        private const val SCALE = 0.5F

        private val typesResource = cobblemonResource("textures/gui/types.png")
        private val smallTypesResource = cobblemonResource("textures/gui/types_small.png")

        // Import backgrounds here, so we don't need to deal with it on screens
    }

    private val diameter = if (small) (TYPE_ICON_DIAMETER / 2) else TYPE_ICON_DIAMETER


    fun drawTooltip(context: DrawContext, mouseX: Int, mouseY: Int) {
        val space = if (secondaryType != null) 16 else 8
        val offsetX = if (centeredX) diameter / 2 * SCALE + (if (secondaryType != null) (doubleCenteredOffset) else 0f) else 0f

        if (mouseX.toFloat() in x - offsetX .. (x.toFloat() + offsetX) && mouseY.toFloat() in y.toFloat() .. (y + 16f)) {
            val text = mutableListOf<MutableText>()

            text.add(primaryType.displayName.withRGBColor(primaryType.hue).formatted(Formatting.BOLD))

            secondaryType?.let { secondType ->
                text.add(text(" & ").formatted(Formatting.BOLD))
                text.add(secondType.displayName.withRGBColor(secondType.hue).bold())
            }

            context.drawTooltip(MinecraftClient.getInstance().textRenderer, text.toMutableText(false), mouseX, mouseY)
        }

    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float)
    {
        val offsetX = if (centeredX) (((diameter / 2) * SCALE) + (if (secondaryType != null) (doubleCenteredOffset) else 0F)) else 0F
        secondaryType?.let {
            blitk(
                matrixStack = context.matrices,
                texture = if (small) smallTypesResource else typesResource,
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
            matrixStack = context.matrices,
            texture = if (small) smallTypesResource else typesResource,
            x = (x.toFloat() - offsetX) / SCALE,
            y = y.toFloat() / SCALE,
            height = diameter,
            width = diameter,
            uOffset = diameter * primaryType.textureXMultiplier.toFloat() + 0.1,
            textureWidth = diameter * 18,
            scale = SCALE
        )
    }

    override fun setX(x: Int) {
        this.x = x
    }

    override fun setY(y: Int) {
        this.y = y
    }

    override fun getX(): Int {
        return x
    }

    override fun getY(): Int {
        return y
    }

    override fun getWidth(): Int {
        return diameter
    }

    override fun getHeight(): Int {
        return diameter
    }

    override fun setFocused(focused: Boolean) {

    }

    override fun isFocused(): Boolean = false


    override fun forEachChild(consumer: Consumer<ClickableWidget>?) {

    }

    override fun getNavigationFocus(): ScreenRect {
        return ScreenRect(ScreenPos(x, y), width, height)
    }

    override fun appendNarrations(builder: NarrationMessageBuilder?) {
    }

    override fun getType(): Selectable.SelectionType = Selectable.SelectionType.NONE

}