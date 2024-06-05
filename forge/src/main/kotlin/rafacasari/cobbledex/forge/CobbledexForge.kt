package rafacasari.cobbledex.forge

import dev.architectury.platform.forge.EventBuses
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.fml.loading.FMLEnvironment
import rafacasari.cobbledex.CobbledexImplementation
import rafacasari.cobbledex.Cobbledex
import rafacasari.cobbledex.Cobbledex.init
import rafacasari.cobbledex.Environment
import rafacasari.cobbledex.ModAPI

@Mod(Cobbledex.MOD_ID)
class CobbledexForge : CobbledexImplementation {

    init {
        EventBuses.registerModEventBus(Cobbledex.MOD_ID, FMLJavaModLoadingContext.get().modEventBus)
        init(this)
    }


    override val modAPI: ModAPI = ModAPI.FORGE


    override fun environment(): Environment {
        return if (FMLEnvironment.dist.isClient) Environment.CLIENT else Environment.SERVER
    }


}