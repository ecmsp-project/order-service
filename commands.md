# Database and Migration Commands

## Docker Commands

### PostgreSQL Container Management
```bash
# Start PostgreSQL container for order-service
docker run -d --name order-service-postgres \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=admin \
  -e POSTGRES_DB=order-service-db \
  -p 5432:5432 postgres:15

# Check running containers
docker ps

# Check if PostgreSQL container is running
docker ps | grep order-service-postgres

# Stop PostgreSQL container
docker stop order-service-postgres

# Remove PostgreSQL container
docker rm order-service-postgres
```

### Database Access
```bash
# Connect to PostgreSQL database
docker exec -it order-service-postgres psql -U admin -d order-service-db

# List tables
docker exec order-service-postgres psql -U admin -d order-service-db -c "\dt"

# Show table structure
docker exec order-service-postgres psql -U admin -d order-service-db -c "SELECT table_name, column_name, data_type FROM information_schema.columns WHERE table_schema = 'public' ORDER BY table_name, ordinal_position;"

# Count records in tables
docker exec order-service-postgres psql -U admin -d order-service-db -c "SELECT COUNT(*) as order_count FROM orders;"
docker exec order-service-postgres psql -U admin -d order-service-db -c "SELECT COUNT(*) as order_item_count FROM order_item;"

# View sample data
docker exec order-service-postgres psql -U admin -d order-service-db -c "SELECT * FROM orders LIMIT 5;"
docker exec order-service-postgres psql -U admin -d order-service-db -c "SELECT * FROM order_item LIMIT 5;"
```

## Flyway Commands

### Basic Flyway Operations
```bash
# Show migration information and status
mvn flyway:info

# Run all pending migrations
mvn flyway:migrate

# Validate migrations
mvn flyway:validate

# Clean database (removes all objects) - USE WITH CAUTION
mvn flyway:clean

# Repair schema history table
mvn flyway:repair

# Show current schema version
mvn flyway:info | grep "Schema version"
```

### Development Workflow
```bash
# Compile project first (needed for classpath resources)
mvn compile

# Run migrations after compilation
mvn compile && mvn flyway:migrate

# Check migration status after applying changes
mvn flyway:info
```

## Maven Commands

### Build and Compile
```bash
# Clean and compile
mvn clean compile

# Run tests (excludes e2e tests)
mvn clean test

# Run all tests including e2e
mvn clean verify

# Build JAR package
mvn clean package

# Run application locally
mvn spring-boot:run
```

### Development with Profiles
```bash
# Run application with dev profile
mvn spring-boot:run -Dspring.profiles.active=dev

# Run with local profile (uses testcontainers)
mvn spring-boot:run -Dspring.profiles.active=local
```

## File System Commands

### Migration Files
```bash
# List migration files
ls -la src/main/resources/db/migration/

# View migration file content
cat src/main/resources/db/migration/V1__Create_initial_schema.sql
cat src/main/resources/db/migration/V2__Insert_example_data.sql

# Find all configuration files
find src/main/resources -name "*.yml" -o -name "*.properties"
```

### Configuration
```bash
# View application configuration
cat src/main/resources/application.yml
cat src/main/resources/application-dev.yml

# Check current directory structure
tree src/main/resources/
```

## Troubleshooting Commands

### Connection Issues
```bash
# Test PostgreSQL connection
docker exec order-service-postgres pg_isready -U admin

# Check PostgreSQL logs
docker logs order-service-postgres

# Check if port 5432 is in use
netstat -tlnp | grep 5432
# or
lsof -i :5432
```

### System Status
```bash
# Check Docker daemon status
systemctl status docker

# Check available Docker images
docker images | grep postgres

# Check Docker network
docker network ls
```

## Migration File Naming Convention

Your current naming convention is **correct** and follows Flyway standards:

- `V1__Create_initial_schema.sql` ✅
- `V2__Insert_example_data.sql` ✅

### Naming Rules:
- Format: `V{version}__{description}.sql`
- Version: Sequential numbers (V1, V2, V3, etc.)
- Separator: Double underscore `__` between version and description
- Description: Use underscores instead of spaces
- File extension: `.sql`

### Examples of Good Migration Names:
```
V1__Create_initial_schema.sql
V2__Insert_example_data.sql
V3__Add_user_table.sql
V4__Update_order_status_enum.sql
V5__Create_indexes.sql
```