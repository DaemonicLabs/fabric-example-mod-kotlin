plugins {
    kotlin("jvm") version "1.3.0"
    idea
    id ("fabric-loom") version "0.0.12-SNAPSHOT"
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
    fabricVersion = "0.1.0.40"

    // Optional. Remove if you're not using Mixins.
    refmapName = "modid.refmap.json"
}

repositories {
    mavenLocal()
    jcenter()
    maven(url="http://maven.modmuss50.me/") {
        name = "Fabric"
    }
}

dependencies {
    implementation("net.fabricmc:fabric-language-kotlin:1.3.0")
}