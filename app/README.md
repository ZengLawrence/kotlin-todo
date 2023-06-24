# kotlin-todo
Todo list REST service using Kotlin.

# Starting Application
## Pre-requisites

1. Docker is installed and running
2. Install Redis by running Gradle task `./gradlew installRedis`. 

## Running application
1. If Redis is stopped, run task `./gradlew startRedis` first.
2. Run `./gradlew run`

# Docker image
## Build image
Run `./gradlew buildImage` to build image. Docker does not overwrite image each time it is built. See [Remove image](#remove-image) section below.

## Start container
Run `./gradlew startContainer` to start container with Redis service as database.

## Shut down container
Run `./gradlew shutDownContainer` to stop and remove container.

## Remove image
Run `./gradlew removeImage` to remove image.
