# Project Specification: GraphQL API Backend

## Project Overview
A modern backend application built with Spring Boot and Kotlin, exposing a GraphQL API for client applications.

The application domain is Books and Authors. Book can have only one author. Author can have many books. The user can do these tasks with app usign the gql api:

- create a book/author
- retrieve book/author
- update book/author
- delete book/author
- list all books by name, isbn, year

## GQL API
Write Grapqhql API that contains Book and Author types, queries and mutations for the tasks from project overview.

## Tech Stack

### Backend
- **Language**: Kotlin 1.9+
- **JVM runtime**: Eclipse Temurin 21.0.9-tem
- **Framework**: Spring Boot 3.2+
- **GraphQL**: Spring for GraphQL (spring-boot-starter-graphql)
- **Database**: PostgreSQL 15+
- **Persistence**: jOOQ compatible version with spring, flyway migrations
- **Build Tool**: Gradle (Kotlin DSL)
- **Testing**: JUnit 5, MockK
- **App config**: springobot application.properties, set correct properties for urls and ports for services defined in docker-compose.yml

### Infrastructure
Docker compose, docker-compose.yml contains the folllwing services with versions that are compatible together
- Postgres container
- Prometheus
- Grafana dashboards

### Design Pattern
- **Layered Architecture**: Controller (Resolver) → Service → Repository
- **Domain-Driven Design**: Separate domain models from DTOs
- **Dependency Injection**: Constructor injection with Spring

### Project Structure
```
src/
├── main/
│   ├── kotlin/
│   │   └── com/rkoubsky/books/
│   │       ├── config/          # Configuration classes
│   │       ├── gql/        # GraphQL resolvers (controllers) + model BookGQL
│   │       ├── service/         # Business logic + model Book
│   │       ├── repository/      # Data access layer, model are classes generated from sql tables using jooq
│   │       ├── security/        # Security configuration
│   │       ├── exception/       # Custom exceptions
│   │       ├── util/            # Utility classes
│   │       └── Application.kt   # Main application class
│   └── resources/
│       ├── graphql/             # GraphQL schema files (.graphqls)
│       ├── application.yml      # Configuration
│       └── db/migration/        # Flyway migrations
└── test/
    └── kotlin/
        └── com/rkoubsky/books/
```

## Code Standards

### General Principles
1. Write idiomatic Kotlin code
2. Follow SOLID principles
3. Prefer immutability (use `val` over `var`)
4. Use data classes for DTOs
5. Leverage Kotlin null safety
6. Write unit tests for business logic (80% coverage minimum)

### Kotlin Standards

#### Naming Conventions
- **Packages**: lowercase, no underscores (`com.company.project.service`)
- **Classes/Interfaces**: PascalCase (`BookService`, `BookRepository`)
- **Functions/Properties**: camelCase (`getBookById`, `isActive`)
- **Constants**: UPPER_SNAKE_CASE in companion object (`MAX_RETRY_COUNT`)
- **Files**: PascalCase matching primary class (`BookService.kt`)

### Database Standards

#### Schema Naming
- Tables: snake_case (`book`, `author`)
- Columns: snake_case (`created_at`, `author_id`)
- Primary keys: `id` (UUID)
- Foreign keys: `{table_name}_id` (`author_id`)
