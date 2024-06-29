package com.rafacasari.mod.cobbledex.fabric

import com.mojang.brigadier.CommandDispatcher
import com.rafacasari.mod.cobbledex.commands.CobbledexCommands
import com.rafacasari.mod.cobbledex.utils.MiscUtils.logError
import com.rafacasari.mod.cobbledex.utils.MiscUtils.logInfo
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

class CobbledexFabricClient: ClientModInitializer {
    override fun onInitializeClient() {
        logInfo("Fabric Client Initialized")

        CobbledexFabric.networkManager.registerClientBound()

        ClientCommandRegistrationCallback.EVENT.register {
                dispatcher, _ -> registerClientCommand(dispatcher)
        }
    }

    private fun registerClientCommand(commandDispatcher: CommandDispatcher<FabricClientCommandSource>?)
    {
        try {
            logInfo("REGISTERING FABRIC CLIENT COMMANDS")
            commandDispatcher?.let { CobbledexCommands.registerClient(it) }
        } catch (e: Exception) {
            logError("Failed to register Cobbledex client commands!")
            logError(e.toString())
        }
    }

}