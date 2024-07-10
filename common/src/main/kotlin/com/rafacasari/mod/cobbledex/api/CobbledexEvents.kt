package com.rafacasari.mod.cobbledex.api

import com.cobblemon.mod.common.api.reactive.EventObservable
import com.cobblemon.mod.common.pokemon.Species
import com.cobblemon.mod.common.pokemon.FormData
import com.rafacasari.mod.cobbledex.api.events.DiscoveryEvent

object CobbledexEvents {

    /**
     * Fired when a new [Species] is **discovered**
     */
    @JvmField
    val NEW_SPECIES_DISCOVERED = EventObservable<DiscoveryEvent.OnSpeciesDiscoveryEvent>()

    /**
     * Fired when a new [FormData] is **discovered**
     */
    @JvmField
    val NEW_FORM_DISCOVERED = EventObservable<DiscoveryEvent.OnFormDiscoveryEvent>()

    /**
     * Fired when a new [Species] is **caught**
     */
    @JvmField
    val NEW_SPECIES_CAUGHT = EventObservable<DiscoveryEvent.OnSpeciesDiscoveryEvent>()

    /**
     * Fired when a new [FormData] is **caught**
     */
    @JvmField
    val NEW_FORM_CAUGHT = EventObservable<DiscoveryEvent.OnFormDiscoveryEvent>()
}