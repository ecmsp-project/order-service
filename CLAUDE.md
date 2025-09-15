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
- Liquibase for migrations (if configured)