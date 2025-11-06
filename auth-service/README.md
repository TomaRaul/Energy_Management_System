# 🔐 Auth Service - Authentication Microservice

## 📋 Overview

Serviciu de autentificare simplu pentru Energy Management System.

**Database:** `DS_EMS_CREDENTIAL` (PostgreSQL)  
**Table:** `Credentials` (id, username, password)  
**Port:** 8084

## 🎯 Endpoints

### POST /auth/login
Login cu username și password.

**Request:**
```json
{
  "username": "tomarm",
  "password": "tomarm"
}
```

**Response (Success - 200):**
```json
{
  "message": "Login successful",
  "username": "tomarm",
  "success": true
}
```

**Response (Failed - 401):**
```json
{
  "message": "Invalid username or password",
  "username": null,
  "success": false
}
```

### POST /auth/register
Register un user nou.

**Request:**
```json
{
  "username": "newuser",
  "password": "password123"
}
```

**Response (Success - 200):**
```json
{
  "message": "Registration successful",
  "username": "newuser",
  "success": true
}
```

**Response (Failed - 400):**
```json
{
  "message": "Username already exists",
  "username": null,
  "success": false
}
```

### GET /auth/health
Health check endpoint.

**Response:**
```
Auth Service is running
```

## 🚀 Rulare în IntelliJ

### Setup

1. **Deschide folderul în IntelliJ:**
   - File → Open → Selectează folderul `auth-service`

2. **Asigură-te că database-ul rulează:**
```bash
docker-compose up -d credential-db
```

3. **Verifică conexiunea:**
```bash
psql -h localhost -p 5434 -U tomarm -d DS_EMS_CREDENTIAL
# Password: cara2016
```

4. **Configurează Environment Variables în IntelliJ:**
   - Run → Edit Configurations
   - Adaugă environment variables:
     ```
     SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5434/DS_EMS_CREDENTIAL
     SPRING_DATASOURCE_USERNAME=tomarm
     SPRING_DATASOURCE_PASSWORD=cara2016
     PORT=8084
     ```

5. **Run Application:**
   - Click dreapta pe `AuthServiceApplication.java`
   - Run 'AuthServiceApplication'

## 🐳 Rulare cu Docker

### Cu docker-compose:

```bash
# În root directory
docker-compose -f docker-compose-with-auth.yml up -d --build
```

### Direct cu Docker:

```bash
cd auth-service

# Build
docker build -t auth-service .

# Run
docker run -p 8084:8084 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5434/DS_EMS_CREDENTIAL \
  -e SPRING_DATASOURCE_USERNAME=tomarm \
  -e SPRING_DATASOURCE_PASSWORD=cara2016 \
  -e PORT=8084 \
  auth-service
```

## 🧪 Testare

### Test Login (user existent):
```bash
curl -X POST http://localhost:8084/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"tomarm","password":"tomarm"}'
```

### Test Register:
```bash
curl -X POST http://localhost:8084/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"test123"}'
```

### Test Health:
```bash
curl http://localhost:8084/auth/health
```

### Prin Traefik (port 80):
```bash
curl -X POST http://localhost:80/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"tomarm","password":"tomarm"}'
```

## 📦 Structura Proiectului

```
auth-service/
├── pom.xml
├── Dockerfile
├── src/
│   └── main/
│       ├── java/
│       │   └── com/ds/ems/
│       │       ├── AuthServiceApplication.java
│       │       ├── controllers/
│       │       │   └── AuthController.java
│       │       ├── services/
│       │       │   └── AuthService.java
│       │       ├── entities/
│       │       │   └── Credentials.java
│       │       ├── repositories/
│       │       │   └── CredentialsRepository.java
│       │       └── dtos/
│       │           ├── LoginRequest.java
│       │           └── LoginResponse.java
│       └── resources/
│           └── application.properties
└── README.md
```

## 🔧 Build în IntelliJ

### Maven Build:

1. Deschide Maven tool window (View → Tool Windows → Maven)
2. Expand auth-service → Lifecycle
3. Click pe `clean` apoi `package`

SAU din terminal:
```bash
cd auth-service
mvn clean package
```

JAR-ul va fi în: `target/auth-service-1.0.0.jar`

### Run JAR:
```bash
java -jar target/auth-service-1.0.0.jar \
  --spring.datasource.url=jdbc:postgresql://localhost:5434/DS_EMS_CREDENTIAL \
  --spring.datasource.username=tomarm \
  --spring.datasource.password=cara2016 \
  --server.port=8084
```

## 🗄️ Database Setup

Tabela `Credentials` va fi creată automat de Hibernate (`ddl-auto=update`).

**Dacă vrei să o creezi manual:**

```sql
CREATE TABLE "Credentials" (
    id SERIAL PRIMARY KEY,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL
);

-- Insert test data
INSERT INTO "Credentials" (username, password) VALUES 
('tomarm', 'tomarm'),
('tomacr', 'tomacr');
```

## ⚠️ Note Importante

1. **Password Storage:** Acest serviciu stochează parole în **plain text** pentru simplitate.  
   În producție TREBUIE folosit **BCrypt** sau alt algoritm de hashing!

2. **CORS:** Controller-ul are `@CrossOrigin(origins = "*")` pentru development.  
   În producție, specifică origin-urile exacte!

3. **Validation:** Se validează doar că username și password sunt non-blank.  
   Poți adăuga mai multe validări (lungime minimă, etc.)

## 🔗 Integrare cu Celelalte Servicii

Auth service se accesează prin Traefik la:
```
http://localhost:80/auth/login
http://localhost:80/auth/register
```

Frontend-ul poate face requests:
```javascript
// În frontend/src/services/api.js
export const authAPI = {
  login: (credentials) => api.post('/api/auth/login', credentials),
  register: (credentials) => api.post('/api/auth/register', credentials)
};
```

## ✅ Verificare Setup

```bash
# 1. Database rulează
docker ps | grep credential-db

# 2. Serviciul pornește
curl http://localhost:8084/auth/health

# 3. Login funcționează
curl -X POST http://localhost:8084/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"tomarm","password":"tomarm"}'

# 4. Prin Traefik funcționează
curl -X POST http://localhost:80/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"tomarm","password":"tomarm"}'
```

## 🎓 Pentru Assignment

Acest serviciu îndeplinește cerința de **Authentication Service** din assignment:
- ✅ Handles user login
- ✅ Stores credentials în Credential Database
- ✅ Returns response (success/failure)
- ✅ Containerized cu Docker
- ✅ Integrare cu Traefik

---

**🚀 Gata de build în IntelliJ!**
