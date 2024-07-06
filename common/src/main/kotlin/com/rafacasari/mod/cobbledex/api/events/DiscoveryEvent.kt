package com.rafacasari.mod.cobbledex.api.events

import com.cobblemon.mod.common.pokemon.FormData
import com.cobblemon.mod.common.pokemon.Species
import com.rafacasari.mod.cobbledex.api.classes.DiscoveryRegister
import net.minecraft.server.network.ServerPlayerEntity

interface DiscoveryEvent {
    val player: ServerPlayerEntity
    val register: DiscoveryRegister

    /**
     * Fired when a player discover a new [Species]
     */
    data class OnSpeciesDiscoveryEvent(override val player: ServerPlayerEntity, override val register: DiscoveryRegister, val species: Species) : DiscoveryEvent

    /**
     * Fired when a player discover a new [FormData]
     */
    data class OnFormDiscoveryEvent(override val player: ServerPlayerEntity, override val register: DiscoveryRegister, val form: FormData) : DiscoveryEvent
}