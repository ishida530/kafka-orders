## Uruchomienie lokalne (docker-compose)

### Wymagania
- Docker + Docker Compose
- Java 11
- Maven

### Start

```bash
docker-compose up --build
```

Aplikacja dostępna pod: `http://localhost:8080`

### Testowanie lokalne

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "trackingNumber": "TRK-001",
    "recipientEmail": "test@example.com",
    "recipientCountryCode": "PL",
    "senderCountryCode": "DE",
    "statusCode": 10
  }'
```

## Wdrożenie produkcyjne

URL: `https://order-notifier-qfbh.onrender.com`

> ⚠️ Darmowy plan Render usypia instancję po bezczynności.
> Pierwsze zapytanie może zająć ~50 sekund.

### Testowanie produkcji

```bash
curl -X POST https://order-notifier-qfbh.onrender.com/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "trackingNumber": "TRK-PROD-001",
    "recipientEmail": "test@example.com",
    "recipientCountryCode": "PL",
    "senderCountryCode": "DE",
    "statusCode": 25
  }'
```

### Health check

```bash
curl https://order-notifier-qfbh.onrender.com/actuator/health
```

### Aktualizacja statusu zamówienia

```bash
curl -X POST https://order-notifier-qfbh.onrender.com/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "trackingNumber": "TRK-PROD-001",
    "recipientEmail": "test@example.com",
    "recipientCountryCode": "PL",
    "senderCountryCode": "DE",
    "statusCode": 75
  }'
```

## Zmienne środowiskowe (produkcja)

| Zmienna | Opis |
|---|---|
| `DATABASE_URL` | JDBC URL do PostgreSQL |
| `DATABASE_USERNAME` | Użytkownik bazy |
| `DATABASE_PASSWORD` | Hasło bazy |
| `KAFKA_BOOTSTRAP_SERVERS` | Adres klastra Kafka (Aiven) |
| `KAFKA_CA_CERT` | Certyfikat CA (pem) |
| `SPRING_PROFILES_ACTIVE` | `prod` |

## Technologie

- Java 11
- Spring Boot 2.7
- Apache Kafka (Aiven)
- PostgreSQL
- Docker / Docker Compose
- Bucket4j (rate limiting)