package com.rafacasari.mod.cobbledex.network.server.packets

import com.rafacasari.mod.cobbledex.network.INetworkPacket
import net.minecraft.network.FriendlyByteBuf as PacketByteBuf
import net.minecraft.resources.ResourceLocation as Identifier

class RequestCobbledexPacket internal constructor(val pokemon: Identifier, val aspects: Set<String>, val form: String = ""):
    INetworkPacket<RequestCobbledexPacket> {
    override val id = ID

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeResourceLocation(pokemon)
        buffer.writeCollection(aspects) {
            buff, value -> buff.writeUtf(value)
        }
        buffer.writeUtf(form)
    }

    companion object{
        val ID = Identifier.fromNamespaceAndPath("cobbledex", "request_cobbledex")
        fun decode(buffer: PacketByteBuf) : RequestCobbledexPacket {
            val identifier = buffer.readResourceLocation()
            val aspects = buffer.readList {
                buff -> buff.readUtf()
            }.toSet()
            val form = buffer.readUtf()
            return RequestCobbledexPacket(identifier, aspects, form)
        }
    }
}
