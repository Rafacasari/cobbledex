plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
}

architectury {
    common("forge", "fabric")
    platformSetupLoomIde()
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
    modCompileOnly("com.cobblemon:mod:${property("cobblemon_version")}")


    // alL fabric dependencies:
    modCompileOnly("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")
    modCompileOnly("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")
    modCompileOnly("dev.architectury:architectury-fabric:${property("architectury_version")}")


}
