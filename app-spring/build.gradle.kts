import org.jetbrains.kotlin.gradle.tasks.KotlinCompile // For `KotlinCompile` task below

plugins {
    id("org.springframework.boot") version "3.1.1"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm")
    kotlin("plugin.spring") // The Kotlin Spring plugin
}

group = "zeng.lawrence"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

val testcontainersVersion: String by project
dependencies {

    implementation(project(":domain"))

    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin") // Jackson extensions for Kotlin for working with JSON
    implementation("org.jetbrains.kotlin:kotlin-reflect") // Kotlin reflection library, required for working with Spring
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8") // Kotlin standard library

    runtimeOnly("org.postgresql:postgresql:42.6.0")
    //runtimeOnly("com.h2database:h2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.assertj:assertj-core:3.11.1")
    testImplementation("org.testcontainers:testcontainers:$testcontainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testcontainersVersion")
    testImplementation("org.testcontainers:postgresql:$testcontainersVersion")
}

tasks.withType<KotlinCompile> { // Settings for `KotlinCompile` tasks
    kotlinOptions { // Kotlin compiler options
        freeCompilerArgs = listOf("-Xjsr305=strict") // `-Xjsr305=strict` enables the strict mode for JSR-305 annotations
        jvmTarget = "17" // This option specifies the target version of the generated JVM bytecode
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}