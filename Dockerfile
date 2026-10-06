# Stage 1: Build the application using Microsoft JDK 21
FROM mcr.microsoft.com/openjdk/jdk:21-alpine AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

# Stage 2: Run the application
FROM mcr.microsoft.com/openjdk/jdk:21-distroless
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
