package com.rafacasari.mod.cobbledex.network.client.packets

import com.rafacasari.mod.cobbledex.network.INetworkPacket
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

class AddToCollectionPacket internal constructor(val nationalNumber: Int):
    INetworkPacket<AddToCollectionPacket> {

    override val id = ID

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeInt(nationalNumber)
    }

    companion object{
        val ID = Identifier("cobbledex", "add_to_collection")
        fun decode(reader: PacketByteBuf) : AddToCollectionPacket {
            return AddToCollectionPacket(reader.readInt())
        }
    }
}