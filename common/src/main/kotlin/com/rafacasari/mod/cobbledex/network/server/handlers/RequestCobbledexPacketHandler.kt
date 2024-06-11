package com.rafacasari.mod.cobbledex.network.server.handlers

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.rafacasari.mod.cobbledex.network.client.packets.ReceiveCobbledexPacket
import com.rafacasari.mod.cobbledex.network.server.IServerNetworkPacketHandler
import com.rafacasari.mod.cobbledex.network.server.packets.RequestCobbledexPacket
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity

object RequestCobbledexPacketHandler : IServerNetworkPacketHandler<RequestCobbledexPacket> {
    override fun handle(packet: RequestCobbledexPacket, server: MinecraftServer, player: ServerPlayerEntity) {
        val pokemon = PokemonSpecies.getByIdentifier(packet.pokemon)?.create()

        if (pokemon != null ) {
            val evolutions = pokemon.evolutions.map { it.result.create().species.resourceIdentifier }
            if (evolutions.isNotEmpty())
                ReceiveCobbledexPacket(evolutions).sendToPlayer(player)
        }
    }
}