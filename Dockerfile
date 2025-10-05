# Dockerfile to build the application
FROM maven:3.8.6-openjdk-11-slim AS build

# Set working directory
WORKDIR /app

# Copy Maven configuration files
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Production stage - Payara Server
FROM payara/server-full:6.2024.1

# Copy the built WAR file
COPY --from=build /app/target/hotel-reservation-management-system.war $DEPLOY_DIR/

# Set environment variables for auto-deployment
ENV PAYARA_DEPLOY_DIR=/opt/payara/deployments

# Expose ports
EXPOSE 8080 4848

# Start Payara without custom command files
CMD ["/opt/payara/scripts/startInForeground.sh"]