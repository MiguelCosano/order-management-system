# Order Management System

This repository contains two Spring Boot microservices:

- `product-service`
- `order-service`

## IntelliJ IDEA

Open the repository root, not the individual service folders.

1. In IntelliJ IDEA, choose `File` -> `Open`.
2. Select the `order-management-system` directory.
3. Import the root `pom.xml` as a Maven project.

The root Maven aggregator imports both microservices into the same IntelliJ window, so you can build and run them side by side.

Recommended project settings:

- JDK: `21`
- Maven import: enabled
- Annotation processing: enabled

After the import, IntelliJ should show both Maven modules:

- `product-service`
- `order-service`

## Docker

Build the application JARs before starting the containers:

```bash
cd product-service && ./mvnw clean package
cd ../order-service && ./mvnw clean package
```

Start the full stack from the repository root:

```bash
docker compose up --build
```

Stop the stack:

```bash
docker compose down
```

Exposed ports:

- MongoDB: `27017`
- product-service: `8081`
- order-service: `8082`
