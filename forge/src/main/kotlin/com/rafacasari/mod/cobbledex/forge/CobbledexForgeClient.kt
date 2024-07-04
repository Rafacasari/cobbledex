package com.rafacasari.mod.cobbledex.forge
import com.rafacasari.mod.cobbledex.utils.MiscUtils.cobbledexResource
import com.rafacasari.mod.cobbledex.utils.MiscUtils.logInfo
import net.minecraft.client.util.ModelIdentifier
import net.minecraftforge.client.event.ModelEvent
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext

object CobbledexForgeClient {

    private val modBus: IEventBus = FMLJavaModLoadingContext.get().modEventBus

    fun init() {
        modBus.addListener(::register2dModel)
    }

    private fun register2dModel(event: ModelEvent.RegisterAdditional) {
        logInfo("Registering client resources")
        event.register(ModelIdentifier(cobbledexResource("cobbledex_icon"), "inventory"))
    }
}