package com.rafacasari.mod.cobbledex.network

import net.minecraft.network.FriendlyByteBuf as PacketByteBuf

interface IEncodable {
    fun encode(buffer: PacketByteBuf)
}