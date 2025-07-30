# Use official PostgreSQL image
FROM postgres:15

# Set environment variables
ENV POSTGRES_DB=ordersdb
ENV POSTGRES_USER=admin
ENV POSTGRES_PASSWORD=admin

# Copy initialization script
COPY init.sql /docker-entrypoint-initdb.d/

# Expose PostgreSQL port
EXPOSE 5432