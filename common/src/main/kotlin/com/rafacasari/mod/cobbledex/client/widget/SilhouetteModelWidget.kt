package com.rafacasari.mod.cobbledex.client.widget
import com.cobblemon.mod.common.client.gui.drawProfilePokemon
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonFloatingState
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.util.math.fromEulerXYZDegrees
import com.rafacasari.mod.cobbledex.utils.CobblemonUtils.drawBlackSilhouettePokemon
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import org.joml.Quaternionf
import org.joml.Vector3f

class SilhouetteModelWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    var pokemon: RenderablePokemon,
    val baseScale: Float = 2.7F,
    var rotationY: Float = 35F,
    var offsetY: Double = 0.0,
    var isDiscovered: Boolean = false
): SoundlessWidget(pX, pY, pWidth, pHeight, Text.literal("Summary - ModelWidget")) {

    companion object {
        var render = true
    }

    var state = PokemonFloatingState()
    val rotVec = Vector3f(13F, rotationY, 0F)

    override fun renderButton(context: DrawContext, pMouseX: Int, pMouseY: Int, partialTicks: Float) {
        if (!render) {
            return
        }
        hovered = pMouseX >= x && pMouseY >= y && pMouseX < x + width && pMouseY < y + height
        renderPKM(context, partialTicks)
    }

    private fun renderPKM(context: DrawContext, partialTicks: Float) {
        val matrices = context.matrices
        matrices.push()

        context.enableScissor(
            x,
            y,
            x + width,
            y +  height
        )

        matrices.translate(x + width * 0.5, y.toDouble() + offsetY, 0.0)
        matrices.scale(baseScale, baseScale, baseScale)
        matrices.push()

        if (isDiscovered)
            drawProfilePokemon(
                renderablePokemon = pokemon,
                matrixStack = matrices,
                rotation = Quaternionf().fromEulerXYZDegrees(rotVec),
                state = state,
                partialTicks = partialTicks
            )
        else
            drawBlackSilhouettePokemon(
                species = pokemon.species.resourceIdentifier,
                aspects = pokemon.aspects,
                matrixStack = matrices,
                rotation = Quaternionf().fromEulerXYZDegrees(rotVec)
            )

        matrices.pop()
        context.disableScissor()

        matrices.pop()
    }

    override fun onClick(pMouseX: Double, pMouseY: Double) {
    }
}