# Taller2_Arquisoft
Sistema monolitico por capas de una aplicacion para reservas en un hotel basado en Java usando Jakarta, JSF y MariaDB

Pasos para hacer deploy en localhost

1. en la terminal del proyecto ejecutar los siguientes comandos:
.\redeploy.bat
docker-compose up -d

2. en localhost:4848 ingresar con admin/admin

JDBC conection pool -> new 
name: HotelReservationsPool
Resource Type: javax.sql.DataSource
MariaDB

3. declarar propiedades
Name: User , value: hotel_user
Name: Url , value: jdbc:mariadb://mariadb:3306/hotel_reservations
Name: password, value: hotel_password
Name: LoginTimeout, value: 30
Name: driverClass, value: org.mariadb.jdbc.Driver

4. JDBC resources -> new

JNDI name: jdbc/HotelReservationsDB
ConnectionPool: HotelReservationsPool

5. Applications

deploy -> new y se adjunta el .war generado en la carpeta target

6. dirigirse a localhost:8080 para la pagina web y localhost:8081 para la base de datos