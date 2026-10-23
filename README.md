# Paperless REST API

Spring Boot REST API with PostgreSQL, Angular frontend and RabbitMQ

## Tech stack

- Backend: Spring Boot, Spring Data JPA, MapStruct, Lombok
- Frontend: Angular
- Database: PostgreSQL
- Messaging: RabbitMQ and OCR worker
- Deployment: Docker Compose, nginx
- CI: GitHub Actions

## Start

Docker Desktop must be running

```bash
docker compose up --build
```

The API runs on http://localhost:8081

Swagger UI: http://localhost:8081/swagger-ui/index.html

The frontend runs on http://localhost

RabbitMQ Management: http://localhost:15672 (user: paperless, password: paperless)

## Project structure

```
src/main/java/at/ac/fhtw/swen3/paperless
  controller/   REST controllers
  dto/          request and response DTOs
  mapper/       mappers between models and DTOs
  business/     services, models, validation and mappers between entities and models
  dal/          entities and repositories
  messaging/    RabbitMQ producer
  exception/    custom exceptions and global exception handler
  config/       RabbitMQ configuration
frontend/       Angular frontend and nginx configuration
ocr-worker/     RabbitMQ consumer for OCR jobs
api-tests/      Postman collection and curl script
```

OCR worker logs:

```bash
docker compose logs -f ocr-worker
```

## Run tests

```bash
./mvnw clean test
```

The tests use an H2 in-memory database, so PostgreSQL is not required

## Test endpoints

Start the application and run:

```bash
./api-tests/test-endpoints.sh
```

The Postman collection is located at `api-tests/Paperless.postman_collection.json`

## CI pipeline

GitHub Actions runs on every push and pull request. It runs the backend tests and builds all Docker images

The workflow is located at `.github/workflows/ci.yml`

## Stop

```bash
docker compose down
```

## Contributing

Branch naming and pull request rules are described in [CONTRIBUTING.md](CONTRIBUTING.md)
