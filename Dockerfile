# Etapa de build con Maven y JDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa de ejecución con JRE 21 en Alpine
FROM eclipse-temurin:21.0.2_13-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8083
CMD ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
