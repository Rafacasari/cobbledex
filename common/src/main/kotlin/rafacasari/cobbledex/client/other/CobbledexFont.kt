package rafacasari.cobbledex.client.other

import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.IntSet
import net.minecraft.client.font.Font
import net.minecraft.client.font.Glyph


class CobbledexFont : Font {
    override fun getProvidedGlyphs(): IntSet {
        TODO("Still needs to implement")


    }

    override fun getGlyph(codePoint: Int): Glyph? {
        return super.getGlyph(codePoint)
    }
}

class CobbledexFontAtlas {
    companion object {

    }

    protected var positions: Int2ObjectMap<Array<CobbledexFontIconLocation>>? = null
}

class CobbledexFontIconLocation {

}