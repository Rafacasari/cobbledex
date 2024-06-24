package com.rafacasari.mod.cobbledex.network.client.handlers

import com.rafacasari.mod.cobbledex.client.gui.CobbledexGUI
import com.rafacasari.mod.cobbledex.network.client.packets.OpenCobbledexPacket
import com.rafacasari.mod.cobbledex.network.client.IClientNetworkPacketHandler
import net.minecraft.client.MinecraftClient

object OpenCobbledexHandler : IClientNetworkPacketHandler<OpenCobbledexPacket> {
    override fun handle(packet: OpenCobbledexPacket, client: MinecraftClient) {
        CobbledexGUI.openCobbledexScreen(packet.formData)
    }
}