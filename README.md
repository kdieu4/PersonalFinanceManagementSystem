# Personal Finance Management System

> Hệ thống quản lý tài chính cá nhân — Backend RESTful API

---

## Mục lục

- [Yêu cầu môi trường](#yêu-cầu-môi-trường)
- [Cài đặt](#cài-đặt)
- [Cách chạy](#cách-chạy)
    - [Chạy bằng Docker Compose (khuyến nghị)](#chạy-bằng-docker-compose-khuyến-nghị)
    - [Chạy thủ công (local)](#chạy-thủ-công-local)
- [Biến cấu hình](#biến-cấu-hình)
- [Tài khoản test](#tài-khoản-test)
- [API Documentation](#api-documentation)
- [Cấu trúc dự án](#cấu-trúc-dự-án)

---

## Yêu cầu môi trường

| Phần mềm       | Phiên bản tối thiểu | Ghi chú                          |
|----------------|---------------------|----------------------------------|
| Java (JDK)     | 21 LTS              | Khuyến nghị Eclipse Temurin      |
| Maven          | 3.9+                | Hoặc dùng `./mvnw` đi kèm        |
| PostgreSQL     | 18                  | Hoặc dùng Docker container       |
| Redis          | 7.x                 | Hoặc dùng Docker container       |
| Apache Kafka   | 4.3.1               | Hoặc dùng Docker container       |
| Docker         | 24+                 | Cần nếu chạy bằng Docker Compose |
| Docker Compose | 2.20+               | Đi kèm Docker Desktop            |

---

## Cài đặt

### 1. Clone repository

```bash
git clone https://github.com/kdieu4/PersonalFinanceManagementSystem.git
cd PersonalFinanceManagementSystem
```

### 2. Tạo file `.env`

Tạo file `.env` ở thư mục gốc của dự án (file này đã có trong `.gitignore`):

```bash
cp .env.example .env
```

Hoặc tạo thủ công với nội dung:

```properties
# Database
DB_USERNAME=postgres
DB_PASSWORD=123456
# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
# Email (Gmail App Password)
MAIL_PASSWORD=<your_gmail_app_password>
# Spring Security default user
USER_PASSWORD=<your_secure_password>
# AWS SNS (tùy chọn — chưa kích hoạt)
AWS_SNS_ACCESS_KEY=<your_access_key>
AWS_SNS_SECRET_KEY=<your_secret_key>
```

> **Lưu ý:** `MAIL_PASSWORD` cần là [App Password](https://support.google.com/accounts/answer/185833) của Gmail, không
> phải mật khẩu tài khoản Google.

---

## Cách chạy

### Chạy bằng Docker Compose (khuyến nghị)

Cách này khởi động toàn bộ hệ thống (PostgreSQL, Redis, Kafka, Backend) trong Docker.

```bash
# Khởi động tất cả services
docker compose up -d

# Kiểm tra trạng thái
docker compose ps

# Xem logs của backend
docker compose logs -f pfms-backend

# Dừng tất cả
docker compose down
```

| Service     | Port nội bộ | Port máy host |
|-------------|:-----------:|:-------------:|
| Backend API |    8080     |   **8081**    |
| PostgreSQL  |    5432     |     5432      |
| Redis       |    6379     |     6379      |
| Kafka       |    9092     |     9092      |

Sau khi chạy, API có thể truy cập tại: `http://localhost:8081/api/v1/`

---

### Chạy thủ công (local)

Yêu cầu: PostgreSQL, Redis, và Kafka đã chạy trên máy (hoặc chỉ chạy các services infra bằng Docker).

#### Bước 1 — Khởi động infrastructure bằng Docker (nếu chưa có sẵn)

```bash
docker compose up -d pfms-db redis kafka
```

#### Bước 2 — Chạy ứng dụng Spring Boot

```bash
./mvnw spring-boot:run
```

Hoặc build JAR rồi chạy:

```bash
./mvnw clean package -Dmaven.test.skip=true
java -jar target/PersonalFinanceManagementSystem-0.0.1-SNAPSHOT.jar
```

Sau khi chạy, API có thể truy cập tại: `http://localhost:8080/api/v1/`

---

## Biến cấu hình

### File `.env` (thư mục gốc)

| Biến            | Giá trị mặc định | Mô tả                                 |
|-----------------|------------------|---------------------------------------|
| `DB_USERNAME`   | `postgres`       | Tên người dùng PostgreSQL             |
| `DB_PASSWORD`   | —                | Mật khẩu PostgreSQL                   |
| `MAIL_PASSWORD` | —                | Gmail App Password cho gửi email OTP  |
| `USER_PASSWORD` | —                | Mật khẩu Spring Security default user |

### File `application.properties` (cấu hình nội bộ)

| Cấu hình                        | Giá trị                | Mô tả                                  |
|---------------------------------|------------------------|----------------------------------------|
| `spring.profiles.active`        | `dev`                  | Profile đang sử dụng                   |
| `spring.jpa.hibernate.ddl-auto` | `validate`             | Chỉ validate schema, không tự tạo bảng |
| `spring.flyway.enabled`         | `true`                 | Bật Flyway migration tự động           |
| `spring.mail.host`              | `smtp.gmail.com`       | SMTP server                            |
| `spring.mail.port`              | `587`                  | SMTP port (STARTTLS)                   |
| `security.public-endpoints`     | `/api/v1/auth/**`, ... | Các endpoint không yêu cầu xác thực    |

### File `application-dev.properties` (profile dev)

| Cấu hình                 | Giá trị      | Mô tả                            |
|--------------------------|--------------|----------------------------------|
| `jwt.access.expiration`  | `300000`     | Access token hết hạn sau 5 phút  |
| `jwt.refresh.expiration` | `604800000`  | Refresh token hết hạn sau 7 ngày |
| `jwt.reset.expiration`   | `1209600000` | Reset token hết hạn sau 14 ngày  |
| `jwt.secretKey`          | `9a4f2c...`  | Khóa bí mật ký JWT (chỉ cho dev) |

---

## Tài khoản test

### Spring Security Default User

| Trường   | Giá trị                          |
|----------|----------------------------------|
| Username | `DieuHoang`                      |
| Password | Giá trị của biến `USER_PASSWORD` |

### Tạo tài khoản End User qua API

Đăng ký tài khoản mới bằng cách gọi API:

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test@123456",
    "fullName": "Nguyen Van Test"
  }'
```

Sau khi đăng ký, đăng nhập để lấy JWT:

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test@123456"
  }'
```

Response sẽ chứa `accessToken` và `refreshToken` để gọi các API khác.

### Database mặc định (Docker Compose)

| Trường   | Giá trị                              |
|----------|--------------------------------------|
| Host     | `localhost`                          |
| Port     | `5432`                               |
| Database | `personal_finance_management_system` |
| Username | `postgres`                           |
| Password | `123456`                             |

---

## API Documentation

Sau khi chạy ứng dụng, truy cập Swagger UI:

| Tài liệu     | URL                                         |
|--------------|---------------------------------------------|
| Swagger UI   | http://localhost:8080/swagger-ui/index.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs           |
| OpenAPI YAML | http://localhost:8080/v3/api-docs.yaml      |

> Khi chạy bằng Docker Compose, thay `8080` bằng `8081`.

---

## Cấu trúc dự án

```
PersonalFinanceManagementSystem/
├── docs/                          # Tài liệu hệ thống
│   ├── SSD.md                     # System Software Design
│   ├── API-Specification.yaml     # OpenAPI Specification
│   └── requirements.md            # Tài liệu yêu cầu
├── src/main/java/.../
│   ├── controller/                # REST Controllers (API Layer)
│   ├── service/                   # Business logic (interfaces)
│   │   └── impl/                  # Service implementations
│   ├── repository/                # Spring Data JPA Repositories
│   ├── domain/
│   │   ├── entity/                # JPA Entities
│   │   └── dto/                   # Request/Response DTOs
│   ├── security/                  # JWT filter, UserDetails
│   ├── exception/                 # Global exception handling
│   └── constant/                  # Constants (URLs, messages)
├── src/main/resources/
│   ├── application.properties     # Cấu hình chính
│   ├── application-dev.properties # Cấu hình dev profile
│   ├── db/migration/              # Flyway SQL migrations
│   └── templates/                 # Email templates (Thymeleaf)
├── docker-compose.yml             # Docker Compose (full stack)
├── Dockerfile                     # Multi-stage Docker build
├── pom.xml                        # Maven dependencies
└── .env                           # Biến môi trường (gitignored)
```