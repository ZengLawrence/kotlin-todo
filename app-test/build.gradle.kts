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

sourceSets {
    main {
        resources {
            srcDir(file("src/main/kotlin"))
            exclude("**/*.kt")
        }
    }
}

dependencies {
    implementation("com.intuit.karate:karate-junit5:1.4.0")
}

