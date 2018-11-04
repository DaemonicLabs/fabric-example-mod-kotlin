import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import net.fabricmc.loom.task.SetupTask

plugins {
    kotlin("jvm") version "1.3.0"
    idea
    id("fabric-loom") version "0.0.12-SNAPSHOT"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

base {
    archivesBaseName = "modid"
}

version = "1.0.0"

minecraft {
    // You can find the latest versions on https://fabric.asie.pl/use/.
    version = "18w44a"
    pomfVersion = "27"
    fabricVersion = "0.1.0.42"

    // Optional. Remove if you're not using Mixins.
    refmapName = "modid.refmap.json"
}

repositories {
    mavenLocal()
}


val jarFIle =(gradle.startParameter.projectCacheDir ?: file(".gradle"))
    .resolve("minecraft").resolve("remapped_mods").resolve("fabric-loader-18w44a-0.1.0.42-mapped-27.jar")
println("lib: $jarFIle")

dependencies {
    implementation("net.fabricmc:fabric-language-kotlin:1.3.0")
    compile(files(jarFIle))
}

val setup by tasks.getting(SetupTask::class)

val compileKotlin by tasks.getting(KotlinCompile::class) {
    dependsOn(setup)
}
