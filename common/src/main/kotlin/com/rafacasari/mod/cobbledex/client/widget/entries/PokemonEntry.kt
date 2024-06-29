package com.rafacasari.mod.cobbledex.client.widget.entries

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.gui.drawPortraitPokemon
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.pokemon.Species
import com.rafacasari.mod.cobbledex.client.widget.LongTextDisplay
import com.rafacasari.mod.cobbledex.utils.MiscUtils.cobbledexResource
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.OrderedText

class PokemonEntry(val species: Species, val aspects: Set<String>, val text: OrderedText) : LongTextDisplay.TextDisplayEntry() {
    companion object
    {
        private val BACKGROUND = cobbledexResource("textures/gui/evolution-menu/background.png")
        private val OVERLAY = cobbledexResource("textures/gui/evolution-menu/overlay.png")
        private const val IMAGE_SIZE = 16
        private const val Y_OFFSET = 1
        private const val X_OFFSET = -2
    }


    override fun render(
        context: DrawContext?,
        index: Int,
        y: Int,
        x: Int,
        entryWidth: Int,
        entryHeight: Int,
        mouseX: Int,
        mouseY: Int,
        hovered: Boolean,
        tickDelta: Float
    ) {
        if (context == null) return
        val matrices = context.matrices


        blitk(
            matrixStack = matrices,
            texture = BACKGROUND,
            x = x + X_OFFSET,
            y = y + Y_OFFSET,
            height = IMAGE_SIZE,
            width = IMAGE_SIZE
        )

        context.enableScissor(
            x + X_OFFSET,
            y + Y_OFFSET,
            x + X_OFFSET + IMAGE_SIZE,
            y + Y_OFFSET + IMAGE_SIZE
        )

        matrices.push()
        matrices.translate(
            x + 10.0 + X_OFFSET,
            y.toDouble() - 3 + Y_OFFSET,
            0.0
        )
        matrices.scale(0.5f, 0.5f,1f)

        drawPortraitPokemon(species, aspects, matrices, partialTicks = tickDelta)

        matrices.pop()
        context.disableScissor()


        blitk(
            matrixStack = matrices,
            texture = OVERLAY,
            x = x + X_OFFSET,
            y = y + Y_OFFSET,
            height = IMAGE_SIZE,
            width = IMAGE_SIZE
        )

        drawScaledText(context, text, x + 18, y + 5, scaleX = 1.1f, scaleY = 1.1f)
    }

    override fun drawTooltip(context: DrawContext, mouseX: Int, mouseY: Int) {

    }
}