package com.rafacasari.mod.cobbledex.network.client.packets

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.pokemon.FormData
import com.rafacasari.mod.cobbledex.network.INetworkPacket
import com.rafacasari.mod.cobbledex.utils.PacketUtils.readNullableIdentifier
import com.rafacasari.mod.cobbledex.utils.PacketUtils.writeNullableIdentifier
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

class OpenCobbledexPacket internal constructor(val formData: FormData?): INetworkPacket<OpenCobbledexPacket> {

    override val id = ID
    override fun encode(buffer: PacketByteBuf) {
        buffer.writeNullableIdentifier(formData?.species?.resourceIdentifier)

        val aspects = formData?.aspects ?: listOf()
        buffer.writeCollection(aspects) {
            bff, value -> bff.writeString(value)
        }

    }

    companion object{
        val ID = Identifier("cobbledex", "force_open_cobbledex")
        fun decode(reader: PacketByteBuf) : OpenCobbledexPacket {
            val species = reader.readNullableIdentifier()?.let {
                PokemonSpecies.getByIdentifier(it)
            }

            val aspects = reader.readList {
                listReader -> listReader.readString()
            }.toSet()

            val form= species?.getForm(aspects)
            return OpenCobbledexPacket(form)
        }
    }
}