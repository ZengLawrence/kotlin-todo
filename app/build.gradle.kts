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
                        outputs.upToDateWhen { false }
                    }
                }
            }
        }
    }
}

// build Docker image
tasks {

    register("buildImage", Exec::class.java) {
        group = "Integration Test"
        dependsOn(installDist)
        commandLine("sh", "-c", "docker build -t kotlin-todo .")
    }

    register("removeImage", Exec::class.java) {
        group = "Integration Test"
        commandLine("sh", "-c", "docker rmi kotlin-todo")
        mustRunAfter(check)
    }

}
val buildImage = tasks.named("buildImage")
val removeImage = tasks.named("removeImage")

// integration tests using Redis
tasks {

    val groupName = "integration test"
    val startContainerRedis = register("startContainerRedis", Exec::class.java) {
        group = groupName
        dependsOn(buildImage)
        commandLine("sh", "-c", "docker compose -f docker-compose.yml -f docker-compose.redis.yml up -d")
    }

    val integrationTestBaseRedis = register("integrationTestBaseRedis") {
        group = groupName
        dependsOn(testing.suites.named("integrationTest"))
        mustRunAfter(startContainerRedis)
    }

    val shutDownContainerRedis = register("shutDownContainerRedis", Exec::class.java) {
        group = groupName
        commandLine("sh", "-c", "docker compose -f docker-compose.yml -f docker-compose.redis.yml down")
        mustRunAfter(integrationTestBaseRedis)
    }

    // integration test with clean up
    register("integrationTestRedis") {
        group = groupName
        dependsOn(startContainerRedis, integrationTestBaseRedis, shutDownContainerRedis)
    }

}
val integrationTestRedis = tasks.named("integrationTestRedis")

// integration tests using Postgres
tasks {

    val groupName = "integration test"
    val startContainerPostgres = register("startContainerPostgres", Exec::class.java) {
        group = groupName
        dependsOn(buildImage)
        mustRunAfter(integrationTestRedis)
        commandLine("sh", "-c", "docker compose -f docker-compose.yml -f docker-compose.postgres.yml up -d")
    }

    val integrationTestBasePostgres = register("integrationTestBasePostgres") {
        group = groupName
        mustRunAfter(startContainerPostgres)
        dependsOn(testing.suites.named("integrationTest"))
    }

    val shutDownContainerPostgres = register("shutDownContainerPostgres", Exec::class.java) {
        group = groupName
        mustRunAfter(integrationTestBasePostgres)
        commandLine("sh", "-c", "docker compose -f docker-compose.yml -f docker-compose.postgres.yml down")
    }

    // integration test with clean up
    register("integrationTestPostgres") {
        group = groupName
        dependsOn(startContainerPostgres, integrationTestBasePostgres, shutDownContainerPostgres)
    }

}
val integrationTestPostgres = tasks.named("integrationTestPostgres")

tasks {

    check {
        dependsOn(integrationTestRedis, integrationTestPostgres)
    }

    build {
        dependsOn(removeImage)
    }

}

// dev tools tasks
tasks {

    val groupName = "dev tool"
    register("installRedis", Exec::class.java) {
        group = groupName
        commandLine("sh", "-c", "docker run --name todo-redis -d -p 6379:6379 redis:alpine")
    }

    register("startRedis", Exec::class.java) {
        group = groupName
        commandLine("sh", "-c", "docker start todo-redis")
    }

}