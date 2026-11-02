# JMP REST service

This Java 17 / Spring Boot 3.2 Maven project is split into five Maven modules:

- `jmp-domain`: JPA entities
- `jmp-dto`: request and HATEOAS response DTOs
- `jmp-service-api`: repository and service contracts
- `jmp-cloud-service-impl`: JPA-backed service implementations
- `jmp-service-rest`: REST controllers, converters, OpenAPI and application entry point

## Run

Create a PostgreSQL database named `jmp_db`, then set the connection variables if
your local PostgreSQL credentials differ from the defaults:

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/jmp_db"
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "postgres"
mvn -pl jmp-service-rest -am package
java -jar jmp-service-rest\target\jmp-service-rest-1.0-SNAPSHOT.jar
```

The application creates/updates tables using Hibernate. For the interactive API
documentation, open [Swagger UI](http://localhost:8080/swagger-ui.html). The
OpenAPI document is available at [http://localhost:8080/api-docs](http://localhost:8080/api-docs).
In Swagger UI, expand an operation, choose **Try it out**, enter the sample JSON
below, and choose **Execute**. A created resource returns `201 Created` with a
`Location` header and a self link; update and delete use the resource URL.

## Sample requests

Create a user (the request `id` is ignored):

```http
POST /api/v1/users
Content-Type: application/json

{"id":999,"name":"Ada","surname":"Lovelace","birthday":"1815-12-10"}
```

Update the created user (replace `1` with the returned user ID; body ID must
match the URL):

```http
PUT /api/v1/users/1
Content-Type: application/json

{"id":1,"name":"Augusta","surname":"Lovelace","birthday":"1815-12-10"}
```

Retrieve users:

```http
GET /api/v1/users/1
GET /api/v1/users
```

Create a subscription (replace `1` with an existing user ID; start date is set
when the subscription is created):

```http
POST /api/v1/subscriptions
Content-Type: application/json

{"userId":1}
```

Update the subscription (replace both IDs with the IDs returned by the API):

```http
PUT /api/v1/subscriptions/1
Content-Type: application/json

{"id":1,"userId":1}
```

Retrieve and delete resources:

```http
GET /api/v1/subscriptions/1
GET /api/v1/subscriptions
DELETE /api/v1/subscriptions/1
DELETE /api/v1/users/1
```

The controllers expose resource URLs and hypermedia links (Richardson Level 3),
use HTTP methods/status codes (Level 2), and use resource-specific paths
(Level 1). The `/api/v1` prefix also supports versioned resource URLs.
