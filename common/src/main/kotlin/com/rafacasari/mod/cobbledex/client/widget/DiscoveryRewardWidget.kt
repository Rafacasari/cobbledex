package com.rafacasari.mod.cobbledex.client.widget

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.cobblemon.mod.common.client.render.drawScaledText
import com.rafacasari.mod.cobbledex.utils.MiscUtils.cobbledexResource
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.DiffuseLighting
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.joml.Matrix4f

class DiscoveryRewardWidget(x: Int, y: Int): SoundlessWidget(x, y, 74, 26, Text.literal("Rewards")) {
    companion object {
        private val ITEM_REWARD: Identifier = cobbledexResource("textures/gui/collection/item_reward.png")
    }

    private fun drawItem(context: DrawContext, stack: ItemStack, x: Int, y: Int, itemSize: Float) {
        if (!stack.isEmpty) {
            val bakedModel: BakedModel =  MinecraftClient.getInstance().itemRenderer.getModel(stack, null, null, 0)
            context.matrices.push()
            context.matrices.translate(x.toFloat(), y.toFloat(), 0f)

            try {
                context.matrices.multiplyPositionMatrix(Matrix4f().scaling(1.0f, -1.0f, 1.0f))
                context.matrices.scale(itemSize, itemSize, itemSize)
                val bl = !bakedModel.isSideLit
                if (bl) DiffuseLighting.disableGuiDepthLighting()

                MinecraftClient.getInstance().itemRenderer.renderItem(
                    stack, ModelTransformationMode.GUI, false,
                    context.matrices,
                    context.vertexConsumers, 15728880, OverlayTexture.DEFAULT_UV, bakedModel
                )
                context.draw()
                if (bl) DiffuseLighting.enableGuiDepthLighting()

            } catch (_: Exception) {

            }

            context.matrices.pop()
        }
    }

    override fun renderButton(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val matrices = context.matrices

        context.enableScissor(x, y, x + 74, y + 26)

        blitk(
            matrixStack = matrices,
            texture = ITEM_REWARD,
            x = x - 7,
            y = y,
            width = 26,
            height = 26,
            alpha = 0.5f
        )


        blitk(
            matrixStack = matrices,
            texture = ITEM_REWARD,
            x = x + width - 26 + 7,
            y = y,
            width = 26,
            height = 26,
            alpha = 0.5f
        )


        blitk(
            matrixStack = matrices,
            texture = ITEM_REWARD,
            x = x + 24,
            y = y,
            width = 26,
            height = 26
        )



        drawScaledText(
            context = context,
            text = Text.literal("Claim"),
            x = x + 24 + (26 / 2),
            y = y + 20,
            centered = true,
            scale = 0.5F, maxCharacterWidth = 26 * 2
        )

        val shinyStone = ItemStack(CobblemonItems.SHINY_STONE, 1)
        val iceStone = ItemStack(CobblemonItems.ICE_STONE, 1)
        val fireStone = ItemStack(CobblemonItems.FIRE_STONE, 1)

        drawItem(context, shinyStone, x + 24 + (26 / 2), y + (26 / 2) - 2, 16f)

        drawItem(context, iceStone, x - 7 + (26 / 2), y + (26 / 2), 16f)
        drawItem(context, fireStone, x + width - 26 + 7 + (26 / 2), y + (26 / 2), 16f)


        context.disableScissor()


    }
}