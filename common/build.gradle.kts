plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")

    id("net.kyori.blossom")
    id("org.jetbrains.gradle.plugin.idea-ext")
}

architectury {
    common("forge", "fabric")
    platformSetupLoomIde()
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")

    modCompileOnly("org.graalvm.js:js:22.3.0")
    modCompileOnly("com.cobblemon:mod:${property("cobblemon_version")}")

    // alL fabric dependencies:
    modCompileOnly("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")
    modCompileOnly("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")
    implementation(kotlin("reflect"))
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
