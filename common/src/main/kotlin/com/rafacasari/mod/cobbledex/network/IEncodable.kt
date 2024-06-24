package com.rafacasari.mod.cobbledex.network

import net.minecraft.network.PacketByteBuf

interface IEncodable {
    fun encode(buffer: PacketByteBuf)
}