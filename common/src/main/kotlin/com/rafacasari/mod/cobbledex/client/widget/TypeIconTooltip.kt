package com.rafacasari.mod.cobbledex.client.widget

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Drawable
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.gui.widget.Widget
import java.util.function.Consumer

class TypeIconTooltip(
    private var x: Number,
    private var y: Number,
    var type: ElementalType,
    var secondaryType: ElementalType? = null,
    val centeredX: Boolean = false,
    val small: Boolean = false,
    val secondaryOffset: Float = 15F,
    val doubleCenteredOffset: Float = 7.5F,
    val opacity: Float = 1F
) : Drawable, Widget {
    companion object {
        private const val TYPE_ICON_DIAMETER = 36
        private const val SCALE = 0.5F

        private val typesResource = cobblemonResource("textures/gui/types.png")
        private val smallTypesResource = cobblemonResource("textures/gui/types_small.png")
    }

    private val diameter = if (small) (TYPE_ICON_DIAMETER / 2) else TYPE_ICON_DIAMETER


    fun drawTooltip(context: DrawContext) {

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
                alpha = opacity,
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
            uOffset = diameter * type.textureXMultiplier.toFloat() + 0.1,
            textureWidth = diameter * 18,
            alpha = opacity,
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
        return x.toInt()
    }

    override fun getY(): Int {
        return y.toInt()
    }

    override fun getWidth(): Int {
        return diameter
    }

    override fun getHeight(): Int {
        return diameter
    }

    override fun forEachChild(consumer: Consumer<ClickableWidget>?) {

    }
}