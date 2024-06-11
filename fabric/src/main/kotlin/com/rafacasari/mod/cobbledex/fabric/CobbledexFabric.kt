package com.rafacasari.mod.cobbledex.fabric

import com.mojang.brigadier.CommandDispatcher
import com.rafacasari.mod.cobbledex.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import com.rafacasari.mod.cobbledex.Cobbledex.preInitialize
import com.rafacasari.mod.cobbledex.commands.CobbledexCommands
import com.rafacasari.mod.cobbledex.utils.logError
import com.rafacasari.mod.cobbledex.utils.logInfo
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.server.MinecraftServer

object CobbledexFabric : ModInitializer, CobbledexImplementation {

    override val networkManager: INetworkManager = FabricNetworkManager
    private var server: MinecraftServer? = null
    override fun server(): MinecraftServer? = if (this.environment() == Environment.CLIENT) MinecraftClient.getInstance().server else this.server


    override fun onInitialize() {
        preInitialize(this)
        networkManager.registerClientBound()

        // Register commands
        //CommandRegistrationCallback.EVENT.register(CobbledexCommands::registerServer)
        ClientCommandRegistrationCallback.EVENT.register {
            dispatcher, _ -> registerClientCommand(dispatcher)
        }


        ServerLifecycleEvents.SERVER_STARTING.register { server ->
            this.server = server
        }
    }


    private fun registerClientCommand(commandDispatcher: CommandDispatcher<FabricClientCommandSource>?)
    {
        try {
            logInfo("Cobbledex: REGISTERING CLIENT COMMANDS")
            commandDispatcher?.let { CobbledexCommands.registerClient(it) }
        } catch (e: Exception) {
            logError("Failed to register Cobbledex client commands!")
            logError(e.toString())
        }
    }

    override val modAPI: ModAPI = ModAPI.FABRIC

    override fun environment(): Environment {
        return when(FabricLoader.getInstance().environmentType) {
            EnvType.CLIENT -> Environment.CLIENT
            EnvType.SERVER -> Environment.SERVER
            else -> throw IllegalStateException("Fabric implementation cannot resolve environment yet")
        }
    }

    override fun registerItems() {
        Registry.register(Registries.ITEM, Identifier(Cobbledex.MOD_ID, "cobbledex_item"), CobbledexConstants.Cobbledex_Item)
    }
}