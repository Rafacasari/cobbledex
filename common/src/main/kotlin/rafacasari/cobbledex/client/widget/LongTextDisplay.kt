package rafacasari.cobbledex.client.widget


import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.client.render.drawScaledText
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
import net.minecraft.text.MutableText
import net.minecraft.text.OrderedText
import net.minecraft.text.Text
import net.minecraft.util.Language


class LongTextDisplay (
    private val x: Int = 0,
    private val y: Int = 0,
    private val frameWidth: Int,
    private val frameHeight: Int,
    val padding : Int = 3
): AlwaysSelectedEntryListWidget<LongTextDisplay.DialogueLine>(
    MinecraftClient.getInstance(),
    frameWidth,
    frameHeight, // height
    0, // top
    frameHeight, // bottom
    LINE_HEIGHT
) {
//    val dialogue = dialogueScreen.dialogueDTO
    var opacity = 1F
    private var scrolling = false

    init {
        correctSize()
        setRenderHorizontalShadows(false)
        setRenderBackground(false)
        setRenderSelection(false)

    }

    private fun scaleIt(i: Int): Int {
        return (client.window.scaleFactor * i).toInt()
    }

    private fun correctSize() {
//        updateSize(frameWidth - (padding * 2), frameHeight - (padding * 2), y + padding, (y) + (frameHeight) - (padding * 2))
        updateSize(frameWidth, frameHeight, y, y + frameHeight)
        setLeftPos(x + padding)
    }

//    private fun correctSize() {
//        val textBoxHeight = height
//        updateSize(width, textBoxHeight, appropriateY + 6, appropriateY + 6 + textBoxHeight)
//
//        setLeftPos(x)
//    }

    companion object {
        const val LINE_HEIGHT = 10
        const val SCROLLBAR_PADDING = 8
//        const val LINE_WIDTH = 180
    }

    private fun add(entry: DialogueLine): Int {
        return super.addEntry(entry)
    }

    fun add(entry: MutableText, breakLine: Boolean = true) {

        if (breakLine && super.getEntryCount() > 0) {
            super.addEntry(DialogueLine(null))
        }

        val textRenderer = MinecraftClient.getInstance().textRenderer
        Language.getInstance().reorder(textRenderer.textHandler.wrapLines(entry, rowWidth - SCROLLBAR_PADDING, entry.style)).forEach {
            add(DialogueLine(it))

        }

//        return super.addEntry(LongTextDisplay.DialogueLine(entry.asOrderedText()))
    }

    fun clear() {
        super.clearEntries()
    }

    override fun getRowWidth(): Int {
        return frameWidth - (padding * 2)
    }

    override fun getScrollbarPositionX(): Int {
        return left + width - 5
    }

    private fun scaleIt(i: Number): Int {
        return (client.window.scaleFactor * i.toFloat()).toInt()
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, partialTicks: Float) {
        correctSize()

        // We need scissor to cut the rest of our scrollbar to make it look prettier ✨
        context.enableScissor(
            x,
            y,
            x + frameWidth,
            y + frameWidth
        )
        super.render(context, mouseX, mouseY, partialTicks)
        context.disableScissor()
    }

//    override fun enableScissor(context: DrawContext) {
//        val textBoxHeight = height
//        context.enableScissor(
//            left,
//            appropriateY + 7,
//            left + frameWidth,
//            appropriateY + 7 + textBoxHeight
//        )
//    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        updateScrollState(mouseX, mouseY)
        if (scrolling) {
            focused = getEntryAtPosition(mouseX, mouseY)
            isDragging = true
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        if (scrolling) {
            if (mouseY < top) {
                scrollAmount = 0.0
            } else if (mouseY > bottom) {
                scrollAmount = maxScroll.toDouble()
            } else {
                scrollAmount += deltaY
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
    }

    private fun updateScrollState(mouseX: Double, mouseY: Double) {
        scrolling = mouseX >= (this.scrollbarPositionX - 3).toDouble()
                && mouseX < (this.scrollbarPositionX + 3).toDouble()
                && mouseY >= top
                && mouseY < bottom
    }

    class DialogueLine(val line: OrderedText?) : Entry<DialogueLine>() {
        override fun getNarration() = "".text()

        override fun render(
            context: DrawContext,
            index: Int,
            rowTop: Int,
            rowLeft: Int,
            rowWidth: Int,
            rowHeight: Int,
            mouseX: Int,
            mouseY: Int,
            isHovered: Boolean,
            partialTicks: Float
        ) {
            if (line != null) {
                drawScaledText(context, line, rowLeft, rowTop)
            }
        }
    }
}
