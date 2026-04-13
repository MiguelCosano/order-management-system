# Order Management System

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
