# CarTrade

Консольное приложение на Kotlin для управления объявлениями о продаже транспортных средств (авто, мото, коммерческий
транспорт). Проект вдохновлён функционалом "Авто.ру".

## Описание

Приложение позволяет пользователям добавлять транспортные средства, владельцев, создавать и снимать объявления, изменять
цены с сохранением истории, а также искать актуальные объявления по различным параметрам. Все данные хранятся в одном
файле `data.json`.

### Основные возможности

- Добавление ТС (авто, мото, коммерческий транспорт) с проверкой уникальности VIN.
- Регистрация владельцев с именем, телефоном и email.
- Создание и снятие объявлений с указанием причины (продано или другое).
- Изменение цены объявления с сохранением истории изменений.
- Поиск по объявлениям:
    - По цене и пробегу.
    - По цвету.
    - По типу ТС (с фильтрами: тип кузова, тип мотоцикла, грузоподъёмность).
    - Общий поиск актуальных объявлений.
- Удобная валидация ввода: при ошибке запрашивается повтор без потери прогресса.

## Требования

- **JDK**: 22 (указан в `jvmToolchain`)
- **Kotlin**: 2.1.0
- **Gradle**: Совместимая версия (рекомендуется последняя)
- **KotlinX Serialization**: 1.8.0 (для работы с JSON)

## Установка и запуск

1. Склонируйте репозиторий:
   ```bash
   git clone https://github.com/MrMikki-boop/CarTrade.git
   ```
2. Переходите в директорию проекта:
   ```bash
   cd CarTrade
   ```
3. Установите зависимости:
   ```bash
   ./gradlew build
   ```
4. Запустите приложение:
   ```bash
   ./gradlew run
   ```

## Структура проекта

- **models/** - модели данных (`Ad`, `Owner`, `Vehicle` с подклассами).
- **services/** - бизнес-логика работы с данными (`AdService`, `OwnerService`, `VehicleService`).
- **storage/** - модуль хранения данных `DataStorage` в JSON. Данные сохраняются в `data.json` в корне проекта.
- **main/** - точка входа с консольным интерфейсом.
