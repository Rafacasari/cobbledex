package rafacasari.cobbledex.forge

import net.minecraft.util.Identifier
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.loading.FMLEnvironment
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegisterEvent
import rafacasari.cobbledex.*
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext

@Mod(Cobbledex.MOD_ID)
class CobbledexForge : CobbledexImplementation {

    private val modBus: IEventBus = FMLJavaModLoadingContext.get().modEventBus

    init {
        //EventBuses.registerModEventBus(Cobbledex.MOD_ID, FMLJavaModLoadingContext.get().modEventBus)

        Cobbledex.init(this@CobbledexForge)
        MinecraftForge.EVENT_BUS.register(this)
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

    override val modAPI: ModAPI = ModAPI.FORGE

    override fun environment(): Environment {
        return if (FMLEnvironment.dist.isClient) Environment.CLIENT else Environment.SERVER
    }
}