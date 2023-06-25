plugins {
    `java-library`
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

dependencies {
    api("io.arrow-kt:arrow-core:1.2.0-RC")
}