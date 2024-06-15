package com.rafacasari.mod.cobbledex.network.client.packets

import com.rafacasari.mod.cobbledex.network.server.INetworkPacket
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

class PokedexDiscoveredUpdated internal constructor(val discoveredCount: Int): INetworkPacket<PokedexDiscoveredUpdated> {

    override val id = ID

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeInt(discoveredCount)
    }

    companion object{
        val ID = Identifier("cobbledex", "updated_discovered_count")
        fun decode(buffer: PacketByteBuf) : PokedexDiscoveredUpdated {
            return PokedexDiscoveredUpdated(buffer.readInt())
        }
    }
}