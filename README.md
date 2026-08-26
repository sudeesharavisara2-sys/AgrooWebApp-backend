# 🌾 Agroo - Agricultural Platform Backend

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![JWT](https://img.shields.io/badge/JWT-Authentication-yellow.svg)](https://jwt.io/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

> **A Complete Agricultural Marketplace & Social Platform for Sri Lankan Farmers**

---

## 📋 Table of Contents

* [Overview](#overview)
* [Features](#features)
* [Technology Stack](#technology-stack)
* [Prerequisites](#prerequisites)
* [Installation & Setup](#installation--setup)
* [Database Configuration](#database-configuration)
* [Running the Application](#running-the-application)
* [API Documentation](#api-documentation)
* [Project Structure](#project-structure)
* [Testing](#testing)
* [Deployment](#deployment)
* [Contributing](#contributing)
* [License](#license)

---

## 📖 Overview

**Agroo** is a comprehensive agricultural platform designed for Sri Lankan farmers. The platform connects stakeholders in the agricultural ecosystem and provides a marketplace for buying and selling agricultural products, a social feed for community engagement, real-time chat groups, price tracking, alerts, and an admin dashboard.

### 🌟 Key Highlights

* 🔐 Secure Authentication with JWT + OTP Email Verification
* 🏪 Agricultural Marketplace
* 📱 Social Community Feed
* 💬 Real-time Chat Groups
* ❤️ Comments & Likes
* 👑 Admin Dashboard
* 📊 Price Tracker
* 📢 Pest & Weather Alerts

---

## ✨ Features

### 🔐 Authentication & Authorization

* User Registration with Email OTP Verification
* JWT-based Authentication
* Role-Based Access Control
* Password Change & Reset
* Secure Logout

### 🏪 Agricultural Marketplace

* Multi-category product listings
* Wholesale & retail support
* Product image uploads
* Advanced search and filtering
* Product availability management

### 📱 Social Community Feed

* Create posts with text and media
* Multiple reactions
* Comments and replies
* Public and private post visibility
* Post search

### 💬 Real-time Chat Groups

* Create chat groups
* Multiple admins per group
* Add and remove members
* Leave groups
* Real-time WebSocket messaging

### 👑 Admin Dashboard

* System analytics and statistics
* User management
* Price management
* Alert management
* Content moderation
* Activity logs

### 📊 Price Tracker

* Daily market price entry
* Price comparison across locations
* Historical price tracking

### 📢 Alerts System

* Weather alerts
* Pest and disease alerts
* System notifications
* Urgent and regular alerts

---

## 🛠️ Technology Stack

### Backend

| Technology       | Version | Purpose                        |
| ---------------- | ------- | ------------------------------ |
| Java             | 17      | Core Programming Language      |
| Spring Boot      | 3.2.1   | Application Framework          |
| Spring Security  | 6.2.1   | Authentication & Authorization |
| Spring Data JPA  | 3.2.1   | Database ORM                   |
| Spring WebSocket | 3.2.1   | Real-time Communication        |
| JWT              | 0.12.5  | Token-based Authentication     |
| Hibernate        | 6.4.1   | JPA Implementation             |
| Lombok           | 1.18.30 | Boilerplate Code Reduction     |

### Database

| Technology | Version |
| ---------- | ------- |
| PostgreSQL | 16+     |

### Tools

* Maven
* Git
* Docker
* IntelliJ IDEA
* Postman

---

## 📋 Prerequisites

Before running the project, make sure you have installed:

* Java 17 or higher
* PostgreSQL 16 or higher
* Maven 3.9 or higher
* Git
* IntelliJ IDEA or another Java IDE

---

# 🚀 Installation & Setup

## 1. Clone the Repository

```bash
git clone https://github.com/YOUR_USERNAME/agroo.git
cd agroo
```

## 2. Configure PostgreSQL

Create the database:

```sql
CREATE DATABASE agroo_db;
```

## 3. Configure Application Properties

Update your `application.properties` file:

```properties
server.port=8081

spring.datasource.url=jdbc:postgresql://localhost:5432/agroo_db
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

jwt.secret=YOUR_JWT_SECRET
jwt.expiration=86400000
```

> ⚠️ Never commit real passwords, email passwords, or JWT secrets to GitHub.

## 4. Build the Project

```bash
mvn clean compile
```

---

## ▶️ Running the Application

### Using Maven

```bash
mvn spring-boot:run
```

### Build and Run JAR

```bash
mvn clean package
```

Then:

```bash
java -jar target/agroo-0.0.1-SNAPSHOT.jar
```

The API will run on:

```text
http://localhost:8081
```

---

# 🔗 API Documentation

## Authentication APIs

| Method | Endpoint                    | Description            |
| ------ | --------------------------- | ---------------------- |
| POST   | `/api/auth/register`        | Register a new user    |
| POST   | `/api/auth/verify-otp`      | Verify email OTP       |
| POST   | `/api/auth/resend-otp`      | Resend OTP             |
| POST   | `/api/auth/login`           | Login user             |
| POST   | `/api/auth/forgot-password` | Request password reset |
| POST   | `/api/auth/reset-password`  | Reset password         |
| POST   | `/api/auth/change-password` | Change password        |
| POST   | `/api/auth/logout`          | Logout                 |

## Product APIs

| Method | Endpoint                            | Description              |
| ------ | ----------------------------------- | ------------------------ |
| POST   | `/api/products`                     | Create product           |
| GET    | `/api/products`                     | Get all products         |
| GET    | `/api/products/{id}`                | Get product by ID        |
| GET    | `/api/products/category/{category}` | Get products by category |
| GET    | `/api/products/search`              | Search products          |
| PUT    | `/api/products/{id}`                | Update product           |
| DELETE | `/api/products/{id}`                | Delete product           |

## Social Feed APIs

| Method | Endpoint          | Description    |
| ------ | ----------------- | -------------- |
| POST   | `/api/posts`      | Create post    |
| GET    | `/api/posts`      | Get all posts  |
| GET    | `/api/posts/feed` | Get user feed  |
| GET    | `/api/posts/{id}` | Get post by ID |
| PUT    | `/api/posts/{id}` | Update post    |
| DELETE | `/api/posts/{id}` | Delete post    |

## Chat Group APIs

| Method | Endpoint                                 | Description       |
| ------ | ---------------------------------------- | ----------------- |
| POST   | `/api/groups`                            | Create group      |
| GET    | `/api/groups`                            | Get user groups   |
| GET    | `/api/groups/{groupId}`                  | Get group details |
| POST   | `/api/groups/{groupId}/members`          | Add member        |
| DELETE | `/api/groups/{groupId}/members/{userId}` | Remove member     |
| POST   | `/api/groups/{groupId}/leave`            | Leave group       |

## Admin APIs

| Method | Endpoint                               | Description          |
| ------ | -------------------------------------- | -------------------- |
| GET    | `/api/admin/dashboard`                 | Dashboard statistics |
| GET    | `/api/admin/users`                     | Get all users        |
| PATCH  | `/api/admin/users/{userId}/activate`   | Activate user        |
| PATCH  | `/api/admin/users/{userId}/deactivate` | Deactivate user      |
| PATCH  | `/api/admin/users/{userId}/make-admin` | Make user an admin   |
| DELETE | `/api/admin/users/{userId}`            | Delete user          |
| POST   | `/api/admin/prices`                    | Add market price     |
| GET    | `/api/admin/prices`                    | Get prices           |
| POST   | `/api/admin/alerts`                    | Create alert         |
| GET    | `/api/admin/alerts`                    | Get alerts           |

---

# 📁 Project Structure

```text
agroo/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/agroo/agroo/
│   │   │       ├── config/
│   │   │       ├── controller/
│   │   │       ├── dto/
│   │   │       ├── exception/
│   │   │       ├── model/
│   │   │       ├── repository/
│   │   │       ├── service/
│   │   │       └── util/
│   │   │
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│
├── pom.xml
└── README.md
```

---

# 🧪 Testing

Run all tests:

```bash
mvn test
```

Example health check:

```bash
curl http://localhost:8081/api/test
```

Example login request:

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"test@test.com","password":"password123"}'
```

---

# 🐳 Docker Deployment

## Build the JAR

```bash
mvn clean package
```

## Dockerfile

```dockerfile
FROM openjdk:17-jdk-slim

COPY target/agroo-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## Build Docker Image

```bash
docker build -t agroo-backend .
```

## Run Container

```bash
docker run -p 8081:8081 agroo-backend
```

---

# 🔐 Environment Variables

| Variable        | Description             |
| --------------- | ----------------------- |
| `SERVER_PORT`   | Application server port |
| `DB_URL`        | PostgreSQL database URL |
| `DB_USERNAME`   | Database username       |
| `DB_PASSWORD`   | Database password       |
| `JWT_SECRET`    | JWT secret key          |
| `MAIL_USERNAME` | Email username          |
| `MAIL_PASSWORD` | Email app password      |

---

# 🤝 Contributing

1. Fork the repository.
2. Create a new branch.

```bash
git checkout -b feature/AmazingFeature
```

3. Commit your changes.

```bash
git commit -m "Add some AmazingFeature"
```

4. Push to GitHub.

```bash
git push origin feature/AmazingFeature
```

5. Create a Pull Request.

---

# 📄 License

This project is licensed under the MIT License.

---

# 🌾 Acknowledgments

* Sri Lankan agricultural community
* Spring Boot
* PostgreSQL
* Open-source contributors

---

## ❤️ Built for Sri Lankan Farmers

**Agroo – Connecting Farmers, Buyers, and the Agricultural Community.**
