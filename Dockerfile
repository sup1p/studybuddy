# Stage 1: Build
FROM maven:3.8-openjdk-21-slim as build

WORKDIR /app

# Copy the Maven files
COPY pom.xml .
COPY src src

# Download and cache dependencies
RUN mvn dependency:resolve

# Build the application JAR
RUN mvn package -DskipTests

# Stage 2: Run
FROM openjdk:21-slim

WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Set the entrypoint
ENTRYPOINT ["java", "-Dvertx.disableDnsResolver=true", "-Djava.net.preferIPv4Stack=true", "-jar", "app.jar"]
