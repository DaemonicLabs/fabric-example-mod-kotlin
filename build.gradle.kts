plugins {
    kotlin("jvm") version Kotlin.version
    idea
    id("fabric-loom") version Fabric.Loom.version
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

base {
    archivesBaseName = Constants.modid
}

group = Constants.group
version = Constants.version

minecraft { }

repositories {
    mavenLocal()
}

dependencies {
    mappings(group = "net.fabricmc", name = "pomf", version = "${Minecraft.version}.${Fabric.Pomf.version}")

    minecraft(group = "com.mojang", name = "minecraft", version = Minecraft.version)

    modCompile(group = "net.fabricmc", name = "fabric-loader", version = "${Minecraft.version}-${Fabric.version}")
    modCompile(group = "net.fabricmc", name = "fabric-language-kotlin", version = Kotlin.version)
    implementation(group = "net.fabricmc", name = "fabric-language-kotlin", version = Kotlin.version){
        // required until modCompile also adds dependencies from fabric-language-kotlin
        exclude(module = "fabric-loader")
    }
}

