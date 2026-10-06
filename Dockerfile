# Stage 1: Build the Spring Boot app using official Microsoft OpenJDK 21
FROM mcr.microsoft.com/openjdk/jdk:21-ubuntu AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

# Stage 2: Minimal runtime using Microsoft OpenJDK 21
FROM mcr.microsoft.com/openjdk/jdk:21-ubuntu
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
