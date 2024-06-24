package com.rafacasari.mod.cobbledex.network.client.packets

import com.rafacasari.mod.cobbledex.network.INetworkPacket
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

class ReceiveCollectionDataPacket internal constructor(val discoveredList: List<Int>):
    INetworkPacket<ReceiveCollectionDataPacket> {

    override val id = ID

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeCollection(discoveredList) {
            bff, value -> bff.writeInt(value)
        }
    }

    companion object{
        val ID = Identifier("cobbledex", "receive_collection_data")
        fun decode(reader: PacketByteBuf) : ReceiveCollectionDataPacket {
            val discoveredList : List<Int> = reader.readList {
                read -> read.readInt()
            }

            return ReceiveCollectionDataPacket(discoveredList)
        }
    }
}