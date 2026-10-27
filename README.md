# Backend Portfolio

API REST d'un portfolio qui **adapte le CV au marché du visiteur** (Afrique francophone, Canada, Union européenne) : format de page, format de date, photo, informations personnelles, ordre et contenu des sections.

Tout le contenu (profil, expériences et tâches, formations, projets, compétences, certifications, langues, sections personnalisées) se gère par l'API admin, sans toucher au code.

## Stack

- Java 21, Spring Boot (Web, Data JPA, Validation, Security, Actuator)
- PostgreSQL, sans Flyway : le schéma est dans `schema.sql`
- Authentification JWT (un seul administrateur)
- Swagger UI (springdoc) pour tester l'API
- Maven, sans Docker

## Fonctionnement

Le visiteur choisit un marché dans le frontend. Le backend renvoie un CV déjà filtré :

1. règles du marché : format de page (A4 ou Letter), format de date, photo, infos personnelles (date de naissance, nationalité, état civil), nombre de pages ;
2. sections visibles, dans l'ordre propre au marché, avec un plafond d'éléments optionnel ;
3. éléments visibles pour ce marché (`markets` nul = visibles partout) ;
4. textes dans la langue demandée, avec repli sur la langue du marché.

Les textes traduisibles sont stockés en JSONB : `{"fr": "...", "en": "..."}`.

Les catégories de compétences acceptent le champ `type` (`PERSONAL` ou `PROFESSIONAL`) via l'API admin. Le CV public les présente dans deux sections distinctes : `PERSONAL_SKILL` et `PROFESSIONAL_SKILL`. Les anciennes catégories sans type sont considérées comme professionnelles.

## Prérequis

- JDK 21
- Maven 3.9+
- PostgreSQL 14+

## Démarrage en local

1. Créer la base :

```bash
createdb portfolio
# ou : psql -U postgres -c "CREATE DATABASE portfolio;"
```

2. Lancer l'application (profil `local` par défaut) :

```bash
mvn spring-boot:run
```

Au démarrage, `schema.sql` crée les tables si elles n'existent pas et insère les 3 marchés avec leurs sections (opération répétable sans risque). Hibernate vérifie ensuite que les entités correspondent au schéma (`ddl-auto: validate`).

3. Ouvrir Swagger : http://localhost:8080/swagger-ui.html

Au premier démarrage, l'administrateur est créé uniquement lorsque `ADMIN_EMAIL` et `ADMIN_PASSWORD` sont fournis.

Un guide de test pas à pas est fourni dans `swagger-test-guide.md`.

## Configuration

Tout se règle dans `src/main/resources/application.yml`, surchargeable par variables d'environnement.

| Variable | Rôle | Défaut |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `local` ou `prod` | `local` |
| `DB_URL` | URL JDBC | `jdbc:postgresql://localhost:5432/portfolio` |
| `DB_USERNAME` / `DB_PASSWORD` | accès base | `postgres` / obligatoire |
| `JWT_SECRET` | secret de signature aléatoire, 32 octets minimum | obligatoire |
| `JWT_EXPIRATION_MINUTES` | durée de vie du jeton d'accès | `15` |
| `JWT_REFRESH_EXPIRATION_DAYS` | durée du refresh token rotatif | `7` |
| `JWT_REFRESH_COOKIE_SECURE` | impose HTTPS pour le cookie de refresh | `true` |
| `ADMIN_EMAIL` / `ADMIN_PASSWORD` | compte admin créé au premier démarrage | obligatoires pour l'initialisation |
| `CORS_ALLOWED_ORIGINS` | origines autorisées, séparées par des virgules | `http://localhost:4200` |
| `SWAGGER_ENABLED` | active Swagger | `true` |
| `PORT` | port HTTP | `8080` |

L'application ne possède plus de secret, mot de passe base ou mot de passe administrateur par défaut. `JWT_SECRET` et `DB_PASSWORD` sont obligatoires au démarrage.

## Structure

```
gerard.meli.backend_portfolio
├── config/        AppProperties, SecurityConfig, OpenApiConfig
├── security/      JwtService, JwtAuthenticationFilter, AuthService, AdminInitializer, SecurityErrorHandler
├── domain/        entités JPA et énumérations
├── repository/    Spring Data JPA
├── mapper/        entité <-> DTO, ViewContext (marché + langue)
├── service/       logique métier, PortfolioService (assemblage du CV)
├── web/           controllers public et admin
├── dto/           records de requête, réponse et vues publiques
└── exception/     exceptions métier et GlobalExceptionHandler
```

Ressources : `application.yml` et `schema.sql` dans `src/main/resources/`.

## API

### Public (sans authentification)

| Méthode | Chemin | Rôle |
|---|---|---|
| GET | `/api/public/markets` | marchés du sélecteur |
| GET | `/api/public/portfolio?market=CANADA&lang=en` | CV adapté (paramètres facultatifs) |
| POST | `/api/auth/login` | connexion, renvoie un JWT et un cookie HttpOnly de refresh |
| POST | `/api/auth/refresh` | renouvelle le JWT et fait tourner le refresh token |
| POST | `/api/auth/logout` | révoque et efface le refresh token |

### Admin (en-tête `Authorization: Bearer <jeton>`)

Pour chaque ressource : `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` et `PATCH /reorder` avec `{"ids": [3, 1, 2]}`.

| Ressource | Chemin | Sous-ressource |
|---|---|---|
| Profil | `/api/admin/profile` (`GET`, `PUT`) | |
| Expériences | `/api/admin/experiences` | `/{id}/tasks` |
| Projets | `/api/admin/projects` | `/{id}/highlights` |
| Compétences | `/api/admin/skill-categories` | `/{id}/skills` |
| Sections perso | `/api/admin/custom-sections` | `/{id}/items` |
| Formations | `/api/admin/educations` | |
| Certifications | `/api/admin/certifications` | |
| Langues | `/api/admin/languages` | |
| Marchés | `/api/admin/markets` | `/{code}/sections` |

Les sous-ressources ont aussi leur `PATCH /{id}/<sous-ressource>/reorder`.

### Erreurs

Format unique : `{ "status", "message", "details", "timestamp" }`.
400 données invalides, 401 non authentifié, 403 interdit, 404 introuvable, 409 conflit, 429 trop de tentatives de connexion.

## Sécurité

- JWT signé HS256, vérifié à chaque requête admin.
- Mots de passe hachés avec BCrypt.
- Après 5 échecs de connexion sur un email, blocage de 15 minutes (compteur en mémoire, remis à zéro au redémarrage).
- CORS limité aux origines configurées.
- Mot de passe perdu : supprimer la ligne de la table `app_user` ; l'admin sera recréé au prochain démarrage avec `ADMIN_EMAIL` et `ADMIN_PASSWORD`.

## Déploiement sur une offre gratuite

Les conditions des offres gratuites changent souvent : vérifie-les avant de choisir.

1. **Base** : un Postgres gratuit (Neon ou Supabase, par exemple). Récupère l'URL et mets-la en `DB_URL` avec `?sslmode=require` à la fin : `jdbc:postgresql://HOTE/BASE?sslmode=require`.
2. **Backend** : un hébergeur Java gratuit (Render ou Koyeb, par exemple) :
    - build : `mvn clean package -DskipTests`
    - démarrage : `java -Xmx256m -jar target/<nom-du-jar>.jar` (le nom dépend de `finalName` dans le `pom.xml`)
    - health check : `/actuator/health`
    - variables : `SPRING_PROFILES_ACTIVE=prod`, `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `ADMIN_EMAIL`, `ADMIN_PASSWORD`, `CORS_ALLOWED_ORIGINS` (URL du frontend), et `SWAGGER_ENABLED=false` si tu veux masquer Swagger.
3. **Mise en veille** : les offres gratuites arrêtent souvent l'application après une période d'inactivité. Le premier appel suivant peut prendre plusieurs dizaines de secondes (démarrage de la JVM). Prévois un écran de chargement côté frontend.

## Problèmes connus

| Symptôme | Cause et solution |
|---|---|
| `No enum constant ... SerializationFeature.write-dates-as-timestamps` | Jackson 3 (Spring Boot 4) : supprimer `spring.jackson.serialization.write-dates-as-timestamps`. Les dates sont déjà en ISO-8601. |
| Erreurs de compilation dans `JwtService` | Version de JJWT ancienne : le code actuel est écrit pour l'API 0.11.x et compile aussi en 0.12.x. |
| Swagger ne démarre pas | `springdoc` 2.8.x n'est pas compatible avec Spring Boot 4 : utiliser la série 3.x. |
| Hibernate refuse le schéma à cause d'une colonne `varchar[]` | Mettre `spring.jpa.hibernate.ddl-auto: none` : le schéma est déjà géré par `schema.sql`. |
| Un `PUT` désactive `showPhoto` ou `visible` | Dans `MarketDto` et `MarketSectionDto`, un booléen absent vaut `false` : toujours l'envoyer. |

## Suite prévue

- Frontend Angular + SCSS : CV public avec sélecteur de marché, espace admin, rendu A4 ou Letter
- Export PDF par marché
