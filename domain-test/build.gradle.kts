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

    implementation(project(":domain"))

    implementation("org.jetbrains.kotlin:kotlin-test")
    implementation("org.jetbrains.kotlin:kotlin-test-junit5")
    implementation("org.assertj:assertj-core:3.11.1")
}

