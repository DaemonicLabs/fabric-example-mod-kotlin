object Minecraft {
    const val version = "1.14"
}

object Fabric {
    object Loader {
        const val version = "0.4.+"
    }
    object API {
        const val version = "0.2.7+build.+"
    }
    object Loom {
        const val version = "0.2.1-SNAPSHOT"
    }
    object Yarn {
        const val version = "${Minecraft.version}+build.+"
    }
    object LanguageKotlin {
        const val version = Kotlin.version + "+build.+"
    }
}

object Kotlin {
    const val version = "1.3.30"
}

object Cursegradle {
    const val version = "1.1.2"
}