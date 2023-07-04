# app
REST service using plain Kotlin libraries.

# Starting Application
## Pre-requisites

1. Docker is installed and running
2. Install Redis by running Gradle task `gradlew app:installRedis`. 

## Running application
1. If Redis is stopped, run task `gradlew app:startRedis` first.
2. Run `gradlew app:run`

# Docker image
## Build image
Run `gradlew app:buildImage` to build image. Docker does not overwrite image each time it is built. See [Remove image](#remove-image) section below.

## Start container
Run `gradlew app:startContainer` to start container with Redis service as database.

## Shut down container
Run `gradlew app:shutDownContainer` to stop and remove container.

## Remove image
Run `gradlew app:removeImage` to remove image.
