# kotlin-todo
Todo list REST service using Kotlin.

# Starting Application
## Pre-requisites

1. Docker is installed and running
2. Install Redis by running Gradle task `./gradlew installRedis`. 

## Running application
1. If Redis is stopped, run task `./gradlew startRedis` first.
2. Run `./gradlew run`

# Docker Image
1. Run `./gradlew buildImage` to build image