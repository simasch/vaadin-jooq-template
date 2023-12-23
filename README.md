# Vaadin/jOOQ Template

Template Project that shows how to integrate Vaadin and jOOQ.

It uses Testcontainers, for generating the jOOQ classes and integration testing and Flyway for the database migrations.  

This project can be used as a starting point to create your own Vaadin application with jOOQ.
It contains all the necessary configuration and some examples to get you started.

## Running the application

To run the application with a database started by Testcontainers simply start from your IDE `TestVjApplication`. 

***Important:***
This class uses the [Spring Boot Testcontainers support](https://spring.io/blog/2023/06/23/improved-testcontainers-support-in-spring-boot-3-1/) introduced with Spring Boot 3.1. 
Thus, [Docker](https://www.docker.com) or [Testcontainers Cloud](https://testcontainers.com/cloud/) must run on your local computer

## Testing the application

[Karibu Testing](https://github.com/mvysny/karibu-testing) and [Playwright](https://playwright.dev) will be used in the future.

## Deploying to Production

To create a production build, call `mvnw clean package -Pproduction` (Windows),
or `./mvnw clean package -Pproduction` (Mac & Linux).

This will build a JAR file with all the dependencies and front-end resources,
ready to be deployed. The file can be found in the `target` folder after the build completes.

Once the JAR file is built, you can run it using
`java -jar target/vaadin-jooq-template-<version>.jar`

## Project structure

- `layout/MainLayout.java` in `src/main/java` contains the navigation setup (i.e., the side/top bar and the main menu). This setup uses [App Layout](https://vaadin.com/docs/components/app-layout).
- `views` package in `src/main/java` contains the server-side Java views of your application.
- `themes` folder in `frontend/` contains the custom CSS styles.

## Useful links

### Vaadin

- Checkout the [Vaadin Developer Portal](https://vaadin.com/developers)
- Read the documentation at [vaadin.com/docs](https://vaadin.com/docs).
- Create new projects at [start.vaadin.com](https://start.vaadin.com/).
- Find a collection of solutions to common use cases at [cookbook.vaadin.com](https://cookbook.vaadin.com/).
- Find add-ons at [vaadin.com/directory](https://vaadin.com/directory).

### jOOQ

- Read the documentation at [jooq.org/learn](https://www.jooq.org/learn/).
- Browse the [Blog](https://blog.jooq.org)

### Spring Boot

- Explore the [Spring Boot project page](https://spring.io/projects/spring-boot/) 

### Testcontainers

 - Go to the [Testcontainers website](https://testcontainers.com) 
