import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.matthewprenger.cursegradle.CurseArtifact
import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseUploadTask
import com.matthewprenger.cursegradle.Options
import net.fabricmc.loom.task.RemapJar

plugins {
    kotlin("jvm") version Kotlin.version
    id("fabric-loom") version Fabric.Loom.version
    id("com.github.johnrengelman.shadow") version "4.0.4"
    id("com.matthewprenger.cursegradle") version Cursegradle.version
    `maven-publish`
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

minecraft {
    // loom 0.3.0 prep
//    version = Minecraft.version
//    setMappings("${Minecraft.version}.${Fabric.Yarn.version}")
}

repositories {
    //    mavenLocal()
    maven(url = "http://maven.fabricmc.net/") {
        name = "Fabric"
    }
    maven(url = "https://kotlin.bintray.com/kotlinx") {
        name = "Kotlinx"
    }
    mavenCentral()
    jcenter()
}

configurations.modCompile.extendsFrom(configurations.shadow)
configurations.modCompile.extendsFrom(configurations.include)
//configurations.compileOnly.extendsFrom(configurations.include)
// required until modCompile also adds to compileOnly
configurations.compileOnly.extendsFrom(configurations.modCompile)
//configurations.compileOnly.extendsFrom(configurations.shadow)

dependencies {
    minecraft(group = "com.mojang", name = "minecraft", version = Minecraft.version)
    mappings(group = "net.fabricmc", name = "yarn", version = Fabric.Yarn.version)

    modCompile(group = "net.fabricmc", name = "fabric-loader", version = Fabric.Loader.version)
//    compileOnly(group = "net.fabricmc", name = "fabric-loader", version = Fabric.Loader.version)

    modCompile(group = "net.fabricmc", name = "fabric", version = Fabric.API.version)

    modCompile(group = "net.fabricmc", name = "fabric-language-kotlin", version = Fabric.LanguageKotlin.version)

    shadow(group = "blue.endless", name = "jankson", version = "1.1.1")
}

tasks.getByName<ProcessResources>("processResources") {
    filesMatching("fabric.mod.json") {
        expand(
            mutableMapOf(
                "modid" to Constants.modid,
                "version" to Constants.version,
                "kotlinVersion" to Kotlin.version,
                "fabricApiVersion" to Fabric.API.version
            )
        )
    }
}

val sourcesJar = tasks.create<Jar>("sourcesJar") {
    classifier = "sources"
    from(sourceSets["main"].allSource)
}

// SHADOWJAR

val shadowJar by tasks.getting(ShadowJar::class) {
    classifier = ""
    configurations = listOf(
        project.configurations.shadow
    )
    exclude("META-INF")
}

val remapJar = tasks.getByName<RemapJar>("remapJar") {
    (this as Task)
    dependsOn(shadowJar)
    mustRunAfter(shadowJar)
    jar = shadowJar.archivePath
}

// MAVEN-PUBLISH

fun MavenPublication.includeComponents() {
    pom.withXml {
        val dependenciesNode = asNode().appendNode("dependencies")

        project.configurations.include.allDependencies.forEach {
            if (it !is SelfResolvingDependency) {
                val dependencyNode = dependenciesNode.appendNode("dependency")
                dependencyNode.appendNode("groupId", it.group)
                dependencyNode.appendNode("artifactId", it.name)
                dependencyNode.appendNode("version", it.version)
                dependencyNode.appendNode("scope", "runtime") // or compileOnly
            }
        }
        project.configurations.shadow.allDependencies.forEach {
            if (it !is SelfResolvingDependency) {
                val dependencyNode = dependenciesNode.appendNode("dependency")
                dependencyNode.appendNode("groupId", it.group)
                dependencyNode.appendNode("artifactId", it.name)
                dependencyNode.appendNode("version", it.version)
                dependencyNode.appendNode("scope", "runtime") // or compileOnly
            }
        }
    }
}

publishing {
    publications {
        create("main", MavenPublication::class.java) {

            groupId = project.group.toString()
            artifactId = project.name.toLowerCase()
            version = project.version.toString()

            artifact(remapJar.jar) {
                builtBy(remapJar)
            }
            artifact(sourcesJar)

            includeComponents()
        }
    }
    repositories {
        // add mavne upload tarets here
        // sample for modmuss50/fabric maven
//        val mavenPass: String? = project.properties["mavenPass"] as String?
//        mavenPass?.let {
//            maven(url = "http://mavenupload.modmuss50.me/") {
//                credentials {
//                    username = "buildslave"
//                    password = mavenPass
//                }
//            }
//        }
    }
}

// CURSEFORGE

val curse_api_key: String? by project
if (curse_api_key != null && project.hasProperty("release")) {
    val CURSEFORGE_RELEASE_TYPE: String by project
    val CURSEFORGE_ID: String by project
    curseforge {
        options(closureOf<Options> {
            forgeGradleIntegration = false
        })
        apiKey = curse_api_key
        project(closureOf<CurseProject> {
            id = CURSEFORGE_ID
            releaseType = CURSEFORGE_RELEASE_TYPE
            addGameVersion("1.14")

            val changelog_file: String? by project
            if (changelog_file != null) {
                println("changelog = $changelog_file")
                changelogType = "markdown"
                changelog = file(changelog_file as String)
            }
            mainArtifact(remapJar.jar, closureOf<CurseArtifact> {
                displayName = "Fill me in $version"
            })
        })
    }
    project.afterEvaluate {
        tasks.getByName<CurseUploadTask>("curseforge${CURSEFORGE_ID}") {
            dependsOn(remapJar)
        }
    }
}