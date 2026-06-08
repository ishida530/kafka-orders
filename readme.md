# Order Notifier

Aplikacja do obsługi powiadomień e-mail dla zamówień e-commerce.
Przyjmuje zamówienia przez REST API, kolejkuje je przez Apache Kafka
i wysyła powiadomienia e-mail (mock).

## Architektura

```
┌─────────────────────────────────────────────────────────────┐
│                        REST CLIENT                          │
└─────────────────────┬───────────────────────────────────────┘
                      │ POST /api/orders
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                   ORDER CONTROLLER                          │
│                  (Rate Limiting)                            │
└──────────┬──────────────────────────┬───────────────────────┘
           │                          │
           ▼                          ▼
┌──────────────────┐      ┌───────────────────────┐
│   PostgreSQL     │      │    Kafka Producer      │
│  (Audit Log)     │      │  topic: order-events   │
│ status: RECEIVED │      └───────────┬────────────┘
└──────────────────┘                  │
                                      ▼
                          ┌───────────────────────┐
                          │    Kafka Consumer      │
                          │   (Rate Limiting)      │
                          └───────────┬────────────┘
                                      │
                          ┌───────────┴────────────┐
                          ▼                        ▼
              ┌──────────────────┐    ┌────────────────────┐
              │  Email Mock      │    │    PostgreSQL       │
              │  (symulacja      │    │  status: NOTIFIED   │
              │   wysyłki)       │    │  lub FAILED         │
              └──────────────────┘    └────────────────────┘
```

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

**Health check:**
```bash
curl http://localhost:8080/actuator/health
```

**Wysłanie zamówienia:**
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

**Aktualizacja statusu (to samo zamówienie, nowy status):**
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "trackingNumber": "TRK-001",
    "recipientEmail": "test@example.com",
    "recipientCountryCode": "PL",
    "senderCountryCode": "DE",
    "statusCode": 75
  }'
```

---

## Wdrożenie produkcyjne

**URL:** `https://order-notifier-qfbh.onrender.com`

> ⚠️ Darmowy plan Render usypia instancję po bezczynności.
> Pierwsze zapytanie może zająć ~50 sekund.

### Testowanie produkcji

**Health check:**
```bash
curl https://order-notifier-qfbh.onrender.com/actuator/health
```

**Wysłanie zamówienia:**
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

**Aktualizacja statusu tego samego zamówienia:**
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

**Test rate limitingu (429):**
```bash
for i in {1..105}; do
  curl -s -o /dev/null -w "%{http_code}\n" \
    -X POST https://order-notifier-qfbh.onrender.com/api/orders \
    -H "Content-Type: application/json" \
    -d '{"trackingNumber":"TRK-RATE","recipientEmail":"test@example.com","recipientCountryCode":"PL","senderCountryCode":"DE","statusCode":1}'
done
```

### Testowanie w Postman

1. Utwórz nowy request `POST`
2. URL: `https://order-notifier-qfbh.onrender.com/api/orders`
3. Headers: `Content-Type: application/json`
4. Body → raw → JSON:
```json
{
  "trackingNumber": "TRK-PROD-001",
  "recipientEmail": "test@example.com",
  "recipientCountryCode": "PL",
  "senderCountryCode": "DE",
  "statusCode": 25
}
```

---

## Zmienne środowiskowe (produkcja)

| Zmienna | Opis |
|---|---|
| `DATABASE_URL` | JDBC URL do PostgreSQL (Render) |
| `DATABASE_USERNAME` | Użytkownik bazy danych |
| `DATABASE_PASSWORD` | Hasło bazy danych |
| `KAFKA_BOOTSTRAP_SERVERS` | Adres klastra Kafka (Aiven) |
| `KAFKA_CA_CERT` | Certyfikat CA (PEM) |
| `KAFKA_CLIENT_CERT` | Certyfikat klienta (PEM) |
| `KAFKA_CLIENT_KEY` | Klucz prywatny klienta (PEM) |
| `SPRING_PROFILES_ACTIVE` | `prod` |

---

## Technologie

| Technologia | Zastosowanie |
|---|---|
| Java 11 | Język programowania |
| Spring Boot 2.7 | Framework aplikacji |
| Apache Kafka (Aiven) | Broker komunikatów |
| PostgreSQL (Render) | Baza danych / audit log |
| Docker / Docker Compose | Konteneryzacja |
| Bucket4j | Rate limiting (API + email) |

---

## Funkcjonalności

- ✅ REST API przyjmujące zamówienia z walidacją
- ✅ Zapis każdego żądania do bazy danych (audit log)
- ✅ Kolejkowanie przez Apache Kafka
- ✅ Mock wysyłki e-mail z pełnymi danymi zamówienia
- ✅ Rate limiting na API (100 req/s) i wysyłce emaili (10/s)
- ✅ Aktualizacja statusu zamówienia (RECEIVED → NOTIFIED/FAILED)
- ✅ Health check endpoint