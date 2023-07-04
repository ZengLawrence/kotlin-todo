# kotlin-todo
Todo list REST service using Kotlin.

# Pre-requisites
Docker is installed and running

# Project structure
This project architecture follows Robert Martin's [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html). Modules are listed below from inner concentric circle to outer. 

- `\domain`: business rules
- `\app`: REST service using plain Kotlin libraries
- `\app-spring`: REST service using Spring framework

The ones with 'test' suffix are for the purpose of sharing test suites, so technically they are not parts of the architecture but parts of the build project.

- `\domain-test`: test suite for interface implementations
- `\app-test`: test suite for REST service implementation