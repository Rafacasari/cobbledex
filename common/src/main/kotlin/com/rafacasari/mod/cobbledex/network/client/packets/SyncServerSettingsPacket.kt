package com.rafacasari.mod.cobbledex.network.client.packets

import com.rafacasari.mod.cobbledex.CobbledexConfig
import com.rafacasari.mod.cobbledex.network.INetworkPacket
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

class SyncServerSettingsPacket internal constructor(val config: CobbledexConfig): INetworkPacket<SyncServerSettingsPacket> {
    override val id = ID

    override fun encode(buffer: PacketByteBuf) {
        config.encode(buffer)
    }

    companion object {
        val ID = Identifier("cobbledex", "sync_server_settings")
        fun decode(reader: PacketByteBuf): SyncServerSettingsPacket {

            return SyncServerSettingsPacket(CobbledexConfig.decode(reader))
        }
    }
}