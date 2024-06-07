package com.rafacasari.mod.cobbledex

//import net.minecraft.world.biome.Biome

interface CobbledexImplementation {
    val modAPI: ModAPI
    fun environment(): Environment
    fun registerItems()
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
