# Evidentra Backend

Evidentra is a Java 21 Spring Boot 3 backend for secure digital police evidence management. It supports case management, evidence tracking, chain of custody, audit logging, forensic task workflow, JWT authentication, PostgreSQL persistence, Swagger/OpenAPI documentation, Docker, and CI.

## Tech Stack

- Java 21
- Spring Boot 3
- Maven
- PostgreSQL
- Spring Security with JWT
- Spring Data JPA and Hibernate
- Flyway migrations
- Swagger OpenAPI
- Docker and Docker Compose
- JUnit 5, Mockito, Lombok

## Run Locally

Install Java 21, Maven, and Docker. Then run:

```bash
mvn spring-boot:run
```

The default `dev` profile uses Spring Boot Docker Compose support and `docker-compose.dev.yml` to start PostgreSQL when Docker is available.

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

Health check:

```text
http://localhost:8080/actuator/health
```

## Run With Docker Compose

Create a local `.env` from `.env.example` and set a strong `JWT_SECRET`, then run:

```bash
docker compose up --build
```

The API will be available on:

```text
http://localhost:8080
```

## Authentication Quick Start

Register the first administrator. Public registration is only for bootstrap; after the first user exists, administrators create additional users through `POST /api/v1/users`.

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "ChangeMe123!",
    "fullName": "System Administrator",
    "role": "ADMIN"
  }'
```

Login:

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"ChangeMe123!"}'
```

Use the returned JWT as:

```text
Authorization: Bearer <token>
```

## Core Endpoints

- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `POST /api/v1/users`
- `GET /api/v1/users`
- `GET /api/v1/audit-logs`
- `POST /api/v1/cases`
- `GET /api/v1/cases`
- `GET /api/v1/cases/{id}`
- `POST /api/v1/evidence`
- `GET /api/v1/evidence/{id}`
- `GET /api/v1/evidence/case/{caseId}`
- `POST /api/v1/evidence/{id}/verify`
- `POST /api/v1/evidence/{id}/custody`
- `GET /api/v1/evidence/{id}/custody`
- `POST /api/v1/forensics/tasks`
- `GET /api/v1/forensics/tasks`
- `PATCH /api/v1/forensics/tasks/{id}/complete`

## Configuration

Important environment variables:

- `SPRING_PROFILES_ACTIVE`
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `JWT_SECRET`
- `APP_PORT`

Development defaults live in `src/main/resources/application-dev.yml`. Production settings live in `src/main/resources/application-prod.yml`.

## Database

Flyway migration `V1__init_schema.sql` creates:

- users
- cases
- evidence items
- chain of custody entries
- forensic tasks
- audit logs

## Tests

Run:

```bash
mvn test
```

The `test` profile uses an in-memory H2 database in PostgreSQL compatibility mode.
