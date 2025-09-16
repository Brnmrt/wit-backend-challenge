# Calculator API - WIT Challenge

This project implements a RESTful calculator API in Java, using a microservices architecture with internal asynchronous communication via Apache Kafka. The API is synchronous for the client: the result is returned directly in the HTTP response.

## Technologies Used

- Java 21
- Spring Boot 3
- Apache Maven
- Apache Kafka
- Docker & Docker Compose
- JUnit 5
- SLF4J & Logback

## Prerequisites

- JDK 21 or higher
- Docker and Docker Compose installed

## How to Build and Run

1. **Build the JARs:**
   In the root directory (`calculator-parent`), run:
   ```bash
   ./mvnw clean package
   ```
This will generate the JARs in `calculator/target/` and `rest/target/`.

> **Attention:** Before running `docker-compose up --build`, you must build the JARs with Maven. Otherwise, the Docker build will fail with a file not found error.
> Run:
> ```bash
> mvn clean package
> ```
> This ensures that the files `rest/target/rest-*.jar` and `calculator/target/calculator-*.jar` exist and can be copied by the Dockerfiles.

2. **Run with Docker Compose:**
   Start all services (Zookeeper, Kafka, `rest`, `calculator`):
   ```bash
   docker-compose up --build
   ```
   The API will be available at `http://localhost:8080`.

## API Endpoints

The API exposes the following GET endpoints:

- `/sum?a={value}&b={value}`
- `/subtract?a={value}&b={value}`
- `/multiply?a={value}&b={value}`
- `/divide?a={value}&b={value}`

**Example request:**
```bash
curl -i "http://localhost:8080/sum?a=12.5&b=7.5"
```

**Example response:**
```json
HTTP/1.1 200 OK
Content-Type: application/json
X-Request-ID: <uuid>
{
  "result": 20.0
}
```

## Architecture

- The REST module receives the request, validates the parameters, and sends the operation to Kafka with a unique ID.
- The calculator module consumes the message, performs the calculation using BigDecimal, and returns the result via Kafka.
- The REST module waits for the response and returns the result directly to the client.

## Tests

- Unit and integration tests cover all endpoints and error scenarios.
- To run the tests:
  ```bash
  mvn test
  ```

## Implemented Bonus

- **SLF4J/Logback:** Complete logging with file appender, input/output/error logs.
- **Unique UUID:** Each request receives a unique ID propagated via HTTP header and Kafka.
- **MDC:** Propagation of the ID in logs of all modules, facilitating traceability.

## Notes

- All configuration is done via `application.properties` (no XML, except logback).
- If you encounter errors when restarting the containers (especially a `KafkaException` or `NodeExistsException` in the Kafka logs), this is likely due to persistent state in the Zookeeper or Kafka volumes after an uncontrolled shutdown.
  To force a complete reset of the infrastructure and clean the data from the volumes, run:
  ```bash
  docker-compose down -v
  ```
  Then start normally with:
  ```bash
  docker-compose up --build
  ```
