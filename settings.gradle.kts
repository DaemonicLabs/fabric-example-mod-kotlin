pluginManagement {
    repositories {
        maven(url="http://maven.fabricmc.net/") {
            name = "Fabric"
        }
        jcenter()
        gradlePluginPortal()
    }
}
rootProject.name = Constants.name