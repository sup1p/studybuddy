# Study Buddy

## Описание проекта
**Study Buddy** – это веб-приложение, которое помогает студентам находить других студентов для совместного изучения или обмена знаниями по определённым дисциплинам.

### Основной функционал
- **Регистрация и авторизация.**
- **Фильтрация пользователей по предметам и навыкам.**
- **Просмотр профиля других пользователей.**

### Скриншоты
- Главная страница:
  ![Главная страница](https://github.com/sup1p/studybuddy/blob/main/%7B2FEFEDF1-80D2-4FAC-A607-1E0D431FC6AA%7D.png)

### Ссылки
- [Frontend приложение](https://github.com/Rakamoosaka/ProjectStudyBuddy)
- [Backend API документация](https://github.com/sup1p/studybuddy/blob/main/API)
- [Deployed website](https://project-studybuddy.vercel.app)

---

## Руководство для разработчиков

### Как запустить проект

#### Требования:
- **Java**: 17+
- **Maven**: 3.8+
- **PostgreSQL**: 15+
- **Node.js**: 16+ (если используется фронтенд)
- **Nginx** (для production)

#### Шаги для запуска backend:
1. Склонируйте репозиторий:
   ```bash
   git clone https://github.com/ваш-репозиторий.git
   cd ваш-репозиторий
2. Настройте файл application.properties или используйте переменные окружения:
   spring.datasource.url=jdbc:postgresql://localhost:5432/your_database
   spring.datasource.username=your_username
   spring.datasource.password=your_password
3. Соберите проект:
   mvn clean package
4. Запустите Spring Boot:
   java -jar target/your_project-0.0.1-SNAPSHOT.jar
#### Шаги для запуска frontend:
1. Перейдите в директорию фронтенда:
    перейдите по ссылке оставленной сверху
2. Установите зависимости:
    npm install
3. Запустите приложение:
    npm start


### Траблшутинг
# Ошибка подключения к базе данных:

   Проверьте доступность PostgreSQL-сервера.
   Убедитесь, что данные для подключения в application.properties корректны.
# Ошибка при сборке проекта:

   Проверьте наличие всех зависимостей в pom.xml:
   mvn dependency:resolve
   Убедитесь, что Java и Maven установлены корректно.


## Требования к разработке
# Нейминги:
Для классов и интерфейсов используйте CamelCase.
Для переменных используйте camelCase.
Для пакетов используйте lowercase.
# Коммиты:
Следуйте Conventional Commits:
feat: – добавление нового функционала.
fix: – исправление ошибок.
chore: – технические изменения.
# Пример сообщения коммита:

feat(auth): добавлена поддержка OAuth2
# Кодстайл:
Используйте Google Java Style Guide.
# Перед коммитом запускайте:

mvn spotless:apply
для форматирования кода.
