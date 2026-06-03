# Spring Bank

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0-6DB33F?style=flat&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-DB-4169E1?style=flat&logo=postgresql&logoColor=white)
![JWT](https://img.shields.io/badge/Auth-JWT-000000?style=flat&logo=jsonwebtokens&logoColor=white)

A REST backend for a simple banking application — user registration, JWT authentication, bank accounts, and money transfers. Built with Spring Boot, Spring Security, and JPA over PostgreSQL.

## Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Java 21 |
| Framework | Spring Boot 4.0 (Web MVC, Data JPA, Security) |
| Auth | JWT (jjwt 0.12) + OAuth2 Resource Server |
| Database | PostgreSQL |
| Docs | springdoc-openapi (Swagger UI) |
| Build | Maven (wrapper included) |
| Boilerplate | Lombok |

## Domain Model

```
User ──< Account ──< Transaction
```

| Entity | Description |
|--------|-------------|
| `User` | Registered customer with credentials |
| `Account` | A bank account belonging to a user (balance, currency) |
| `Transaction` | A transfer record between accounts |

## API Endpoints

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/users/register` | Register a new user | Public |
| `POST` | `/auth/login` | Authenticate, receive JWT | Public |
| `GET` | `/users` | List users | JWT |
| `POST` | `/accounts` | Create a bank account | JWT |
| `POST` | `/transactions/transfer` | Transfer money between accounts | JWT |

Interactive API docs are available at `/swagger-ui.html` once the app is running.

## Project Structure

```
src/main/java/com/dimash/springbank/
├── config/          # Security configuration
├── controller/      # REST controllers
├── dto/             # Request/response objects
├── entity/          # JPA entities
├── exception/       # Global exception handling
├── repository/      # Spring Data repositories
├── security/        # JWT filter
└── service/         # Business logic
```

## Getting Started

### Prerequisites

- Java 21+
- PostgreSQL
- Maven (or use the included `./mvnw` wrapper)

### 1. Create the database

```bash
createdb springbank
```

### 2. Configure connection

The app reads its datasource from `src/main/resources/application.properties`.
Override the defaults with environment variables rather than editing the file:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/springbank
export SPRING_DATASOURCE_USERNAME=your_user
export SPRING_DATASOURCE_PASSWORD=your_password
```

Schema is created automatically on startup (`spring.jpa.hibernate.ddl-auto=update`).

### 3. Run

```bash
./mvnw spring-boot:run
```

The API starts on `http://localhost:8080`.

## Example Usage

```bash
# Register
curl -X POST http://localhost:8080/users/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","email":"alice@example.com","password":"secret123"}'

# Login → returns a JWT
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"alice@example.com","password":"secret123"}'

# Create an account (authenticated)
curl -X POST http://localhost:8080/accounts \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"currency":"USD"}'

# Transfer money (authenticated)
curl -X POST http://localhost:8080/transactions/transfer \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"senderAccountId":1,"receiverAccountId":2,"amount":100.00}'
```

## Testing

```bash
./mvnw test
```
