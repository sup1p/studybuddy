# Этап 1: Сборка
FROM maven:3.9.4-openjdk-21-slim AS build

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем файл pom.xml и загружаем зависимости
COPY pom.xml .

# Предварительно загружаем зависимости для использования кэширования Docker
RUN mvn dependency:go-offline

# Копируем исходный код приложения
COPY src ./src

# Собираем JAR-файл приложения, пропуская тесты (опционально)
RUN mvn clean package -DskipTests

# Этап 2: Запуск
FROM openjdk:21-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем собранный JAR-файл из этапа сборки
COPY --from=build /app/target/*.jar app.jar

# Открываем порт приложения
EXPOSE 8080

# Устанавливаем команду для запуска приложения
ENTRYPOINT ["java", "-Dvertx.disableDnsResolver=true", "-Djava.net.preferIPv4Stack=true", "-jar", "app.jar"]
