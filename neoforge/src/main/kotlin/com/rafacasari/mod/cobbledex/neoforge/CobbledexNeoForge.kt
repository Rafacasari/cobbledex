package com.rafacasari.mod.cobbledex.neoforge

import com.rafacasari.mod.cobbledex.Cobbledex
import com.rafacasari.mod.cobbledex.CobbledexConstants
import com.rafacasari.mod.cobbledex.CobbledexImplementation
import com.rafacasari.mod.cobbledex.Environment
import com.rafacasari.mod.cobbledex.INetworkManager
import com.rafacasari.mod.cobbledex.ModAPI
import com.rafacasari.mod.cobbledex.commands.CobbledexCommands
import com.rafacasari.mod.cobbledex.utils.MiscUtils.logError
import com.rafacasari.mod.cobbledex.utils.MiscUtils.logInfo
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation as Identifier
import net.minecraft.server.MinecraftServer
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.fml.loading.FMLEnvironment
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.registries.RegisterEvent
import net.neoforged.neoforge.server.ServerLifecycleHooks

@Mod(Cobbledex.MOD_ID)
class CobbledexNeoForge(private val modBus: IEventBus) : CobbledexImplementation {

    private val toolsTabKey: ResourceKey<CreativeModeTab> =
        ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.withDefaultNamespace("tools_and_utilities"))

    override val networkManager: INetworkManager = NeoForgeNetworkManager
    override val modAPI: ModAPI = ModAPI.NEOFORGE

    override fun server(): MinecraftServer? = ServerLifecycleHooks.getCurrentServer()

    init {
        Cobbledex.preInitialize(this)

        modBus.addListener(this::initialize)
        modBus.addListener(NeoForgeNetworkManager::registerMessages)

        NeoForge.EVENT_BUS.addListener(this::registerCommands)
        if (FMLEnvironment.dist.isClient) {
            NeoForge.EVENT_BUS.addListener(this::registerClientCommands)
            CobbledexNeoForgeClient.init(modBus)
        }
    }

    private fun initialize(event: FMLCommonSetupEvent) {
        logInfo("Configured NeoForge integration")
    }

    override fun registerItems() {
        modBus.addListener(this::onRegisterItems)
        modBus.addListener(this::onBuildCreativeModeTab)
    }

    private fun onRegisterItems(event: RegisterEvent) {
        event.register(Registries.ITEM) { helper ->
            helper.register(Identifier.fromNamespaceAndPath(Cobbledex.MOD_ID, "cobbledex_item"), CobbledexConstants.COBBLEDEX_ITEM)
        }
    }

    private fun onBuildCreativeModeTab(event: BuildCreativeModeTabContentsEvent) {
        if (event.tabKey == toolsTabKey) {
            event.accept(ItemStack(CobbledexConstants.COBBLEDEX_ITEM))
        }
    }

    private fun registerCommands(event: RegisterCommandsEvent) {
        CobbledexCommands.registerServer(event.dispatcher, event.buildContext, event.commandSelection)
    }

    private fun registerClientCommands(event: RegisterClientCommandsEvent) {
        try {
            logInfo("Registering NeoForge client commands")
            CobbledexCommands.registerClient(event.dispatcher)
        } catch (exception: Exception) {
            logError("Failed to register Cobbledex client commands!")
            logError(exception.toString())
        }
    }

    override fun environment(): Environment {
        return if (FMLEnvironment.dist.isClient) Environment.CLIENT else Environment.SERVER
    }
}
