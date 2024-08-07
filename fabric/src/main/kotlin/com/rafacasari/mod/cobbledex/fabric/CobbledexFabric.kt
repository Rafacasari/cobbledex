package com.rafacasari.mod.cobbledex.fabric

import com.rafacasari.mod.cobbledex.*
import com.rafacasari.mod.cobbledex.Cobbledex.preInitialize
import com.rafacasari.mod.cobbledex.commands.CobbledexCommands
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.ModifyEntries
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemGroups
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.server.MinecraftServer
import net.minecraft.util.Identifier


object CobbledexFabric : CobbledexImplementation {

    override val networkManager: INetworkManager = FabricNetworkManager
    private var server: MinecraftServer? = null
    override fun server(): MinecraftServer? = if (this.environment() == Environment.CLIENT) MinecraftClient.getInstance().server else this.server


    fun initFabric() {
        preInitialize(this)
        networkManager.registerServerBound()

        // Register commands
        CommandRegistrationCallback.EVENT.register(CobbledexCommands::registerServer)

        ServerLifecycleEvents.SERVER_STARTING.register { server ->
            this.server = server
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
        Registry.register(Registries.ITEM, Identifier(Cobbledex.MOD_ID, "cobbledex_item"), CobbledexConstants.COBBLEDEX_ITEM)

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
            .register(ModifyEntries { content ->
                content.add(CobbledexConstants.COBBLEDEX_ITEM)
            })
    }
}