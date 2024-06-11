package com.rafacasari.mod.cobbledex.network.server.packets


import com.rafacasari.mod.cobbledex.network.server.INetworkPacket
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

class RequestCobbledexPacket internal constructor(val pokemon: Identifier): INetworkPacket<RequestCobbledexPacket> {
    override val id = ID

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeIdentifier(pokemon)
    }

    companion object{
        val ID = Identifier("cobbledex", "request_cobbledex")
        fun decode(buffer: PacketByteBuf) : RequestCobbledexPacket {
            return RequestCobbledexPacket(buffer.readIdentifier())
        }
    }
}