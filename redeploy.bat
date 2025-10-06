@echo off
echo Building and redeploying application...

echo.
echo 1. Stopping Docker containers...
docker-compose down

echo.
echo 2. Building application...
mvn clean package -DskipTests

echo.
echo 3. Starting Docker containers...
docker-compose up -d

echo.
echo 4. Waiting for Payara to start...
timeout /t 30 /nobreak > nul

echo.
echo 5. Deploying application...
docker exec -it payara-server /opt/payara/bin/asadmin deploy /opt/payara/deployments/hotel-reservation-management-system.war

echo.
echo Application redeployed successfully!
echo Access the application at: http://localhost:8080/hotel-reservation-management-system/

pause