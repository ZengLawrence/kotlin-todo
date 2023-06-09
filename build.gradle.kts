plugins {
    java
    application
    `jvm-test-suite`
    id("org.jetbrains.kotlin.jvm") version "1.8.0"
    id("org.jetbrains.kotlin.kapt") version "1.8.21"
}

group = "zeng.lawrence"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

val javalinVersion by extra("5.5.0")
val openapiVersion by extra("5.4.2")

repositories {
    mavenCentral()
}

val exposedVersion: String by project
dependencies {

    // annotation processor
    kapt("io.javalin.community.openapi:openapi-annotation-processor:$openapiVersion")

    implementation("io.javalin:javalin-bundle:${javalinVersion}")
    implementation("io.javalin.community.openapi:javalin-openapi-plugin:${openapiVersion}")
    implementation("io.javalin.community.openapi:javalin-swagger-plugin:${openapiVersion}")
    implementation("redis.clients:jedis:4.3.1")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

    runtimeOnly("org.postgresql:postgresql:42.6.0")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.assertj:assertj-core:3.11.1")
    testImplementation("org.testcontainers:testcontainers:1.18.1")
    testImplementation("org.testcontainers:junit-jupiter:1.18.1")
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }

        register<JvmTestSuite>("integrationTest") {
            dependencies {
                implementation(project())
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
                        shouldRunAfter(test)
                        dependsOn(startContainer)
                    }
                }
            }
        }
    }
}

val integrationTest = testing.suites.named("integrationTest")

// integration test with clean up
val integrationTestClean = tasks.register("integrationTestClean") {
    dependsOn(integrationTest, shutDownContainer, removeImage)
}

tasks.named("check") {
    dependsOn(integrationTestClean)
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}

tasks.register("installRedis", Exec::class.java) {
    commandLine("sh", "-c", "docker run --name todo-redis -d -p 6379:6379 redis:alpine")
}

tasks.register("startRedis", Exec::class.java) {
    commandLine("sh", "-c", "docker start todo-redis")
}

tasks.register("buildImage", Exec::class.java) {
    dependsOn("installDist")
    commandLine("sh", "-c", "docker build -t kotlin-todo .")
}

val removeImage = tasks.register("removeImage", Exec::class.java) {
    commandLine("sh", "-c", "docker rmi kotlin-todo")
    mustRunAfter(shutDownContainer)
}

val startContainer = tasks.register("startContainer", Exec::class.java) {
    dependsOn("buildImage")
    commandLine("sh", "-c", "docker compose up -d")
}

val shutDownContainer = tasks.register("shutDownContainer", Exec::class.java) {
    commandLine("sh", "-c", "docker compose down")
    mustRunAfter(integrationTest)
}