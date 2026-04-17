package com.rafacasari.mod.cobbledex.network.client.packets

import com.rafacasari.mod.cobbledex.network.INetworkPacket
import net.minecraft.network.FriendlyByteBuf as PacketByteBuf
import net.minecraft.resources.ResourceLocation as Identifier

class OpenDiscoveryPacket internal constructor(): INetworkPacket<OpenDiscoveryPacket> {

    override val id = ID
    override fun encode(buffer: PacketByteBuf) {

    }

    companion object{
        val ID = Identifier.fromNamespaceAndPath("cobbledex", "force_open_discovery")
        fun decode(reader: PacketByteBuf) : OpenDiscoveryPacket {
            return OpenDiscoveryPacket()
        }
    }
}