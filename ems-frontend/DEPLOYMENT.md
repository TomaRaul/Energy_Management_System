# Energy Management System - Deployment Guide

## 📦 Structura Completă a Proiectului

```
project-root/
├── user-service/           # Backend service pentru users
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── device-service/         # Backend service pentru devices
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── ems-frontend/           # Frontend React
│   ├── src/
│   ├── package.json
│   ├── Dockerfile
│   └── nginx.conf
└── docker-compose.yml      # Orchestrare completă
```

## 🚀 Deployment Complet cu Docker

### Opțiunea 1: Development Mode

#### 1. Pornește doar backend-ul
```bash
# În root directory
docker-compose up -d user-db device-db credential-db user-service device-service traefik
```

#### 2. Rulează frontend-ul local în mod development
```bash
cd ems-frontend
npm install
npm run dev
```

Frontend va fi disponibil la: http://localhost:3000
Backend APIs prin Traefik: http://localhost:80

### Opțiunea 2: Full Production Mode

#### 1. Asigură-te că toate serviciile sunt în același director
```bash
project-root/
├── user-service/
├── device-service/
└── ems-frontend/
```

#### 2. Folosește docker-compose complet
Copiază `docker-compose-full.yml` ca `docker-compose.yml` în root sau rulează:

```bash
docker-compose -f ems-frontend/docker-compose-full.yml up -d --build
```

#### 3. Verifică că toate serviciile rulează
```bash
docker-compose ps
```

Ar trebui să vezi:
- `traefik-gateway` - Running
- `user-db` - Running (healthy)
- `device-db` - Running (healthy)
- `credential-db` - Running (healthy)
- `user-service` - Running
- `device-service` - Running
- `ems-frontend` - Running
- `pgadmin` - Running

### 3. Accesează aplicația

- **Frontend**: http://localhost:3000
- **User API**: http://localhost:80/users
- **Device API**: http://localhost:80/devices
- **Traefik Dashboard**: http://localhost:8085
- **PgAdmin**: http://localhost:5050

## 🔍 Verificare și Testare

### 1. Test Backend APIs

```bash
# Test User Service
curl http://localhost:80/users

# Test Device Service
curl http://localhost:80/devices

# Create a test user
curl -X POST http://localhost:80/users \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "name": "Test User",
    "email": "test@example.com",
    "username": "testuser",
    "role": "client",
    "age": 25,
    "address": "Test Address"
  }'

# Create a test device
curl -X POST http://localhost:80/devices \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "name": "Smart Meter 1",
    "mcv": 5000,
    "userId": 1
  }'
```

### 2. Verifică Logs

```bash
# Backend logs
docker logs user-service
docker logs device-service

# Frontend logs
docker logs ems-frontend

# Traefik logs
docker logs traefik-gateway
```

### 3. Verifică Database

Accesează PgAdmin la http://localhost:5050
- Email: admin@admin.com
- Password: admin

Adaugă servere:
1. User DB: host=user-db, port=5432, database=DS_EMS_USER
2. Device DB: host=device-db, port=5432, database=DS_EMS_DEVICE

## 🛠️ Troubleshooting

### Problema: Frontend nu se poate conecta la backend

**Soluție 1**: Verifică că nginx proxy settings sunt corecte
```bash
docker exec ems-frontend cat /etc/nginx/conf.d/default.conf
```

**Soluție 2**: Verifică network-ul Docker
```bash
docker network inspect user-network
```

### Problema: Backend service nu pornește

**Verifică logs**:
```bash
docker logs user-service -f
docker logs device-service -f
```

**Verifică database connection**:
```bash
docker exec -it user-db psql -U tomarm -d DS_EMS_USER -c "SELECT version();"
```

### Problema: Port already in use

**Schimbă porturile** în docker-compose.yml:
```yaml
frontend:
  ports:
    - "3001:80"  # Instead of 3000:80
```

## 🔄 Update și Rebuild

### Update Frontend
```bash
cd ems-frontend
# Fă modificările necesare
docker-compose up -d --build frontend
```

### Update Backend Services
```bash
docker-compose up -d --build user-service device-service
```

### Full Rebuild
```bash
docker-compose down
docker-compose up -d --build
```

## 🗑️ Cleanup

### Stop toate serviciile
```bash
docker-compose down
```

### Stop și șterge volumes (⚠️ șterge datele!)
```bash
docker-compose down -v
```

### Șterge toate containers, images, volumes
```bash
docker-compose down -v --rmi all
docker system prune -a --volumes
```

## 📊 Monitorizare

### Traefik Dashboard
Accesează http://localhost:8085 pentru a vedea:
- Toate serviciile înregistrate
- Statusul fiecărui service
- Request metrics
- Routing rules

### Docker Stats
```bash
docker stats
```

Vezi utilizarea resurse pentru toate containerele.

## 🔐 Securitate (Pentru Producție)

Pentru deployment în producție, adaugă:

1. **JWT Authentication**
2. **HTTPS cu certificat SSL**
3. **Environment variables securizate**
4. **Rate limiting**
5. **CORS policy strict**
6. **Database password management (secrets)**

## 📝 Next Steps

După ce aplicația rulează:

1. ✅ Testează CRUD operations pe Users
2. ✅ Testează CRUD operations pe Devices
3. ✅ Testează asocierea Devices cu Users
4. ✅ Verifică responsive design
5. ✅ Test error handling
6. 🔜 Adaugă Authentication Service
7. 🔜 Implementează role-based access (Admin/Client)
8. 🔜 Adaugă Swagger documentation

## 🎓 Pentru Assignment

Asigură-te că ai:
- ✅ UML Deployment Diagram
- ✅ README cu build instructions
- ✅ Source code în GitLab
- ✅ Docker deployment functional
- ✅ Traefik reverse proxy configurat
- ✅ Toate serviciile comunică corect
