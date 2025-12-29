# Etapa 1: Construcción
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .
COPY src ./src

# Construir la aplicación (sin ejecutar tests)
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copiar el JAR desde la etapa de construcción
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto de la aplicación
EXPOSE 8001

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
