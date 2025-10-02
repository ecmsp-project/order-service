# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

### Build and Test
- `mvn clean compile` - Compile the project
- `mvn clean test` - Run unit tests (excludes e2e tests)
- `mvn clean verify` - Run all tests including e2e tests
- `mvn clean package` - Build the JAR file
- `mvn spring-boot:run` - Run the application locally

### Local Development
- Run with `local` Spring profile for development (uses testcontainers for Kafka)
- Access Kafka UI at `http://localhost:8088` when running locally
- Docker daemon must be running for local development

### Testing
- Unit tests: `mvn test`
- E2E tests: `mvn failsafe:integration-test`
- Single test: `mvn test -Dtest=TestClassName`

## Architecture Overview

### Domain-Driven Design Structure
The codebase follows a hexagonal architecture with clear domain boundaries:

- **Domain Layer** (`order.domain`): Core business logic with `OrderFacade` as the main entry point
- **Application Layer** (`api`): REST controllers, gRPC services, and Kafka consumers
- **Infrastructure Layer** (`order.adapter`): Database, Kafka, and external service implementations

### Key Components
- **OrderFacade**: Main domain service interface for order operations
- **Order Aggregates**: Domain entities (Order, OrderItem, OrderStatus)
- **Repository Pattern**: Configurable implementations (DB, in-memory)
- **Event Publisher**: Kafka-based domain event publishing
- **Payment Integration**: Kafka-based communication with payment service

### Configuration Profiles
- `local`: Uses testcontainers for Kafka and PostgreSQL
- `dev`: Development environment configuration
- `compose`: Docker compose environment
- `test`: Test environment with H2 database

### Kafka Topics
- `payment-processed-succeeded/failed`: Payment result events
- `payment-request`: Payment initiation
- `order-status-updated`: Order state changes
- `cart-event`: Cart-related events

### Configurable Components
The service uses Spring profiles to switch between implementations:
- Repository: `db` or `inmemory`
- Payment Client: `kafka` or `inmemory`
- Event Publisher: `kafka` or `inmemory`
- ID Generator: `random` or `fixed`

### gRPC Integration
The service includes gRPC endpoints using protobuf definitions from the `com.ecmsp:protos` dependency.

### Database
- PostgreSQL for production
- H2 for testing
- JPA entities with Hibernate
- Flyway for migrations (enabled by default)

## API Endpoints for E2E Testing

### REST API Endpoints

#### Base URL
- **REST API**: `http://localhost:8300/api/orders`
- **Health Check**: `http://localhost:8300/health`

#### OrdersController (`/api/orders`)

**GET /api/orders**
- List all orders (TODO: requires admin authorization)
- Response: `200 OK` with array of `OrderDetailsResponse`

**GET /api/orders/user/{userId}**
- List orders by user ID (uses JWT context from gateway)
- Requires: `X-User-Id` header (injected by gateway)
- Response: `200 OK` with array of `OrderDetailsResponse`

**GET /api/orders/{orderId}**
- Get order by ID
- Path parameter: `orderId` (UUID)
- Response: `200 OK` with `OrderDetailsResponse` or `404 Not Found`

**POST /api/orders**
- Create a new order
- Headers:
  - `X-Correlation-Id` (optional): UUID for request correlation
- Request body: `CreateOrderRequest`
```json
{
  "clientId": "uuid",
  "items": [
    {
      "itemId": "uuid",
      "quantity": 1,
      "price": 99.99,
      "returnable": true
    }
  ]
}
```
- Response: `201 Created` with `OrderDetailsResponse`

**PUT /api/orders/{orderId}**
- Update order status
- Path parameter: `orderId` (UUID)
- Request body: `UpdateOrderRequest`
```json
{
  "orderStatus": "PAID"
}
```
- Valid statuses: `PENDING`, `PROCESSING`, `PAID`, `FAILED`, `CANCELLED`, `COMPLETED`
- Response: `200 OK` with `OrderDetailsResponse`

**DELETE /api/orders/{orderId}**
- Delete an order
- Path parameter: `orderId` (UUID)
- Response: `204 No Content`

**GET /api/orders/{orderId}/returnability**
- Get order returnability details
- Path parameter: `orderId` (UUID)
- Response: `200 OK` with `OrderReturnabilityResponse` or `404 Not Found`

**GET /api/orders/{orderId}/returnable**
- Check if order can be returned (boolean)
- Path parameter: `orderId` (UUID)
- Response: `200 OK` with boolean value

#### InternalOrdersController (`/api/internal/orders`)

**POST /api/internal/orders/order-id-mappings**
- Create order ID mapping (only available when `order.id-generator.type=fixed`)
- Request body: `OrderIdMappingDto`
```json
{
  "correlationId": "uuid",
  "orderId": "uuid"
}
```
- Response: `200 OK`

#### HealthController (`/health`)

**GET /health**
- Health check endpoint
- Response: `200 OK` with `"OK"` string

### gRPC API Endpoints

#### Service Configuration
- **gRPC Server**: `localhost:7300`
- **Proto Package**: `com.ecmsp.order.v1`
- **Service**: `OrderService`

#### gRPC Methods

**GetOrder**
- Request: `GetOrderRequest { string order_id }`
- Response: `GetOrderResponse` with order details
- Errors: `NOT_FOUND` if order doesn't exist, `INTERNAL` for other errors

**CreateOrder**
- Request: `CreateOrderRequest` with client ID and items
- Response: `CreateOrderResponse` with created order details
- Note: Context/metadata support is planned for future versions
- Errors: `INTERNAL` on failure

**UpdateOrder**
- Request: `UpdateOrderRequest` with order ID and new status
- Response: `UpdateOrderResponse` with updated order details
- Errors: `NOT_FOUND` if order doesn't exist, `INTERNAL` for other errors

**DeleteOrder**
- Request: `DeleteOrderRequest { string order_id }`
- Response: `DeleteOrderResponse { bool success }`
- Errors: `INTERNAL` on failure

**ListOrders**
- Request: `ListOrdersRequest` (empty)
- Response: `ListOrdersResponse` with array of orders
- Errors: `INTERNAL` on failure

### Response Models

**OrderDetailsResponse**
```json
{
  "orderId": "uuid",
  "clientId": "uuid",
  "orderStatus": "PENDING|PROCESSING|PAID|FAILED|CANCELLED|COMPLETED",
  "date": "2025-09-30T12:00:00",
  "items": [
    {
      "itemId": "uuid",
      "quantity": 1
    }
  ]
}
```

### E2E Testing Notes

1. **Authentication**: The service expects JWT context via headers (injected by API Gateway)
   - Use `/api/orders/user/{userId}` endpoint to test gateway integration

2. **Correlation IDs**: Include `X-Correlation-Id` header for request tracing

3. **Order Lifecycle**:
   - Orders start in `PENDING` status
   - Payment processing moves to `PROCESSING` then `PAID` or `FAILED`
   - Can be `CANCELLED` by user or system
   - Final status is `COMPLETED` after delivery

4. **Kafka Integration**: Creating orders triggers payment requests via Kafka
   - Topic: `payment-request`
   - Listen on: `payment-processed-succeeded` or `payment-processed-failed` for results

5. **Database State**: Orders are persisted to PostgreSQL (or H2 in tests)

6. **Proto Definitions**: gRPC contracts are defined in `com.ecmsp:protos` dependency