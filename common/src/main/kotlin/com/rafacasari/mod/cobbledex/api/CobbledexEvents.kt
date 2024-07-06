package com.rafacasari.mod.cobbledex.api

import com.cobblemon.mod.common.api.reactive.EventObservable
import com.rafacasari.mod.cobbledex.api.events.DiscoveryEvent

object CobbledexEvents {
    @JvmField
    val NEW_SPECIES_DISCOVERED = EventObservable<DiscoveryEvent.OnSpeciesDiscoveryEvent>()

    @JvmField
    val NEW_FORM_DISCOVERED = EventObservable<DiscoveryEvent.OnFormDiscoveryEvent>()

    @JvmField
    val NEW_SPECIES_CAUGHT = EventObservable<DiscoveryEvent.OnSpeciesDiscoveryEvent>()

    @JvmField
    val NEW_FORM_CAUGHT = EventObservable<DiscoveryEvent.OnFormDiscoveryEvent>()
}