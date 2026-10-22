# backend-portfolio

API Spring Boot du portfolio : contenu multilingue (FR/EN) adapté à plusieurs marchés (Afrique, Canada, UE),
API publique ouverte aux visiteurs et API d'administration protégée par JWT.

## 1. Créer le projet

Sur **start.spring.io** : Maven, Java 21, Group `gerard.meli`, Artifact `backend-portfolio`,
Package name `gerard.meli.backend_portfolio`. Dépendances à cocher :

Spring Web, Spring Data JPA, Validation, Spring Security, **OAuth2 Resource Server**, Actuator,
**Lombok**, PostgreSQL Driver, Flyway Migration.

À ajouter **à la main** dans le `pom.xml` (sans elle, l'appli plante avec `Unsupported Database: PostgreSQL`) :

```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-database-postgresql</artifactId>
</dependency>
```

Puis copie par-dessus le projet généré : `src/`, `docker-compose.yml`, `Dockerfile`, `.gitignore`,
`.env.example` et `smoke-test.sh`.

## 2. Lancer en local

```bash
docker compose up -d                                    # PostgreSQL
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run       # Windows : mvnw.cmd
./smoke-test.sh                                         # vérifie tout de bout en bout
```

Le profil `dev` (`application-dev.yml`) fournit un secret JWT et un admin de test
(`admin@local.test` / `dev-password-123456`). **Uniquement pour le local.**
Le test de fumée crée puis supprime des données de test : ne le lance pas sur un site déjà rempli.

## 3. Variables d'environnement (production)

| Variable | Rôle |
|---|---|
| `DB_URL`, `DB_USER`, `DB_PASSWORD` | Connexion PostgreSQL (URL au format JDBC, voir `.env.example`) |
| `JWT_SECRET` | Secret de signature des tokens admin, 32 caractères minimum (obligatoire) |
| `ADMIN_EMAIL`, `ADMIN_PASSWORD` | Premier admin, créé seulement si aucun admin n'existe (12 caractères minimum) |
| `APP_CORS_ORIGINS` | URL(s) du frontend autorisées, séparées par des virgules |
| `PORT` | Port d'écoute (fourni par la plupart des hébergeurs) |

## 4. Déployer

Un `Dockerfile` est fourni (build en deux étapes, JRE seul, utilisateur non-root, mémoire plafonnée pour
les offres gratuites). Sur l'hébergeur : déploiement par Dockerfile, variables ci-dessus, et une base PostgreSQL
**externe et durable** (les bases gratuites de certains hébergeurs expirent).

## 5. API

| Zone | Accès | Routes principales |
|---|---|---|
| Public | Ouvert à tous | `GET /api/public/portfolio?lang=fr&market=canada`, `GET /api/public/projects/{slug}`, `POST /api/public/contact` |
| Connexion | Ouvert | `POST /api/auth/login` |
| Admin | JWT, rôle ADMIN | `/api/admin/**` : profil, variantes de marché, sections, compétences, expériences et tâches, formations, projets, messages |

## 6. Limites connues

- Les limiteurs de tentatives (connexion, contact) sont en mémoire : remis à zéro au redémarrage, valables pour une seule instance.
- Un token admin n'est pas révocable avant son expiration (120 minutes par défaut).
- Pas encore d'upload d'images (on saisit des URL `https://`) ni d'email de notification pour les messages de contact.
- L'API publique est mise en cache 5 minutes : une modification peut mettre ce temps à apparaître.