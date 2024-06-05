plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

architectury {
    platformSetupLoomIde()
    fabric()
}

configurations {
    create("common")
    create("shadowCommon")
    compileClasspath.get().extendsFrom(configurations["common"])
    runtimeClasspath.get().extendsFrom(configurations["common"])
    getByName("developmentFabric").extendsFrom(configurations["common"])
}

loom {
    enableTransitiveAccessWideners.set(true)
    silentMojangMappingsLicense()


}

dependencies {
    minecraft("net.minecraft:minecraft:${property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
    modApi("dev.architectury:architectury-fabric:${property("architectury_version")}")
    modApi("teamreborn:energy:3.0.0")

    modImplementation("com.cobblemon:fabric:${property("cobblemon_version")}")
    modImplementation("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")
    modApi("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")


//    modImplementation(fabricApi.module("fabric-command-api-v2", "0.89.3+1.20.1"))


    "common"(project(":common", "namedElements")) { isTransitive = false }
    "shadowCommon"(project(":common", "transformProductionFabric")) { isTransitive = false }





}

tasks.processResources {
    expand(mapOf(
            "mod_name" to project.property("mod_name"),
            "mod_id" to project.property("mod_id"),
            "mod_version" to project.property("mod_version"),
            "mod_description" to project.property("mod_description"),
            "repository" to project.property("repository"),
            "license" to project.property("license"),
            "mod_icon" to project.property("mod_icon"),
            "environment" to project.property("environment"),
            "supported_minecraft_versions" to project.property("supported_minecraft_versions")
    ))
}


tasks {
    base.archivesName.set("${project.property("archives_base_name")}-fabric")
    processResources {
        inputs.property("version", project.version)

        filesMatching("META-INF/mods.toml") {
            expand(mapOf("version" to project.version))
        }
    }

    shadowJar {
        exclude("generations/gg/generations/core/generationscore/fabric/datagen/**")
        exclude("data/forge/**")
        configurations = listOf(project.configurations.getByName("shadowCommon"))
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        injectAccessWidener.set(true)
        inputFile.set(shadowJar.get().archiveFile)
        dependsOn(shadowJar)
    }

    jar.get().archiveClassifier.set("dev")
}
