# 💳 Transaction Service Application

[![Java](https://img.shields.io/badge/Java-17%2B-blue)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Compose-blue)](https://www.docker.com/)
[![Build](https://img.shields.io/badge/Build-Maven-orange)](https://maven.apache.org/)

A Spring Boot microservice for **managing accounts and transactions**.  
It leverages **Spring Data JPA**, **MySQL**, **Testcontainers**, and **Docker Compose**.

---

## 📚 Table of Contents

- [Frameworks Used](#-frameworks-used)
- [Maven Dependencies](#-maven-dependencies)
- [Quick Start](#-quick-start)
- [Local Development](#-local-development)
- [Production Setup](#-production-setup)
- [API Endpoints](#-api-endpoints)
- [Database Details](#-database-details)
- [Running Tests](#-running-tests)
- [Arch Diagrams](#-arch-diagrams)

---

## 🛠 Frameworks Used

- **Spring Boot** — Backend application framework
- **Spring Data JPA** — ORM for database interactions
- **Testcontainers** — Integration testing with containerized MySQL
- **MockMvc** — Unit testing Spring MVC controllers
- **Springdoc OpenAPI** — Auto-generated Swagger documentation
- **Docker Compose** — Container orchestration
- **Lombok** — Reduces boilerplate code

---

## 📦 Maven Dependencies

The Maven dependency configurations can be found in:

```
pom.xml
```

---

## 🚀 Quick Start

```bash
# 1️⃣ Clone repository and it's currently having single branch to avoid any issues which main for now local test and production
git clone https://github.com/Knlsharma/transaction-service
cd transaction-service

# 2️⃣ Start services (Docker Compose - prod profile)
./start.sh

# 3️⃣ Access Swagger UI
http://127.0.0.1:8080/swagger-ui/index.html#/
```

---

## 🖥 Local Development

Run with **Testcontainers** (local profile):

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

This will:
- Spin up MySQL Testcontainer automatically
- Load `src/resources/create_sample_data.sql` into DB

---

## 🏭 Production Setup

Run with **Docker Compose** (prod profile):

```bash
./start.sh
```

**Stop (preserve data)**
```bash
./stop.sh
```

**Full reset (delete volumes)**
```bash
./stop.sh && docker-compose down -v
```

---

## 📌 API Endpoints

### 🗂 Account Management

| Endpoint                | Method | Description         | Request Body                                    | Success Response |
|-------------------------|--------|--------------------|-------------------------------------------------|------------------|
| `/accounts`             | POST   | Create new account | `{ "document_number": "12345678900" }`          | 201 Created      |
| `/accounts/{accountId}` | GET    | Get account details| -                                               | 200 OK           |

### 💰 Transaction Processing

| Endpoint                               | Method | Description                    | Request Body                                                              | Success Response |
|----------------------------------------|--------|--------------------------------|---------------------------------------------------------------------------|------------------|
| `/transactions`                        | POST   | Create new transaction         | `{ "account_id": 1, "operation_type_id": 4, "amount": 100.00 }`           | 201 Created      |
| `/transactions/{transactionId}`        | GET    | Get transaction details        | -                                                                         | 200 OK           |
| `/transactions/account/{accountId}`    | GET    | Get transactions for account   | -                                                                         | 200 OK           |

---

## 🗄 Database Details

| Item            | Value         |
|-----------------|--------------|
| Host            | `localhost`  |
| Port            | `3306`       |
| User            | `root`       |
| Password        | `root` (default for local/test) |
| Database Name   | `transactions_db` |

---

## 🧪 Running Tests

### Unit tests (MockMvc)
```bash
./mvnw test -Dtest=com.transaction_service.app.integration_and_unit.AccountTests
```

### Integration tests (Testcontainers)
```bash
./mvnw test -Dtest=com.transaction_service.app.integration_and_unit.TransactionTests
```

### All tests
```bash
sh run-tests.sh
```

---

## 🔍 Useful Links

- **Swagger UI:** [http://127.0.0.1:8080/swagger-ui/index.html#/](http://127.0.0.1:8080/swagger-ui/index.html#/)
- **Health Check:** [http://127.0.0.1:8080/actuator/health](http://127.0.0.1:8080/actuator/health)  



## 🧪 Arch Diagrams

![Diagram1.png](/app/img1.png)

![Diagram2.png](/app/img2.png)
