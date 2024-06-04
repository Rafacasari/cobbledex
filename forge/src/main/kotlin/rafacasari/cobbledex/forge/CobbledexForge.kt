package rafacasari.cobbledex.forge

import dev.architectury.platform.forge.EventBuses
import net.minecraft.util.Identifier
import net.minecraftforge.common.CreativeModeTabRegistry
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegisterEvent
import rafacasari.cobbledex.CobbledexConstants
import rafacasari.cobbledex.CobbledexMod
import rafacasari.cobbledex.CobbledexMod.init

@Mod(CobbledexMod.MOD_ID)
class CobbledexForge {
    init {
        EventBuses.registerModEventBus(CobbledexMod.MOD_ID, FMLJavaModLoadingContext.get().modEventBus)
        init()
    }

    //    @SubscribeEvent
    //    public static void onItemRegistryEvent(RegisterEvent event) {
    //        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "poke_ball_icon"), new Item(new Item.Settings()));
    //    }

}