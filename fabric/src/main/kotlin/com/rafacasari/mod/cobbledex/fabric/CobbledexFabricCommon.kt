package com.rafacasari.mod.cobbledex.fabric

import net.fabricmc.api.ModInitializer

class CobbledexFabricCommon : ModInitializer {
    override fun onInitialize() {
        CobbledexFabric.initFabric()
    }
}