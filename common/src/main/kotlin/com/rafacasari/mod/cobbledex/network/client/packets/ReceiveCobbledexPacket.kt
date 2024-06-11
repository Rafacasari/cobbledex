package com.rafacasari.mod.cobbledex.network.client.packets

import com.rafacasari.mod.cobbledex.network.server.INetworkPacket
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

class ReceiveCobbledexPacket internal constructor(val evolutionList: List<Identifier>):
    INetworkPacket<ReceiveCobbledexPacket> {

    override val id = ID

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeCollection(evolutionList) {
            buff, value -> buff.writeIdentifier(value)
        }
    }

    companion object{
        val ID = Identifier("cobbledex", "receive_cobbledex")
        fun decode(buffer: PacketByteBuf) : ReceiveCobbledexPacket {
            val evolutionList = buffer.readList { value -> value.readIdentifier() }

            return ReceiveCobbledexPacket(evolutionList)
        }
    }
}