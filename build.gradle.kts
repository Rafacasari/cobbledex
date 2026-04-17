plugins {
    id("java")
    id("java-library")
    kotlin("jvm") version ("2.2.20")

    id("dev.architectury.loom") version ("1.11.454") apply false
    id("architectury-plugin") version ("3.4.164") apply false

    id("net.kyori.blossom") version "2.1.0" apply false
    id("org.jetbrains.gradle.plugin.idea-ext") version ("1.1.8") apply false
}

group = "${property("maven_group")}"

allprojects {
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    version = "${property("mod_version")}"
    group = "${property("maven_group")}"

    repositories {
        mavenCentral()
        maven("https://maven.architectury.dev/")
        maven("https://maven.impactdev.net/repository/development/")
        maven("https://maven.neoforged.net/releases/")
        maven ("https://cursemaven.com")
        maven("https://thedarkcolour.github.io/KotlinForForge/")
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }

    kotlin {
        jvmToolchain(21)
    }
}
