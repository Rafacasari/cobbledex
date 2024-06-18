package com.rafacasari.mod.cobbledex.network.server

import net.minecraft.network.PacketByteBuf

interface IEncodable {
    fun encode(buffer: PacketByteBuf)
}