package com.rafacasari.mod.cobbledex.network.client.packets

import com.rafacasari.mod.cobbledex.network.INetworkPacket
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

class OpenDiscoveryPacket internal constructor(): INetworkPacket<OpenDiscoveryPacket> {

    override val id = ID
    override fun encode(buffer: PacketByteBuf) {

    }

    companion object{
        val ID = Identifier("cobbledex", "force_open_discovery")
        fun decode(reader: PacketByteBuf) : OpenDiscoveryPacket {
            return OpenDiscoveryPacket()
        }
    }
}