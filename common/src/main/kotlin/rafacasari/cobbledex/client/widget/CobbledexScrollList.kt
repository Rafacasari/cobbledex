package rafacasari.cobbledex.client.widget

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.client.CobblemonResources
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
import net.minecraft.text.MutableText

abstract class CobbledexScrollList<T : AlwaysSelectedEntryListWidget.Entry<T>>(
    private val x: Int,
    private val y: Int,
    slotHeight: Int
) : AlwaysSelectedEntryListWidget<T>(
    MinecraftClient.getInstance(),
    WIDTH, // width
    HEIGHT, // height
    0, // top
    HEIGHT, // bottom
    slotHeight
) {
    companion object {
        const val WIDTH = 82
        const val HEIGHT = 119
        const val SLOT_WIDTH = 80

//        private val backgroundResource = cobblemonResource("textures/gui/summary/summary_scroll_background.png")
    }

    private var scrolling = false

    override fun getRowWidth(): Int {
        return SLOT_WIDTH
    }

    init {
        correctSize()
        super.setRenderHorizontalShadows(false)
        super.setRenderBackground(false)
        super.setRenderSelection(false)
    }

    override fun getScrollbarPositionX(): Int {
        return left + width - 1
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, partialTicks: Float) {
        val matrices = context.matrices
        correctSize()

        context.enableScissor(
            left,
            top + 1,
            left + width,
            top + 1 + height
        )
        super.render(context, mouseX, mouseY, partialTicks)
        context.disableScissor()

    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        updateScrollingState(mouseX, mouseY)
        if (scrolling) {
            focused = getEntryAtPosition(mouseX, mouseY)
            isDragging = true
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        if (scrolling) {
            if (mouseY < top) {
                setScrollAmount(0.0)
            } else if (mouseY > bottom) {
                setScrollAmount(maxScroll.toDouble())
            } else {
                setScrollAmount(scrollAmount + deltaY)
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
    }

    private fun updateScrollingState(mouseX: Double, mouseY: Double) {
        scrolling = mouseX >= this.scrollbarPositionX.toDouble()
                && mouseX < (this.scrollbarPositionX + 3).toDouble()
                && mouseY >= top
                && mouseY < bottom
    }

    private fun correctSize() {
        updateSize(WIDTH, HEIGHT, y + 1, (y + 1) + (HEIGHT - 2))
        setLeftPos(x)
    }

//    private fun scaleIt(i: Int): Int {
//        return (client.window.scaleFactor * i).toInt()
//    }
}
