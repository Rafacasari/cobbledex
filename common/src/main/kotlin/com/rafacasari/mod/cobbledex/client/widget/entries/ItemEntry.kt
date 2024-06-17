package com.rafacasari.mod.cobbledex.client.widget.entries


import com.rafacasari.mod.cobbledex.client.widget.LongTextDisplay
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.DiffuseLighting
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.item.ItemStack
import net.minecraft.text.OrderedText
import net.minecraft.util.Colors
import org.joml.Matrix4f

class ItemEntry(val item: ItemStack, val text: OrderedText) : LongTextDisplay.TextDisplayEntry() {
    companion object
    {
        const val ITEM_SIZE = 10.5F
    }

    private fun drawItemName(context: DrawContext, text: OrderedText, x: Number, y: Number, pMouseX: Int? = null, pMouseY: Int? = null): Boolean {
        val textRenderer = MinecraftClient.getInstance().textRenderer
        val width = textRenderer.getWidth(text)

        context.drawText(textRenderer, text, x.toInt(), y.toInt(), Colors.WHITE, false)

        // Return isHovered
        return pMouseY != null && pMouseX != null &&
                pMouseX.toInt() >= x.toInt() && pMouseX.toInt() <= x.toInt() + width &&
                pMouseY.toInt() >= y.toInt() && pMouseY.toInt() <= y.toInt() + textRenderer.fontHeight
    }

    private fun drawItem(context: DrawContext, stack: ItemStack, x: Int, y: Int) {
        if (!stack.isEmpty) {
            val bakedModel: BakedModel =  MinecraftClient.getInstance().itemRenderer.getModel(stack, null, null, 0)
            context.matrices.push()
            context.matrices.translate(
                (x + 3).toFloat(),
                (y + 3.5).toFloat(),
                150.toFloat()
            )

            try {
                context.matrices.multiplyPositionMatrix(Matrix4f().scaling(1.0f, -1.0f, 1.0f))
                context.matrices.scale(ITEM_SIZE, ITEM_SIZE, ITEM_SIZE)
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

    var isItemHovered = false
    var isNameHovered = false

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

        drawItem(context, item, x, y)
        isNameHovered = drawItemName(context, text, x + ITEM_SIZE, y, mouseX, mouseY)

//        val offset = 0.5f
        isItemHovered = mouseX.toFloat() in ((x.toFloat() - 1)..(x.toFloat() - 2 + ITEM_SIZE))
                                && mouseY.toFloat() in ((y.toFloat() - 1)..(y.toFloat() - 2 + ITEM_SIZE))
//        if (hovered) context.drawItemTooltip(MinecraftClient.getInstance().textRenderer, item, mouseX, mouseY)


    }

    override fun drawTooltip(context: DrawContext, mouseX: Int, mouseY: Int) {
        context.drawItemTooltip(MinecraftClient.getInstance().textRenderer, item, mouseX, mouseY)
    }

    override fun isMouseOver(mouseX: Double, mouseY: Double): Boolean {
        return isItemHovered || isNameHovered
    }
}