package com.rafacasari.mod.cobbledex.network.server.packets

import com.rafacasari.mod.cobbledex.network.server.INetworkPacket
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

class RequestCobbledexPacket internal constructor(val pokemon: Identifier, val aspects: Set<String>, val form: String = ""): INetworkPacket<RequestCobbledexPacket> {
    override val id = ID

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeIdentifier(pokemon)
        buffer.writeCollection(aspects) {
            buff, value -> buff.writeString(value)
        }
        buffer.writeString(form)
    }

    companion object{
        val ID = Identifier("cobbledex", "request_cobbledex")
        fun decode(buffer: PacketByteBuf) : RequestCobbledexPacket {
            val identifier = buffer.readIdentifier()
            val aspects = buffer.readList {
                buff -> buff.readString()
            }.toSet()
            val form = buffer.readString()
            return RequestCobbledexPacket(identifier, aspects, form)
        }
    }
}