plugins {
    java
    application
    `jvm-test-suite`
    kotlin("jvm")
    kotlin("kapt")
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
val openapiVersion: String by project
val exposedVersion: String by project
val testcontainersVersion: String by project
dependencies {

    // annotation processor
    kapt("io.javalin.community.openapi:openapi-annotation-processor:$openapiVersion")

    implementation(project(":domain"))

    implementation("io.javalin:javalin-bundle:${javalinVersion}")
    implementation("io.javalin.community.openapi:javalin-openapi-plugin:${openapiVersion}")
    implementation("io.javalin.community.openapi:javalin-swagger-plugin:${openapiVersion}")
    implementation("redis.clients:jedis:4.3.1")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

    runtimeOnly("org.postgresql:postgresql:42.6.0")

    testImplementation(project(":domain-test"))
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.assertj:assertj-core:3.11.1")
    testImplementation("org.testcontainers:testcontainers:$testcontainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testcontainersVersion")
    testImplementation("org.testcontainers:postgresql:$testcontainersVersion")

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
                        mustRunAfter(test)
                        shouldRunAfter(startContainerRedis, startContainerPostgres)
                        outputs.upToDateWhen { false }
                    }
                }
            }
        }
    }
}

val integrationTest = testing.suites.named("integrationTest")

val integrationTestRedis = tasks.register("integrationTestRedis") {
    dependsOn(startContainerRedis, integrationTest)
}

// integration test with clean up
val integrationTestCleanRedis = tasks.register("integrationTestCleanRedis") {
    dependsOn(integrationTestRedis, shutDownContainerRedis)
}

val check = tasks.named("check") {
    dependsOn(integrationTestCleanRedis, integrationTestCleanPostgres)
}

tasks.named("build") {
    dependsOn(removeImage)
}

tasks.register("installRedis", Exec::class.java) {
    commandLine("sh", "-c", "docker run --name todo-redis -d -p 6379:6379 redis:alpine")
}

tasks.register("startRedis", Exec::class.java) {
    commandLine("sh", "-c", "docker start todo-redis")
}

val buildImage = tasks.register("buildImage", Exec::class.java) {
    dependsOn("installDist")
    commandLine("sh", "-c", "docker build -t kotlin-todo .")
}

val removeImage = tasks.register("removeImage", Exec::class.java) {
    commandLine("sh", "-c", "docker rmi kotlin-todo")
    mustRunAfter(check)
}

val startContainerRedis = tasks.register("startContainerRedis", Exec::class.java) {
    dependsOn(buildImage)
    commandLine("sh", "-c", "docker compose -f docker-compose.yml -f docker-compose.redis.yml up -d")
}

val shutDownContainerRedis = tasks.register("shutDownContainerRedis", Exec::class.java) {
    commandLine("sh", "-c", "docker compose -f docker-compose.yml -f docker-compose.redis.yml down")
    mustRunAfter(integrationTestRedis)
}

val startContainerPostgres = tasks.register("startContainerPostgres", Exec::class.java) {
    dependsOn(buildImage)
    mustRunAfter(integrationTestCleanRedis)
    commandLine("sh", "-c", "docker compose -f docker-compose.yml -f docker-compose.postgres.yml up -d")
}

val shutDownContainerPostgres = tasks.register("shutDownContainerPostgres", Exec::class.java) {
    mustRunAfter(integrationTestPostgres)
    commandLine("sh", "-c", "docker compose -f docker-compose.yml -f docker-compose.postgres.yml down")
}

val integrationTestPostgres = tasks.register("integrationTestPostgres") {
    dependsOn(startContainerPostgres, integrationTest)
}

// integration test with clean up
val integrationTestCleanPostgres = tasks.register("integrationTestCleanPostgres") {
    dependsOn(integrationTestPostgres, shutDownContainerPostgres)
}
