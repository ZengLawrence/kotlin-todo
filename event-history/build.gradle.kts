plugins {
    java
    application
    kotlin("jvm")
}

group = "zeng.lawrence"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt")
}

val javalinVersion: String by project
dependencies {

    implementation("io.javalin:javalin:${javalinVersion}")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")
    runtimeOnly("org.slf4j:slf4j-simple:2.0.7")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.assertj:assertj-core:3.11.1")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.0.0")

}
