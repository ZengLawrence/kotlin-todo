rootProject.name = "kotlin-todo"
include("app", "domain")

pluginManagement {
    val kotlinPluginVersion: String by settings
    plugins {
        kotlin("jvm") version kotlinPluginVersion
        kotlin("kapt") version kotlinPluginVersion
    }
}