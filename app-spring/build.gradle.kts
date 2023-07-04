import  java.net.http.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile // For `KotlinCompile` task below
import java.net.URI

plugins {
    id("org.springframework.boot") version "3.1.1"
    id("io.spring.dependency-management") version "1.1.0"
    application
    java
    `jvm-test-suite`
    kotlin("jvm")
    kotlin("plugin.spring") // The Kotlin Spring plugin
}

group = "zeng.lawrence"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

application {
    mainClass.set("spring.SpringApplicationKt")
}

repositories {
    mavenCentral()
}

val testcontainersVersion: String by project
dependencies {

    implementation(project(":domain"))

    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin") // Jackson extensions for Kotlin for working with JSON
    implementation("org.jetbrains.kotlin:kotlin-reflect") // Kotlin reflection library, required for working with Spring
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8") // Kotlin standard library

    runtimeOnly("org.postgresql:postgresql:42.6.0")

    testImplementation(project(":domain-test"))
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

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }

        register<JvmTestSuite>("integrationTest") {
            dependencies {
                implementation(project())
                implementation(project(":app-test"))
                implementation("com.intuit.karate:karate-junit5:1.4.0")
            }

            sources {
                resources {
                    srcDir(file("src/integrationTest/kotlin"))
                    exclude("**/*.kt")
                }
            }

            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test, startContainer)
                        outputs.upToDateWhen { false }
                    }
                }
            }
        }
    }
}

val integrationTest = testing.suites.named("integrationTest")

val integrationTestClean = tasks.register("integrationTestClean") {
    dependsOn(startContainer, integrationTest, shutDownContainer)
}

val check = tasks.named("check") {
    dependsOn(integrationTestClean)
}

val buildImage = tasks.register("buildImage", Exec::class.java) {
    dependsOn("installDist")
    commandLine("sh", "-c", "docker build -t kotlin-todo-spring .")
}

val removeImage = tasks.register("removeImage", Exec::class.java) {
    commandLine("sh", "-c", "docker rmi kotlin-todo-spring")
    mustRunAfter(shutDownContainer)
}

val startContainer = tasks.register("startContainer", Exec::class.java) {
    dependsOn(buildImage)
    commandLine("sh", "-c", "docker compose -f docker-compose.yml up -d")
    doLast {
        repeatHealthCheck(30)
    }
}

val shutDownContainer = tasks.register("shutDownContainer", Exec::class.java) {
    mustRunAfter(integrationTest)
    commandLine("sh", "-c", "docker compose -f docker-compose.yml down")
}

val build = tasks.named("build") {
    dependsOn(removeImage)
}

fun healthCheck(): Boolean {
    val request = HttpRequest.newBuilder()
        .uri(URI("http://localhost:8080/actuator/health"))
        .GET()
        .build()
    return try {
        HttpClient.newHttpClient()
            .send(request, HttpResponse.BodyHandlers.ofString())
            .statusCode() == 200
    } catch(_: Exception) {
        false
    }
}

fun repeatHealthCheck(times: Int) {
    var res = healthCheck()
    var remaining = times - 1
    while (!res && remaining > 0) {
        println("App fails to start or still starting after ${times - remaining} second")
        remaining--
        Thread.sleep(1000) // 1 second
        res = healthCheck()
    }
    if (res) {
        println("App started")
    } else {
        println("App fails to start after $times seconds")
        throw IllegalStateException("App fails to start")
    }
}
