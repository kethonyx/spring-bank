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
| Auth | JWT (jjwt 0.12), stateless Spring Security |
| Database | PostgreSQL |
| Docs | springdoc-openapi (Swagger UI) |
| Tests | JUnit 5, MockMvc, H2 |
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
| `POST` | `/auth/register` | Register a new user | Public |
| `POST` | `/auth/login` | Authenticate, receive JWT | Public |
| `GET` | `/users/me` | Current user's profile | JWT |
| `POST` | `/accounts` | Open a bank account (`{"currency":"USD"}`) | JWT |
| `GET` | `/accounts` | List my accounts | JWT |
| `POST` | `/accounts/deposit` | Deposit into one of my accounts | JWT |
| `POST` | `/transactions/transfer` | Transfer from my account to any account (same currency) | JWT |
| `GET` | `/transactions?page=0&size=20` | My transaction history, newest first (paginated) | JWT |

### Transfer rules

- The sender account must belong to the authenticated user (otherwise `404`).
- Sender and receiver must differ and use the same currency (otherwise `422`).
- Insufficient balance returns `422`.
- Amounts must be positive with at most 2 decimal places.
- Both accounts are row-locked (`SELECT ... FOR UPDATE`) in ascending id order, so concurrent transfers can't overdraw an account or deadlock.

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
- Docker (for PostgreSQL) or a local PostgreSQL
- Maven (or use the included `./mvnw` wrapper)

### 1. Start PostgreSQL

```bash
docker compose up -d
```

(or create a `springbank` database on your own Postgres.)

### 2. Configure

All settings have dev defaults matching `compose.yaml`. Override with environment variables:

| Variable | Default |
|----------|---------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/springbank` |
| `DB_USERNAME` / `DB_PASSWORD` | `springbank` / `springbank` |
| `JWT_SECRET` | dev-only value — **set this outside local dev** (min 32 bytes) |
| `JWT_EXPIRATION` | `1h` |

Schema is created automatically on startup (`spring.jpa.hibernate.ddl-auto=update`).

### 3. Run

```bash
./mvnw spring-boot:run
```

The API starts on `http://localhost:8080`.

## Example Usage

```bash
# Register
curl -X POST http://localhost:8080/auth/register \
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

Integration tests run against in-memory H2, so no database is needed. They cover auth, deposits, transfers, ownership checks and validation.
