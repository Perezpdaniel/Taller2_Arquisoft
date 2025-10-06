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
COPY --from=build /app/target/hotel-reservation-management-system.war /opt/payara/deployments/

# Ensure MariaDB JDBC driver is available on server classpath for JDBC pools
COPY --from=build /root/.m2/repository/org/mariadb/jdbc/mariadb-java-client/3.1.4/mariadb-java-client-3.1.4.jar /opt/payara/appserver/glassfish/domains/domain1/lib/

# Set environment variables for auto-deployment
ENV PAYARA_DEPLOY_DIR=/opt/payara/deployments

# Expose ports
EXPOSE 8080 4848

# Start Payara without custom command files
CMD ["/opt/payara/scripts/startInForeground.sh"]