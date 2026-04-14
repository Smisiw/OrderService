# OrderService

Микросервис управления заказами. Принимает запросы на создание заказов, синхронно резервирует товары через Feign-вызов к ProductService, публикует Kafka-события об оформлении заказа и отмене позиций.

- **Порт:** 8084
- **Имя в Eureka:** `order-service` (строчными буквами)
- **Java:** 17
- **Spring Boot:** 3.4.4

---

## Содержание

- [Назначение](#назначение)
- [API эндпоинты](#api-эндпоинты)
- [Модель данных](#модель-данных)
- [Kafka-интеграция](#kafka-интеграция)
- [Feign-клиент (исходящий)](#feign-клиент-исходящий)
- [Переменные окружения](#переменные-окружения)
- [Сборка и запуск](#сборка-и-запуск)
- [Тесты](#тесты)

---

## Назначение

- Принимает создание заказа (`POST /api/orders/new`).
- Через Feign вызывает ProductService для проверки наличия и резервирования товаров.
- При успешном резервировании сохраняет заказ в PostgreSQL и публикует событие `order.created` в Kafka.
- CartService получает это событие и автоматически очищает корзину пользователя.
- При отмене позиции публикует событие `order.item.cancelled` — ProductService снимает резерв.

---

## API эндпоинты

Доступны через Gateway: `http://localhost:8080`. Пометка `[AUTH]` — требуется JWT-токен.

| Метод | Путь               | Auth   | Описание                                                           |
|-------|--------------------|--------|--------------------------------------------------------------------|
| POST  | `/api/orders/new`  | [AUTH] | Создать заказ                                                      |
| GET   | `/api/orders/{id}` | [AUTH] | Получить заказ по UUID (доступно владельцу или пользователю с `ROLE_ADMIN`) |

### Тело запроса `POST /api/orders/new`

```json
{
  "items": [
    {
      "productVariationId": "uuid-вариации",
      "quantity": 2,
      "price": 1500.00
    }
  ]
}
```

### Структура ответа `GET /api/orders/{id}`

Возвращает `OrderResponseDto` с полями заказа (`OrderStatus`) и списком позиций (`OrderItemResponseDto`) с их `OrderItemStatus`.

### HTTP-коды ошибок

| Код | Описание                                          |
|-----|---------------------------------------------------|
| 403 | Запрос на чужой заказ без роли `ROLE_ADMIN`       |
| 404 | Заказ не найден                                   |
| 400 | Ошибка резервирования товара (нет на складе и т.п.)|

---

## Модель данных

| Сущность       | Поля                                                          | Описание                           |
|----------------|---------------------------------------------------------------|------------------------------------|
| `Order`        | `id` (UUID), `userId` (UUID), `status` (OrderStatus), `createdAt` | Заказ                        |
| `OrderItem`    | `id`, `productVariationId`, `quantity`, `price`, `status` (OrderItemStatus) | Позиция заказа  |

### Статусы заказа (`OrderStatus`)

| Статус      | Описание                  |
|-------------|---------------------------|
| `PENDING`   | Создан, ожидает оплаты    |
| `PAID`      | Оплачен                   |
| `COMPLETED` | Завершён                  |
| `CANCELLED` | Отменён                   |

### Статусы позиции заказа (`OrderItemStatus`)

| Статус       | Описание                              |
|--------------|---------------------------------------|
| `RESERVED`   | Зарезервирован на складе              |
| `PAID`       | Оплачен                               |
| `ASSEMBLING` | Собирается                            |
| `DELIVERING` | Доставляется                          |
| `DELIVERED`  | Доставлен                             |
| `CANCELLED`  | Отменён                               |
| `RETURNED`   | Возвращён                             |

Схема управляется JPA (`ddl-auto: update`). Flyway не настроен в данном сервисе.

---

## Kafka-интеграция

**Продюсер** публикует два типа событий:

| Топик                  | Событие                    | Потребители                     |
|------------------------|----------------------------|---------------------------------|
| `order.created`        | `OrderCreatedEvent`        | CartService, ProductService     |
| `order.item.cancelled` | `OrderItemCancelledEvent`  | ProductService                  |

`OrderCreatedEvent` содержит `orderId`, `userId` и список позиций с `productVariationId`.

---

## Feign-клиент (исходящий)

OrderService вызывает ProductService для резервирования товаров.

| Интерфейс              | Целевой сервис    | Метод | Эндпоинт                          |
|------------------------|-------------------|-------|-----------------------------------|
| `ProductServiceClient` | `PRODUCT-SERVICE` | POST  | `/api/products/checkAndReserve`   |

Тайм-ауты Feign: `connectTimeout = 5000 мс`, `readTimeout = 5000 мс`.

---

## Переменные окружения

| Переменная                | По умолчанию (dev)                                 | Описание                  |
|---------------------------|----------------------------------------------------|---------------------------|
| `DB_URL`                  | `jdbc:postgresql://localhost:5436/order_db`        | JDBC-URL PostgreSQL       |
| `DB_USERNAME`             | `user`                                             | Пользователь БД           |
| `DB_PASSWORD`             | `password`                                         | Пароль БД                 |
| `JWT_SECRET`              | `nTDmGYqtvLfDCptgzwG+xKGtXV/JHL4fHKJrxK9tHdI=`   | Ключ проверки JWT         |
| `KAFKA_BOOTSTRAP_SERVERS` | `http://localhost:9092`                            | Адрес Kafka-брокера       |
| `EUREKA_URL`              | `http://localhost:8761/eureka/`                    | Адрес Eureka Discovery    |

---

## Сборка и запуск

### Через Docker Compose

```bash
cd MarketPlaceProject
docker compose -f docker-compose.dev.yml up --build order-service order-db -d
```

### Локально из исходников

Требования: JDK 17, PostgreSQL 15 (база `order_db`), Kafka, запущенный ProductService.

```bash
cd OrderService
KAFKA_BOOTSTRAP_SERVERS=localhost:9092 \
./gradlew bootRun
```

### Сборка JAR

```bash
cd OrderService
./gradlew build
```

---

## Тесты

```bash
cd OrderService
./gradlew test
```

Тестовые классы в `src/test/java/ru/projects/order_service/`:
- `service/OrderServiceTest.java`

Health-check endpoint: `GET http://localhost:8084/actuator/health`
