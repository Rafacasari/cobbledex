package com.rafacasari.mod.cobbledex.fabric

import com.rafacasari.mod.cobbledex.Cobbledex
import com.rafacasari.mod.cobbledex.CobbledexConstants
import com.rafacasari.mod.cobbledex.CobbledexImplementation
import com.rafacasari.mod.cobbledex.Environment
import com.rafacasari.mod.cobbledex.INetworkManager
import com.rafacasari.mod.cobbledex.ModAPI
import com.rafacasari.mod.cobbledex.commands.CobbledexCommands
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft as MinecraftClient
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation as Identifier
import net.minecraft.server.MinecraftServer
import net.minecraft.world.item.CreativeModeTab

object CobbledexFabric : CobbledexImplementation {

    private val toolsTabKey: ResourceKey<CreativeModeTab> =
        ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.withDefaultNamespace("tools_and_utilities"))

    override val networkManager: INetworkManager = FabricNetworkManager
    private var server: MinecraftServer? = null

    override fun server(): MinecraftServer? =
        if (environment() == Environment.CLIENT) MinecraftClient.getInstance().singleplayerServer else server

    fun initFabric() {
        Cobbledex.preInitialize(this)
        networkManager.registerServerBound()

        CommandRegistrationCallback.EVENT.register(CobbledexCommands::registerServer)

        ServerLifecycleEvents.SERVER_STARTING.register { currentServer ->
            server = currentServer
        }
    }

    override val modAPI: ModAPI = ModAPI.FABRIC

    override fun environment(): Environment {
        return when (FabricLoader.getInstance().environmentType) {
            EnvType.CLIENT -> Environment.CLIENT
            EnvType.SERVER -> Environment.SERVER
        }
    }

    override fun registerItems() {
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Cobbledex.MOD_ID, "cobbledex_item"), CobbledexConstants.COBBLEDEX_ITEM)

        ItemGroupEvents.modifyEntriesEvent(toolsTabKey).register { entries ->
            entries.prepend(CobbledexConstants.COBBLEDEX_ITEM)
        }
    }
}
