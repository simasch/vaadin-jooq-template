# Vaadin/jOOQ Template

## Introduction

This is a template project that shows how to integrate [Vaadin](https://vaadin.com) and [jOOQ](https://jooq.org) 
and how to test it with [Karibu Testing](https://github.com/mvysny/karibu-testing) and [Playwright](https://playwright.dev).

It uses [Testcontainers](https://testcontainers.com) for generating the jOOQ classes and integration testing and 
Flyway for the database migrations.

This project can be used as a starting point to create your own Vaadin application with jOOQ.
It contains all the necessary configuration and some examples to get you started.

## Running the Application

Before running the application, the jOOQ metamodel has to be generated using the Maven plugin:

    ./mvnw compile

Then you can simply run the application with a database started by Testcontainers from your IDE using the `TestVjApplication`.

**Important:**
This class uses the [Spring Boot Testcontainers support](https://spring.io/blog/2023/06/23/improved-testcontainers-support-in-spring-boot-3-1/), introduced with Spring Boot 3.1.
Thus, [Docker](https://www.docker.com) or [Testcontainers Cloud](https://testcontainers.com/cloud/) must be running on your local computer.

## Testing the Application

There are two base classes:

- `KaribuTest` can be used for fast [browser-less testing](https://mvysny.github.io/browserless-web-testing/), aka UI unit test. Karibu sets up a Vaadin mock environment.
- `PlaywrightIT` configures Playwright for E2E tests. This class uses SpringBootTest at a random port.

The Playwright test uses [Mopo](https://github.com/viritin/mopo),
which simplifies the testing of Vaadin applications with Playwright.

## Deploying to Production

To create a production build, call `mvnw clean package -Pproduction` (Windows),
or `./mvnw clean package -Pproduction` (Mac & Linux).

This will build a JAR file with all the dependencies and front-end resources,
ready to be deployed. You can find the file in the `target` folder after the build completes.

Once the JAR file is built, you can run it using
`java -jar target/vaadin-jooq-template-<version>.jar`

## Project structure

- `layout/MainLayout.java` in `src/main/java` contains the navigation setup using [App Layout](https://vaadin.com/docs/components/app-layout).
- `views` package in `src/main/java` contains the server-side Java views of your application.
- `themes` folder in `frontend/` contains the custom CSS styles.

## Security Configuration

The example uses JWT authentication for a better developer experience (for example, you don't have to re-login during development).
Read more in the blog of [Matti Tahvonen](https://vaadin.com/blog/jwt-authentication-with-vaadin-flow-for-better-developer-and-user-experience).

## Useful links

### Vaadin

- Check out the [Vaadin Developer Portal](https://vaadin.com/developers).
- Read the documentation at [vaadin.com/docs](https://vaadin.com/docs).
- Create new projects at [start.vaadin.com](https://start.vaadin.com/).
- Find a collection of solutions to common use cases at [cookbook.vaadin.com](https://cookbook.vaadin.com/).
- Find add-ons at [vaadin.com/directory](https://vaadin.com/directory).

### jOOQ

- Read the documentation at [jooq.org/learn](https://www.jooq.org/learn/).
- Browse the [Blog](https://blog.jooq.org).

### Spring Boot

- Explore the [Spring Boot project page](https://spring.io/projects/spring-boot/).

### Testcontainers

- Go to the [Testcontainers website](https://testcontainers.com).

### Karibu Testing

- Check out the [GitHub project](https://github.com/mvysny/karibu-testing).

### Playwright

- Read the [documentation](https://playwright.dev).
