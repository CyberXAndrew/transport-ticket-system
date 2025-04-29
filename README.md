[![Maintainability](https://qlty.sh/badges/3628cde9-b84c-4c87-8b92-113381302e33/maintainability.png)](https://qlty.sh/gh/CyberXAndrew/projects/transport-ticket-system)
[![codecov](https://codecov.io/gh/CyberXAndrew/transport-ticket-system/graph/badge.svg?token=I5AEBG3WVJ)](https://codecov.io/gh/CyberXAndrew/transport-ticket-system)

### Description

Transport ticket system project is a Java-based Spring Boot microservice providing a REST API for managing
transportation ticket purchases.It includes user registration, ticket browsing, and purchase functionality.
Key features include filtering and pagination, input validation, and Swagger API documentation. Additional features
implemented include authentication by JWT access & refresh tokens,role-based authorization, Redis caching, and Kafka
integration.

### Key Features

* **User Registration:** Allows users to register with a unique login, password, and full name.
* **Ticket Browsing:** Provides a REST endpoint to retrieve a paginated and filtered list of available tickets. 
Filters include date/time, origin/destination (substring matching), and carrier name (substring matching).
* **Ticket Purchase:** Enables users to purchase available tickets. Purchased tickets are no longer available for
purchase.
* **Purchase History:** Allows users to view a list of their purchased tickets.
* **Input Validation:** Validates all REST method input data, returning HTTP 40х errors with descriptive messages 
for invalid input.
* **Swagger Documentation:** Includes Swagger annotations for automatically generated API documentation.

### Additional Features

* **JWT Authentication:** Implements user authentication using JWT tokens (access and refresh tokens).
* **Role-Based Authorization:** Adds user roles (Customer/Administrator) and restricts access to certain API endpoints
based on role.
* **Redis Caching:** Caches purchased tickets in Redis for faster retrieval.
* **Kafka Integration:** Publishes ticket purchase events to a Kafka topic for asynchronous processing.

### Technologies used

| Technology                         | Version                          | Description                                                                                                                     |
|------------------------------------|----------------------------------|---------------------------------------------------------------------------------------------------------------------------------|
| **Core Framework & Web**           |                                  |                                                                                                                                 |
| Spring Boot                        | 3.4.3                            | Framework for building stand-alone, production-ready Spring-powered applications quickly.                                       |
| Spring Web                         | 3.4.3                            | Spring Boot module for developing web applications, including RESTful APIs.                                                     |
| Spring JDBC                        | 3.4.3                            | Spring Boot module for simplifying database interaction using JDBC.                                                             |
| Spring Data Commons                | 3.4.3                            | Core abstractions for Spring Data, including repositories.                                                                      |
| Spring Validation                  | 3.4.3                            | Spring Boot module for validating data coming into the application (e.g., through REST APIs).                                   |
| Spring Boot Security               | 3.4.3                            | Provides Spring Security integration for authentication and authorization.                                                      |
| Spring Security Test               | 6.4.4                            | Provides testing support for Spring Security.                                                                                   |
| **Security (JWT)**                 |                                  |                                                                                                                                 |
| JJWT API                           | 0.12.6                           | A Java library for creating and verifying JSON Web Tokens (JWTs).                                                               |
| JJWT Impl                          | 0.12.6                           | Implementation of the JJWT API.                                                                                                 |
| JJWT Jackson                       | 0.12.6                           | Jackson integration for JJWT, enabling JWT claims to be handled with Jackson.                                                   |
| **Databases**                      |                                  |                                                                                                                                 |
| H2 Database                        | 2.3.232                          | An in-memory database, often used for development and testing.                                                                  |
| PostgreSQL                         | 42.7.5                           | A powerful, open-source object-relational database system.                                                                      |
| Redis                              | (From Spring Data Redis Version) | In-memory data structure store, used as a database, cache and message broker. Used for caching purchased tickets for each user. |
| Spring Data Redis                  | 3.4.4                            | Provides integration for Redis data store. (Already covered by redis above).                                                    |
| **API Documentation**              |                                  |                                                                                                                                 |
| Springdoc OpenAPI                  | 2.8.5                            | Library for automatically generating OpenAPI (Swagger) documentation for Spring Boot REST APIs.                                 |
| **Testing**                        |                                  |                                                                                                                                 |
| Spring Boot Test                   | 3.4.3                            | Spring Boot module for integration testing Spring Boot applications.                                                            |
| Mockito Core                       | 5.16.0                           | A framework for creating mock objects for unit tests.                                                                           |
| Mockito JUnit Jupiter              | 5.16.0                           | Mockito extension for integration with JUnit Jupiter (JUnit 5).                                                                 |
| **Logging**                        |                                  |                                                                                                                                 |
| SLF4J                              | 2.0.17                           | A facade for various logging frameworks in Java.                                                                                |
| Logback Classic                    | 1.5.11                           | A logging framework that implements the SLF4J API.                                                                              |
| **Object Mapping & Data Transfer** |                                  |                                                                                                                                 |
| Lombok                             | 1.18.36                          | A library for automatically generating boilerplate code (getters, setters, constructors, etc.).                                 |
| MapStruct                          | 1.6.3                            | A code generation library for mapping Java beans.                                                                               |
| Lombok-Mapstruct Binding           | 0.2.0                            | Allows Lombok and MapStruct to work together seamlessly.                                                                        |
| Jackson Databind Nullable          | 0.2.6                            | Utilities for handling nullable values, for more correct work with nullable fields when working with JSON (e.g., in OpenAPI).   |
| Jackson Datatype JSR310            | 2.18.3                           | Provides support for Java 8 Date & Time API (java.time) with Jackson.                                                           |
| **Jakarta EE**                     |                                  |                                                                                                                                 |
| Jakarta Persistence                | 3.2.0                            | API for managing persistence for Java EE objects.                                                                               |

### Status

Completed
