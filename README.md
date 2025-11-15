# Energy Management System (EMS)

## Descriere

Sistem distribuit de management energetic bazat pe arhitectura microservicii, dezvoltat ca proiect pentru cursul de Sisteme Distribuite, Universitatea Tehnica Cluj-Napoca, 2025.

EMS permite gestionarea utilizatorilor si a dispozitivelor de consum energetic, oferind functionalitati de autentificare, administrare si monitorizare bazate pe roluri (Administrator/Client).

## Arhitectura

Sistemul este construit folosind arhitectura microservicii cu urmatoarele componente:

```
┌─────────────────────────────────────────────────────────────────┐
│                        Browser/Client                            │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ↓
┌─────────────────────────────────────────────────────────────────┐
│                    Frontend (React + Nginx)                      │
│                         Port: 80                                 │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ↓
┌─────────────────────────────────────────────────────────────────┐
│                  Traefik API Gateway                             │
│              Ports: 80, 8000, 8085 (Dashboard)                   │
└────┬──────────────────┬────────────────────┬────────────────────┘
     │                  │                    │
     ↓                  ↓                    ↓
┌─────────┐      ┌─────────────┐      ┌────────────┐
│  Auth   │      │    User     │      │   Device   │
│ Service │      │   Service   │      │  Service   │
│:8084    │      │   :8082     │      │   :8083    │
└────┬────┘      └──────┬──────┘      └─────┬──────┘
     │                  │                    │
     ↓                  ↓                    ↓
┌─────────┐      ┌─────────────┐      ┌────────────┐
│credential-db│   │  user-db    │      │ device-db  │
│  :5434   │      │   :5432     │      │   :5433    │
│PostgreSQL│      │ PostgreSQL  │      │ PostgreSQL │
└──────────┘      └─────────────┘      └────────────┘
```

### Componente

#### Backend Services (Spring Boot)

1. **Auth Service** (Port 8084)
   - Autentificare si inregistrare utilizatori
   - Gestionare credentiale
   - Database: `DS_EMS_CREDENTIAL` (PostgreSQL)

2. **User Service** (Port 8082)
   - CRUD operatii pentru utilizatori
   - Gestionare roluri (admin/client)
   - Database: `DS_EMS_USER` (PostgreSQL)

3. **Device Service** (Port 8083)
   - CRUD operatii pentru dispozitive
   - Asociere dispozitive cu utilizatori
   - Gestionare consum maxim (MCV)
   - Database: `DS_EMS_DEVICE` (PostgreSQL)

#### Frontend (React + Vite)

- Single Page Application (SPA)
- Interfata bazata pe roluri
- Comunicare cu backend prin Axios
- Port: 80 (production), 3000 (development)

#### Infrastructure

- **Traefik**: API Gateway si reverse proxy
- **PostgreSQL**: 3 instante separate pentru fiecare serviciu
- **pgAdmin**: Administrare baze de date (Port 5050)
- **Docker Compose**: Orchestrare servicii

## Stack Tehnologic

### Backend
- **Framework**: Spring Boot 4.0.0-SNAPSHOT
- **Java**: Version 25
- **ORM**: Spring Data JPA
- **Validation**: Spring Validation
- **Database**: PostgreSQL 15
- **Build Tool**: Maven

### Frontend
- **Framework**: React 18.2.0
- **Build Tool**: Vite 5.0.8
- **Routing**: React Router DOM 6.20.0
- **HTTP Client**: Axios 1.6.2
- **Web Server**: Nginx (production)

### Infrastructure
- **Containerization**: Docker & Docker Compose
- **API Gateway**: Traefik 3.0
- **Database**: PostgreSQL 15
- **Database Admin**: pgAdmin 4

## Cerinte (Prerequisites)

- Docker Desktop sau Docker Engine + Docker Compose
- Git
- (Optional) Java 25 pentru dezvoltare locala
- (Optional) Node.js 18+ pentru dezvoltare frontend

## Instalare si Rulare

### 1. Clonare repository

```bash
git clone <repository-url>
cd demo
```

### 2. Pornire sistem complet cu Docker Compose

```bash
docker-compose up --build
```

Aceasta comanda va:
- Construi imaginile Docker pentru toate serviciile
- Porni 3 baze de date PostgreSQL
- Porni cele 3 microservicii Spring Boot
- Porni frontend-ul React cu Nginx
- Porni Traefik API Gateway
- Porni pgAdmin

### 3. Accesare aplicatie

- **Frontend**: http://localhost
- **Traefik Dashboard**: http://localhost:8085
- **pgAdmin**: http://localhost:5050
  - Email: admin@admin.com
  - Password: ****

### 4. Oprire sistem

```bash
docker-compose down
```

Pentru a sterge si volumele (datele din baze de date):

```bash
docker-compose down -v
```

## Documentatie API

### Swagger UI

Fiecare microserviciu expune documentatia API prin Swagger UI:

- **Auth Service Swagger**: http://localhost:8084/swagger-ui.html
- **User Service Swagger**: http://localhost:8082/swagger-ui.html
- **Device Service Swagger**: http://localhost:8083/swagger-ui.html

Swagger UI ofera:
- Documentatie interactiva completa a API-ului
- Posibilitatea de a testa endpoint-urile direct din browser
- Schema detaliata a request/response bodies
- Coduri de status HTTP si descrieri
- Modele de date cu validari

### Auth Service (Port 8084)

Acceseaza Swagger UI la: http://localhost:8084/swagger-ui.html

#### POST /auth/login
Autentificare utilizator

**Request Body:**
```json
{
  "username": "string",
  "password": "****"
}
```

**Response:**
```json
{
  "userId": "integer",
  "username": "string",
  "role": "string"
}
```

#### POST /auth/register
Inregistrare utilizator nou

**Request Body:**
```json
{
  "username": "string",
  "password": "****"
}
```

#### GET /auth/health
Health check

### User Service (Port 8082)

Acceseaza Swagger UI la: http://localhost:8082/swagger-ui.html

#### GET /users
Returneaza toti utilizatorii

**Response:**
```json
[
  {
    "id": "integer",
    "name": "string",
    "email": "string",
    "username": "string",
    "role": "string",
    "age": "integer",
    "address": "string"
  }
]
```

#### GET /users/{id}
Returneaza utilizator dupa ID

#### POST /users
Creare utilizator nou

**Request Body:**
```json
{
  "name": "string",
  "email": "string",
  "username": "string",
  "role": "string",
  "age": "integer",
  "address": "string"
}
```

#### PUT /users/{id}
Actualizare utilizator

#### DELETE /users/{id}
Stergere utilizator

### Device Service (Port 8083)

Acceseaza Swagger UI la: http://localhost:8083/swagger-ui.html

#### GET /devices
Returneaza toate dispozitivele

**Response:**
```json
[
  {
    "id": "integer",
    "name": "string",
    "mcv": "double",
    "userId": "integer"
  }
]
```

#### GET /devices/{id}
Returneaza dispozitiv dupa ID

#### POST /devices
Creare dispozitiv nou

**Request Body:**
```json
{
  "name": "string",
  "mcv": "double",
  "userId": "integer"
}
```

**Note:** MCV = Maximum Consumption Value (valoarea maxima de consum)

#### PUT /devices/{id}
Actualizare dispozitiv

#### DELETE /devices/{id}
Stergere dispozitiv

## Routing (Traefik)

Toate cererile catre backend trec prin Traefik pe portul 80:

- `http://localhost/auth/*` → Auth Service (8084)
- `http://localhost/users/*` → User Service (8082)
- `http://localhost/devices/*` → Device Service (8083)

## Configurare Baze de Date

### Conexiuni PostgreSQL

| Service | Database | Port | Username | Password |
|---------|----------|------|----------|----------|
| Auth | DS_EMS_CREDENTIAL | 5434 | tomarm | **** |
| User | DS_EMS_USER | 5432 | tomarm | **** |
| Device | DS_EMS_DEVICE | 5433 | tomarm | **** |

### pgAdmin Configuration

Pentru a conecta pgAdmin la bazele de date:

1. Acceseaza http://localhost:5050
2. Login cu admin@admin.com / ****
3. Add Server cu urmatoarele detalii:

**User Database:**
- Host: user-db
- Port: 5432
- Username: tomarm
- Password: ****

**Device Database:**
- Host: device-db
- Port: 5433
- Username: tomarm
- Password: ****

**Credential Database:**
- Host: credential-db
- Port: 5434
- Username: tomarm
- Password: ****

## Dezvoltare Locala

### Backend (Spring Boot)

Pentru a rula un serviciu local (fara Docker):

```bash
cd user-service  # sau auth-service, device-service
mvn spring-boot:run
```

**Note:** Trebuie sa configurezi variabilele de mediu pentru conexiunea la database:
```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/DS_EMS_USER
export SPRING_DATASOURCE_USERNAME=tomarm
export SPRING_DATASOURCE_PASSWORD=****
export PORT=8082
```

### Frontend (React)

Pentru dezvoltare locala cu hot reload:

```bash
cd ems-frontend
npm install
npm run dev
```

Frontend-ul va fi disponibil pe http://localhost:3000

## Structura Proiectului

```
demo/
├── auth-service/
│   ├── src/
│   ├── Dockerfile
│   ├── pom.xml
│   └── README.md
├── user-service/
│   ├── src/
│   ├── Dockerfile
│   ├── pom.xml
│   └── README.md
├── device-service/
│   ├── src/
│   ├── Dockerfile
│   ├── pom.xml
│   └── README.md
├── ems-frontend/
│   ├── src/
│   ├── public/
│   ├── Dockerfile
│   ├── package.json
│   └── README.md
├── docker-compose.yml
├── pom.xml (root)
└── README.md
```

## Functionalitati

### Pentru Administratori
- Vizualizare, creare, editare, stergere utilizatori
- Vizualizare, creare, editare, stergere dispozitive
- Asociere dispozitive cu utilizatori
- Gestionare roluri utilizatori

### Pentru Clienti
- Vizualizare dispozitive proprii
- Monitorizare consum maxim dispozitive

## Modele de Date

### User
```java
{
  id: Long,
  name: String,
  email: String,
  username: String,
  role: String,  // "admin" sau "client"
  age: Integer,
  address: String
}
```

### Device
```java
{
  id: Long,
  name: String,
  mcv: Double,      // Maximum Consumption Value
  userId: Long      // Foreign key catre Users
}
```

### Credentials
```java
{
  id: Long,
  username: String,
  password: String  // ⚠️ Plain text (doar pentru development!)
}
```

## Securitate

⚠️ **Avertisment**: Aceasta aplicatie este destinata scopurilor educationale:

- Parolele sunt stocate in plain text (fara hash)
- Nu exista JWT sau session management
- CORS este permis pentru toate origin-urile
- Nu exista HTTPS

Pentru productie ar fi necesare:
- Hashing parole (BCrypt)
- JWT tokens pentru autentificare
- HTTPS
- Rate limiting
- Input validation imbunatatita
- Configurare CORS restrictiva

## Network Configuration

Toate serviciile ruleaza in aceeasi retea Docker (`user-network`), permitand comunicarea inter-servicii prin numele containerelor.

## Volumes

Datele bazelor de date sunt persistente folosind Docker volumes:
- `user-db-data`
- `device-db-data`
- `credential-db-data`

## Troubleshooting

### Porturile sunt deja folosite

Daca primiti erori despre porturi ocupate, opriti serviciile care folosesc aceste porturi:
- 80, 8000, 8085 (Traefik)
- 8082, 8083, 8084 (Backend services)
- 5432, 5433, 5434 (PostgreSQL)
- 5050 (pgAdmin)
- 3000 (Frontend dev)

### Containerele nu pornesc

```bash
# Verifica logs
docker-compose logs <service-name>

# Reporneste serviciul
docker-compose restart <service-name>

# Rebuild complet
docker-compose down
docker-compose up --build
```

### Baza de date nu se conecteaza

Asigura-te ca:
1. Containerele PostgreSQL sunt pornite: `docker-compose ps`
2. Variabilele de mediu sunt corecte in `docker-compose.yml`
3. Health checks trec cu succes

### Swagger UI nu se incarca

Daca Swagger UI nu este disponibil:
1. Verifica ca serviciul ruleaza: `docker-compose ps`
2. Verifica logs-urile: `docker-compose logs <service-name>`
3. Asigura-te ca dependentele Swagger sunt in `pom.xml`
4. Acceseaza direct portul serviciului (nu prin Traefik)

## Testare API

### Folosind Swagger UI (Recomandat)

1. Acceseaza Swagger UI pentru serviciul dorit
2. Selecteaza endpoint-ul de testat
3. Click pe "Try it out"
4. Completeaza parametrii necesari
5. Click pe "Execute"
6. Vezi response-ul in interfata

### Exemple de Request-uri

**Login (prin Swagger):**
1. Deschide http://localhost:8084/swagger-ui.html
2. Gaseste POST /auth/login
3. Try it out → completeaza username si password
4. Execute

**Creare User (prin Swagger):**
1. Deschide http://localhost:8082/swagger-ui.html
2. Gaseste POST /users
3. Try it out → completeaza toate campurile
4. Execute

**Creare Device (prin Swagger):**
1. Deschide http://localhost:8083/swagger-ui.html
2. Gaseste POST /devices
3. Try it out → completeaza name, mcv, userId
4. Execute


**Autor** Toma Raul Mihai, Universitatea Tehnica Cluj-Napoca
**Curs**: Sisteme Distribuite
**Data**: 06.11.2025
