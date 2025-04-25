# GeoNetwork OIDC Harvester

This project extends GeoNetwork with a custom harvester that authenticates using OpenID Connect (OIDC).

## 🐳 Docker Setup

```bash
docker-compose up --build
```

Access GeoNetwork at [http://localhost:8080/geonetwork](http://localhost:8080/geonetwork)

## 🔐 Keycloak Test Server

```bash
docker-compose -f docker-compose.yml -f keycloak.yml up -d keycloak
```

Then log into Keycloak: [http://localhost:8081](http://localhost:8081)
- Username: `admin`
- Password: `admin`
