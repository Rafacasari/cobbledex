package com.rafacasari.mod.cobbledex.fabric

import com.rafacasari.mod.cobbledex.utils.logInfo
import net.fabricmc.api.ClientModInitializer

class CobbledexFabricClient: ClientModInitializer {
    override fun onInitializeClient() {
        logInfo("Fabric Client Initialized")

        CobbledexFabric.networkManager.registerClientBound()
    }
}