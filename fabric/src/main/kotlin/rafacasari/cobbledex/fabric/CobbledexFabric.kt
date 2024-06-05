package rafacasari.cobbledex.fabric

import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import rafacasari.cobbledex.Cobbledex.init
import rafacasari.cobbledex.CobbledexImplementation
import rafacasari.cobbledex.Environment
import rafacasari.cobbledex.ModAPI


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



}