rootProject.name = "kotlin-todo"
include("app", "domain", "domain-test", "app-spring", "app-test", "event-history")

pluginManagement {
    val kotlinPluginVersion: String by settings
    plugins {
        kotlin("jvm") version kotlinPluginVersion
        kotlin("kapt") version kotlinPluginVersion
        kotlin("plugin.spring") version kotlinPluginVersion
    }
}