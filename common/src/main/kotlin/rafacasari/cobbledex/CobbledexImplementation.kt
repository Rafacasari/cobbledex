package rafacasari.cobbledex

//import net.minecraft.world.biome.Biome

interface CobbledexImplementation {
    val modAPI: ModAPI
    fun environment(): Environment

//    fun getAllRegisteredBiomes() : List<Biome>
}

enum class ModAPI {
    FABRIC,
    FORGE
}

enum class Environment {
    CLIENT,
    SERVER
}
