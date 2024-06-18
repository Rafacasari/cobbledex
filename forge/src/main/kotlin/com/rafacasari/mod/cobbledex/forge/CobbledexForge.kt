package com.rafacasari.mod.cobbledex.forge

import com.rafacasari.mod.cobbledex.*
import com.rafacasari.mod.cobbledex.commands.CobbledexCommands
import com.rafacasari.mod.cobbledex.utils.logError
import com.rafacasari.mod.cobbledex.utils.logInfo
import net.minecraft.server.MinecraftServer
import net.minecraft.util.Identifier
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RegisterClientCommandsEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.loading.FMLEnvironment
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegisterEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.server.ServerLifecycleHooks

@Mod(Cobbledex.MOD_ID)
class CobbledexForge : CobbledexImplementation {

    private val modBus: IEventBus = FMLJavaModLoadingContext.get().modEventBus
    override val networkManager: INetworkManager = ForgeNetworkManager
    override fun server(): MinecraftServer? = ServerLifecycleHooks.getCurrentServer()

    init {
        //EventBuses.registerModEventBus(Cobbledex.MOD_ID, FMLJavaModLoadingContext.get().modEventBus)

        Cobbledex.preInitialize(this@CobbledexForge)
        MinecraftForge.EVENT_BUS.register(this)
        modBus.addListener(this@CobbledexForge::initialize)

        // In the future we can use this other event to register server-sided commands
        //MinecraftForge.EVENT_BUS.addListener(this@CobbledexForge::registerCommands)
        MinecraftForge.EVENT_BUS.addListener(this@CobbledexForge::registerClientCommands)

        DistExecutor.safeRunWhenOn(Dist.CLIENT) { DistExecutor.SafeRunnable(CobbledexForgeClient::init) }
    }


    @Suppress("UNUSED_PARAMETER")
    private fun initialize(e: FMLCommonSetupEvent)
    {
        logInfo("Registering Forge Network")
        this.networkManager.registerClientBound()
        this.networkManager.registerServerBound()
    }

    override fun registerItems() {
        with(modBus) {
            addListener<RegisterEvent> { event ->
                event.register(ForgeRegistries.Keys.ITEMS) {
                    it.register(Identifier(Cobbledex.MOD_ID, "cobbledex_item"), CobbledexConstants.Cobbledex_Item)
                }
            }
        }
    }

//    private fun registerCommands(e: RegisterCommandsEvent) {
//        CobbledexCommands.registerServer(e.dispatcher, e.buildContext, e.commandSelection)
//    }

    private fun registerClientCommands(e: RegisterClientCommandsEvent) {
        try {
            logInfo("REGISTERING FORGE CLIENT COMMANDS")
            CobbledexCommands.registerClient(e.dispatcher)
        } catch (e: Exception) {
            logError("Failed to register Cobbledex client commands!")
            logError(e.toString())
        }
    }


    override val modAPI: ModAPI = ModAPI.FORGE

    override fun environment(): Environment {
        return if (FMLEnvironment.dist.isClient) Environment.CLIENT else Environment.SERVER
    }
}