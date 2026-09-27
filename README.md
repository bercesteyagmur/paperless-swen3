# Paperless REST API

Spring Boot REST API with PostgreSQL

## Start

Docker Desktop must be running

```bash
docker compose up --build
```

The API runs on http://localhost:8081

Swagger UI: http://localhost:8081/swagger-ui/index.html

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

## Stop

```bash
docker compose down
```
