# Stage 1: Build
FROM maven:3.8.8-openjdk-21-slim as build

WORKDIR /app

# Копирование файлов Maven
COPY pom.xml .
COPY src ./src

# Загрузка зависимостей (кешируем этот слой для ускорения последующих сборок)
RUN mvn dependency:go-offline

# Сборка приложения без запуска тестов
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM openjdk:21-slim

WORKDIR /app

# Копирование собранного JAR из этапа сборки
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Установка точки входа
ENTRYPOINT ["java", "-Dvertx.disableDnsResolver=true", "-Djava.net.preferIPv4Stack=true", "-jar", "app.jar"]
