@echo off
echo ========================================
echo   REBUILDING AND REDEPLOYING PROJECT
echo ========================================

echo.
echo 1. Cleaning project...
call mvn clean

echo.
echo 2. Compiling project...
call mvn compile

echo.
echo 3. Packaging project...
call mvn package

echo.
echo 4. Stopping Docker containers...
docker-compose down

echo.
echo 5. Starting Docker containers...
docker-compose up -d

echo.
echo ========================================
echo   DEPLOYMENT COMPLETE!
echo ========================================
echo.
echo Access your application at:
echo   http://localhost:8080/index.jsf
echo.
echo Or try these URLs:
echo   http://localhost:8080/clients/list.jsf
echo   http://localhost:8080/reservations/list.jsf
echo.
pause
