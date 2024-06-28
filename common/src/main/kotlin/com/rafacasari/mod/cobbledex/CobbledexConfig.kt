package com.rafacasari.mod.cobbledex

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rafacasari.mod.cobbledex.network.IEncodable
import com.rafacasari.mod.cobbledex.network.client.packets.SyncServerSettingsPacket
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity

class CobbledexConfig : IEncodable {

    var HowToFind_IsEnabled = true
    var HowToFind_NeedSeen = false
    var HowToFind_NeedCatch = false

    var ShowEvolutions_IsEnabled = true
    var ShowEvolutions_NeedSeen = false
    var ShowEvolutions_NeedCatch = false

    var ItemDrops_IsEnabled = true
    var ItemDrops_NeedSeen = false
    var ItemDrops_NeedCatch = false

    var Collection_NeedSeen = false
    var Collection_NeedCatch = false


    companion object {
        val GSON: Gson = GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create()

        fun decode(reader: PacketByteBuf) : CobbledexConfig {
            val config = CobbledexConfig()

            config.HowToFind_IsEnabled = reader.readBoolean()
            config.HowToFind_NeedSeen = reader.readBoolean()
            config.HowToFind_NeedCatch = reader.readBoolean()

            config.ShowEvolutions_IsEnabled = reader.readBoolean()
            config.ShowEvolutions_NeedSeen = reader.readBoolean()
            config.ShowEvolutions_NeedCatch = reader.readBoolean()

            config.ItemDrops_IsEnabled = reader.readBoolean()
            config.ItemDrops_NeedSeen = reader.readBoolean()
            config.ItemDrops_NeedCatch = reader.readBoolean()

            config.Collection_NeedSeen = reader.readBoolean()
            config.Collection_NeedCatch = reader.readBoolean()

            return config
        }
    }

    internal var lastSavedVersion: String = "0.0.1"


    fun syncEveryone() {
        SyncServerSettingsPacket(this).sendToAllPlayers()
    }

    fun syncPlayer(player: ServerPlayerEntity) {
        SyncServerSettingsPacket(this).sendToPlayer(player)
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeBoolean(HowToFind_IsEnabled)
        buffer.writeBoolean(HowToFind_NeedSeen)
        buffer.writeBoolean(HowToFind_NeedCatch)

        buffer.writeBoolean(ShowEvolutions_IsEnabled)
        buffer.writeBoolean(ShowEvolutions_NeedSeen)
        buffer.writeBoolean(ShowEvolutions_NeedCatch)

        buffer.writeBoolean(ItemDrops_IsEnabled)
        buffer.writeBoolean(ItemDrops_NeedSeen)
        buffer.writeBoolean(ItemDrops_NeedCatch)

        buffer.writeBoolean(Collection_NeedSeen)
        buffer.writeBoolean(Collection_NeedCatch)
    }

}
