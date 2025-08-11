# 💳 Transaction Service Application

[![Java](https://img.shields.io/badge/Java-17%2B-blue)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Compose-blue)](https://www.docker.com/)
[![Build](https://img.shields.io/badge/Build-Maven-orange)](https://maven.apache.org/)

A Spring Boot microservice for **managing accounts and transactions**.  
It leverages **Spring Data JPA**, **MySQL**, **Testcontainers**, and **Docker Compose**.

---

## 📚 Table of Contents

1. [Prerequisites](#-prerequisites)
2. [Frameworks Used](#-frameworks-used)
3. [Maven Dependencies](#-maven-dependencies)
4. [Quick Start](#-quick-start)
5. [Production Setup (Recommended)](#-production-setup-profile-prod-with-docker-compose)
6. [Prod Deployment (Direct Run)](#-prod-deployment)
7. [API Endpoints](#-api-endpoints)
8. [cURL Examples](#-curl-examples)
9. [Running Tests](#-running-tests)
10. [Useful Links](#-useful-links)
11. [Arch Diagrams](#-arch-diagrams)

---

## 🛠 Prerequisites

Ensure the following are installed on your system before starting:

- **Java 17+** → [Download](https://adoptium.net/) *(Required)*
- **Maven 3.8+** → [Download](https://maven.apache.org/download.cgi)
- **Docker & Docker Compose** → [Install](https://docs.docker.com/get-docker/) *(Required)*
- **Git** → [Download](https://git-scm.com/downloads) *(Required)*

You can verify installation with:
```
java -version
mvn -version  # or mvn -version if Maven installed globally
docker --version
docker compose version
git --version
```

---

## 🛠 Frameworks Used

- **Maven** - Maven tool for building
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
app/pom.xml
```

---

## 🚀 Quick Start

### 1️⃣ Clone repository
> **Note:** This project uses a single branch (`main`). After cloning, you’ll already be on `main` by default, so you usually don’t need to switch branches — the `git checkout main` below is just for safety.

```
git clone https://github.com/Knlsharma/transaction-service
cd transaction-service
git checkout main
cd app
```

### 2️⃣ Choose a profile

- **`local` profile** → Runs with **Testcontainers** (spins up MySQL in Docker automatically for development/testing).
- **`prod` profile** → Runs with **Docker Compose** for production-like setup.

---

## 🏭 Production Setup (Profile: `prod` with Docker Compose) — **Recommended**

Run with **Docker Compose**:

```
cd app
./start.sh
```

**Stop (preserve data)**
```
cd app
./stop.sh
```

**Full reset (delete volumes & data)**
```
cd app
./stop.sh && docker compose down -v
```

---

## 🖥 Prod Deployment (Profile: `prod` Direct Run)

```
cd app
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

> **Note:** This runs using the configuration intended for production but without Docker Compose orchestration.

---

## 📌 API Endpoints

### 🗂 Account Management

| Endpoint                | Method | Description         | Request Body                                    | Success Response |
|-------------------------|--------|--------------------|-------------------------------------------------|------------------|
| `/accounts`             | POST   | Create new account | `{ "document_number": "12345678900" }`          | 201 Created      |
| `/accounts/{accountId}` | GET    | Get account details| –                                               | 200 OK           |

### 💰 Transaction Processing

| Endpoint                               | Method | Description                    | Request Body                                                              | Success Response |
|----------------------------------------|--------|--------------------------------|---------------------------------------------------------------------------|------------------|
| `/transactions`                        | POST   | Create new transaction         | `{ "account_id": 1, "operation_type_id": 4, "amount": 100.00 }`           | 201 Created      |
| `/transactions/{transactionId}`        | GET    | Get transaction details        | –                                                                         | 200 OK           |
| `/transactions/account/{accountId}`    | GET    | Get transactions for account   | –                                                                         | 200 OK           |

---

## 📡 cURL Examples

### Create Account
```
curl -X POST "http://127.0.0.1:8080/accounts" \
     -H "Content-Type: application/json" \
     -d '{"document_number": "12345678900"}'
```

### Get Account by ID
```
curl -X GET "http://127.0.0.1:8080/accounts/1"
```

### Create Transaction
```
curl -X POST "http://127.0.0.1:8080/transactions" \
     -H "Content-Type: application/json" \
     -d '{"account_id": 1, "operation_type_id": 4, "amount": 100.00}'
```

### Get Transaction by ID
```
curl -X GET "http://127.0.0.1:8080/transactions/1"
```

### Get All Transactions for Account
```
curl -X GET "http://127.0.0.1:8080/transactions/account/1"
```

### Health Check
```
curl -X GET "http://127.0.0.1:8080/actuator/health"
```

### All Actuator Endpoints
```
curl -X GET "http://127.0.0.1:8080/actuator"
```

---

## 🧪 Running Tests

### Integration tests for Accounts (Testcontainers)
```
cd app
mvn -Dtest=com.transaction_service.app.integration_and_unit.AccountTests test
```

### Integration tests for Transactions (Testcontainers)
```
cd app
mvn -Dtest=com.transaction_service.app.integration_and_unit.TransactionTests test
```

### All tests
```
cd app
sh run-tests.sh
```

---

## 🔍 Useful Links

- **Swagger UI:** [http://127.0.0.1:8080/swagger-ui/index.html#/](http://127.0.0.1:8080/swagger-ui/index.html#/)
- **Health Check:** [http://127.0.0.1:8080/actuator/health](http://127.0.0.1:8080/actuator/health)

---

## 🧪 Arch Diagrams

![Diagram1.png](/app/img1.png)

![Diagram2.png](/app/img2.png)
