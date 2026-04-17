plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")

    id("net.kyori.blossom")
    id("org.jetbrains.gradle.plugin.idea-ext")
}

architectury {
    common("fabric", "neoforge")
    platformSetupLoomIde()
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings(loom.officialMojangMappings())

    implementation(kotlin("reflect"))

    modCompileOnly("com.cobblemon:mod:${property("cobblemon_version")}") { isTransitive = false }

    // alL fabric dependencies:
    modCompileOnly("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")
    modCompileOnly("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")
}

sourceSets {
    main {
        blossom {
            kotlinSources  {
                property("version", project.version.toString())
            }
        }
    }
}
