# Marrakech Food — Application de Livraison de Repas

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen)
![Java](https://img.shields.io/badge/Java-17-orange)
![React](https://img.shields.io/badge/React.js-18-blue)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![CI/CD](https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-black)
![Railway](https://img.shields.io/badge/Deploy-Railway-purple)

Projet de fin de formation — Module **Ingénierie Logicielle Avancée**  
Université Cadi Ayyad — Faculté des Sciences Semlalia, Marrakech  
Année universitaire 2025/2026

---

## Description

**Marrakech Food** est une application web de livraison de repas basée sur une architecture **microservices** Spring Boot. Elle permet aux clients de commander des repas en ligne, aux restaurants de gérer leurs menus, et aux livreurs de confirmer les livraisons via scan de QR code.

---

## Architecture

L'application est composée de quatre microservices indépendants, chacun disposant de sa propre base de données MySQL.

| Microservice | Port | Responsabilité |
|---|---|---|
| `client-service` | 8089 | Inscription, connexion, gestion des rôles |
| `restaurant-service` | 8083 | CRUD restaurants et plats |
| `commande-service` | 8091 | Commandes, calcul du total, génération QR code |
| `livreur-service` | 8085 | Gestion livreurs, scan QR, confirmation livraison |

Les services communiquent entre eux via **Spring Cloud OpenFeign**.
Frontend React
|
|--- Client Service      (8089) ---> client_db
|--- Restaurant Service  (8083) ---> restaurant_db
|--- Commande Service    (8091) ---> commande_db
|--- Livreur Service     (8085) ---> livreur_db
---

## Fonctionnalités

- Authentification avec hashage BCrypt (inscription / connexion)
- Gestion des rôles : CLIENT, ADMIN, LIVREUR
- Catalogue de restaurants et plats avec filtrage par type de cuisine
- Gestion du panier et validation de commande
- Génération automatique de QR code à la validation (librairie ZXing)
- Scan du QR code par le livreur pour confirmer la livraison
- Suivi des statuts : EN_ATTENTE — EN_LIVRAISON — LIVREE
- Tests unitaires JUnit 5 avec base H2 en mémoire
- Pipeline CI/CD automatisé via GitHub Actions

---

## Stack technologique

| Couche | Technologies |
|---|---|
| Backend | Spring Boot 3.2.5, Java 17, Spring Data JPA, OpenFeign |
| Frontend | React.js, Bootstrap 5, Axios, React Router DOM |
| Base de données | MySQL (production), H2 (tests unitaires) |
| Securite | BCrypt — Spring Security Crypto |
| QR Code | ZXing (Google) |
| CI/CD | GitHub Actions |
| Deploiement | Railway, Docker |

---

## Lancer le projet en local

### Prerequis

- Java 17 ou supérieur
- Maven 3.8 ou supérieur
- MySQL 8.0
- Node.js 18 et npm
- Git

### 1. Cloner le dépôt

```bash
git clone https://github.com/votre-username/marrakech-food.git
cd marrakech-food
```

### 2. Créer les bases de données MySQL

```sql
CREATE DATABASE client_db;
CREATE DATABASE restaurant_db;
CREATE DATABASE commande_db;
CREATE DATABASE livreur_db;
```

### 3. Configurer chaque service

Dans chaque fichier `application.properties`, renseigner les informations de connexion :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/NOM_DB
spring.datasource.username=root
spring.datasource.password=votre_mot_de_passe
spring.jpa.hibernate.ddl-auto=update
```

### 4. Démarrer les microservices

```bash
# Client Service
cd client-service && mvn spring-boot:run

# Restaurant Service
cd restaurant-service && mvn spring-boot:run

# Commande Service
cd commande-service && mvn spring-boot:run

# Livreur Service
cd livreur-service && mvn spring-boot:run
```

### 5. Démarrer le frontend

```bash
cd frontend
npm install
npm start
```

L'application est accessible sur `http://localhost:3000`.

---

## Tests unitaires

```bash
# Depuis le répertoire de chaque microservice
mvn test
```

Les tests s'exécutent sur une base **H2 en mémoire**. Aucune configuration supplémentaire n'est requise.

---

## Endpoints API

### Client Service — port 8089

| Méthode | Endpoint | Description |
|---|---|---|
| POST | `/api/clients/inscrire` | Inscription d'un nouveau client |
| POST | `/api/clients/connexion` | Connexion |
| GET | `/api/clients/{id}` | Récupérer un client par son identifiant |

### Restaurant Service — port 8083

| Méthode | Endpoint | Description |
|---|---|---|
| GET | `/api/restaurants` | Liste de tous les restaurants |
| POST | `/api/restaurants` | Ajouter un restaurant |
| GET | `/api/restaurants/{id}/plats` | Plats d'un restaurant |

### Commande Service — port 8091

| Méthode | Endpoint | Description |
|---|---|---|
| POST | `/api/commandes` | Créer une commande |
| PUT | `/api/commandes/{id}/valider` | Valider et générer le QR code |
| GET | `/api/commandes/{id}` | Détails d'une commande |

### Livreur Service — port 8085

| Méthode | Endpoint | Description |
|---|---|---|
| GET | `/api/livreurs` | Liste des livreurs disponibles |
| POST | `/api/livreurs/scan-qr` | Scanner un QR code de livraison |
| PUT | `/api/livreurs/{id}/confirmer` | Confirmer la livraison |

---

## Comptes de test

| Role | Email | Mot de passe |
|---|---|---|
| Client | `client@test.com` | `1234` |
| Administrateur | `admin@marrakechfood.com` | `1234` |
| Livreur | `livreur@marrakechfood.com` | `1234` |

---

## Note sur le déploiement

Le projet a été déployé sur **Railway**. Les quatre microservices étaient accessibles et les APIs répondaient correctement au démarrage.

Une erreur **CORS** (Cross-Origin Resource Sharing) est apparue en production lors de l'inscription des utilisateurs. Le navigateur bloquait les requêtes du frontend vers le backend en raison des URLs dynamiquement assignées par Railway. Plusieurs solutions ont été tentées sans succès en production :

- Annotation `@CrossOrigin(origins = "*")` sur les contrôleurs
- Bean `CorsFilter` global dans chaque microservice
- Configuration des headers dans le `Dockerfile`
- Modification des `application.properties`

Le projet fonctionne correctement en local. Une **release GitHub** a été publiée pour documenter l'état final fonctionnel du code source.

Instances Railway déployées :
https://loving-dedication-production-6a00.up.railway.app
https://handsome-gratitude-production-9e23.up.railway.app
https://marrakechfood-production.up.railway.app
https://marrakechfood-production-22c2.up.railway.app
---

## Auteurs

| Nom | Profil GitHub |
|---|---|
| Zainab ZAHID | [@zainab]([https://github.com/jihanenasri) |
| Jihane NASRI | [@jihane](https://github.com/Zainab8019) |

Encadré par **Fahd KALLOUBI** — Université Cadi Ayyad, Marrakech — 2025/2026
