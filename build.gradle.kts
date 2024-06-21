plugins {
    id("java")
    id("java-library")
    kotlin("jvm") version ("1.9.22")

    id("dev.architectury.loom") version ("1.6-SNAPSHOT") apply false
    id("architectury-plugin") version ("3.4-SNAPSHOT") apply false

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
        maven("https://maven.impactdev.net/repository/development/")
        maven ("https://cursemaven.com")
        maven("https://thedarkcolour.github.io/KotlinForForge/")
    }
}