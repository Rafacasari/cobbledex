package rafacasari.cobbledex.fabric

import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import rafacasari.cobbledex.*
import rafacasari.cobbledex.Cobbledex.init


class CobbledexFabric : ModInitializer, CobbledexImplementation {
    override fun onInitialize() {
        init(this)
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