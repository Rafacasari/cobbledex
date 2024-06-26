package com.rafacasari.mod.cobbledex.api.classes

import com.rafacasari.mod.cobbledex.network.IEncodable
import com.rafacasari.mod.cobbledex.utils.PacketUtils.readNullableLong
import com.rafacasari.mod.cobbledex.utils.PacketUtils.writeNullableLong
import net.minecraft.network.PacketByteBuf
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class DiscoveryRegister(var isShiny: Boolean, var status: RegisterType, var discoveredTimestamp: Long?, var caughtTimestamp: Long?) : IEncodable {
    enum class RegisterType {
        SEEN, CAUGHT
    }

    fun getDiscoveredTimestamp(): Text? {
        if (discoveredTimestamp == null) return null

        val localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(discoveredTimestamp!!), ZoneId.systemDefault())
        return Text.literal(localDateTime.format(TIME_FORMAT)).formatted(Formatting.ITALIC, Formatting.GRAY)
    }

    fun getCaughtTimestamp(): Text? {
        if (caughtTimestamp == null) return null

        val localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(caughtTimestamp!!), ZoneId.systemDefault())
        return Text.literal(localDateTime.format(TIME_FORMAT)).formatted(Formatting.ITALIC, Formatting.GRAY)
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeBoolean(isShiny)
        buffer.writeString(status.name)
        buffer.writeNullableLong(discoveredTimestamp)
        buffer.writeNullableLong(caughtTimestamp)
    }

    companion object {
        private val TIME_FORMAT = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)

        fun decode(reader: PacketByteBuf) : DiscoveryRegister {
            val isShiny = reader.readBoolean()
            val status = RegisterType.valueOf(reader.readString())
            val discoveredTimestamp = reader.readNullableLong()
            val caughtTimestamp = reader.readNullableLong()

            return DiscoveryRegister(isShiny, status, discoveredTimestamp, caughtTimestamp)
        }
    }
}