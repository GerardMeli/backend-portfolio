-- =====================================================================
-- Portfolio multi-marchés (Afrique / Canada / UE) - PostgreSQL
-- À placer dans src/main/resources/schema.sql
-- application.properties :
--   spring.sql.init.mode=always
--   spring.jpa.hibernate.ddl-auto=validate
-- Textes traduisibles = JSONB {"fr": "...", "en": "..."}
-- Colonne "markets" : NULL = visible partout, sinon ex. {AFRICA,EU}
-- =====================================================================

-- ---------- Authentification (admin unique) ----------
CREATE TABLE IF NOT EXISTS app_user (
    id            VARCHAR(255) PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- Jetons de rafraîchissement opaques : seul leur hash SHA-256 est stocké.
CREATE TABLE IF NOT EXISTS refresh_token (
    id         VARCHAR(255) PRIMARY KEY,
    user_id    VARCHAR(255) NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_refresh_token_user ON refresh_token(user_id);

-- ---------- Profil (une seule ligne) ----------
CREATE TABLE IF NOT EXISTS profile (
    id                 VARCHAR(255) PRIMARY KEY,
    full_name          VARCHAR(150) NOT NULL,
    headline           JSONB,
    summary            JSONB,
    email              VARCHAR(255),
    phone              VARCHAR(50),
    city               VARCHAR(100),
    country            VARCHAR(100),
    photo_url          VARCHAR(500),
    birth_date         DATE,
    nationality        VARCHAR(100),
    marital_status     VARCHAR(50),
    work_authorization JSONB,
    linkedin_url       VARCHAR(500),
    github_url         VARCHAR(500),
    website_url        VARCHAR(500),
    updated_at         TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ---------- Marchés et règles de rendu ----------
CREATE TABLE IF NOT EXISTS market (
    code                  VARCHAR(20) PRIMARY KEY,           -- AFRICA, CANADA, EU
    label                 VARCHAR(100) NOT NULL,
    default_locale        VARCHAR(5)   NOT NULL,             -- fr, en
    page_format           VARCHAR(10)  NOT NULL,             -- A4, LETTER
    date_format           VARCHAR(20)  NOT NULL,             -- ex. MM/yyyy, MMM yyyy
    show_photo            BOOLEAN NOT NULL DEFAULT FALSE,
    show_personal_details BOOLEAN NOT NULL DEFAULT FALSE,    -- naissance, état civil, nationalité
    max_pages             SMALLINT NOT NULL DEFAULT 2,
    notes                 TEXT
);

-- Ordre et visibilité des sections par marché
CREATE TABLE IF NOT EXISTS market_section (
    market_code  VARCHAR(20) NOT NULL REFERENCES market(code) ON DELETE CASCADE,
    section_type VARCHAR(50) NOT NULL,   -- SUMMARY, EXPERIENCE, EDUCATION, PROJECT, SKILL, CERTIFICATION, LANGUAGE, ou code d'une custom_section
    position     INT     NOT NULL,
    visible      BOOLEAN NOT NULL DEFAULT TRUE,
    max_items    INT,
    PRIMARY KEY (market_code, section_type)
);

-- ---------- Expériences et tâches ----------
CREATE TABLE IF NOT EXISTS experience (
    id              VARCHAR(255) PRIMARY KEY,
    company         VARCHAR(200) NOT NULL,
    location        VARCHAR(150),
    title           JSONB NOT NULL,
    description     JSONB,
    employment_type VARCHAR(30),         -- FULL_TIME, INTERNSHIP, FREELANCE, CONTRACT...
    start_date      DATE NOT NULL,
    end_date        DATE,
    is_current      BOOLEAN NOT NULL DEFAULT FALSE,
    markets         VARCHAR(20)[],
    position        INT NOT NULL DEFAULT 0,
    visible         BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS experience_task (
    id            VARCHAR(255) PRIMARY KEY,
    experience_id VARCHAR(255) NOT NULL REFERENCES experience(id) ON DELETE CASCADE,
    content       JSONB NOT NULL,
    markets       VARCHAR(20)[],
    position      INT NOT NULL DEFAULT 0,
    visible       BOOLEAN NOT NULL DEFAULT TRUE
);

-- ---------- Formations ----------
CREATE TABLE IF NOT EXISTS education (
    id          VARCHAR(255) PRIMARY KEY,
    institution VARCHAR(200) NOT NULL,
    location    VARCHAR(150),
    degree      JSONB NOT NULL,
    field       JSONB,
    description JSONB,
    start_date  DATE,
    end_date    DATE,
    markets     VARCHAR(20)[],
    position    INT NOT NULL DEFAULT 0,
    visible     BOOLEAN NOT NULL DEFAULT TRUE
);

-- ---------- Projets (personnels / pro / open source) ----------
CREATE TABLE IF NOT EXISTS project (
    id          VARCHAR(255) PRIMARY KEY,
    name        VARCHAR(200) NOT NULL,
    kind        VARCHAR(20)  NOT NULL DEFAULT 'PERSONAL',  -- PERSONAL, PROFESSIONAL, OPEN_SOURCE
    description JSONB,
    tech_stack  VARCHAR(50)[],
    demo_url    VARCHAR(500),
    repo_url    VARCHAR(500),
    start_date  DATE,
    end_date    DATE,
    markets     VARCHAR(20)[],
    position    INT NOT NULL DEFAULT 0,
    visible     BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS project_highlight (
    id         VARCHAR(255) PRIMARY KEY,
    project_id VARCHAR(255) NOT NULL REFERENCES project(id) ON DELETE CASCADE,
    content    JSONB NOT NULL,
    position   INT NOT NULL DEFAULT 0,
    visible    BOOLEAN NOT NULL DEFAULT TRUE
);

-- ---------- Compétences ----------
CREATE TABLE IF NOT EXISTS skill_category (
    id       VARCHAR(255) PRIMARY KEY,
    name     JSONB NOT NULL,             -- Backend, Frontend, Base de données...
    kind     VARCHAR(20) NOT NULL DEFAULT 'PROFESSIONAL',
    position INT NOT NULL DEFAULT 0,
    visible  BOOLEAN NOT NULL DEFAULT TRUE
);

ALTER TABLE skill_category ADD COLUMN IF NOT EXISTS kind VARCHAR(20) NOT NULL DEFAULT 'PROFESSIONAL';
-- to_jsonb(row) makes the legacy-column lookup safe when category_type is absent.
UPDATE skill_category
SET kind = COALESCE(to_jsonb(skill_category) ->> 'category_type', kind);
ALTER TABLE skill_category DROP COLUMN IF EXISTS category_type;

CREATE TABLE IF NOT EXISTS skill (
    id          VARCHAR(255) PRIMARY KEY,
    category_id VARCHAR(255) NOT NULL REFERENCES skill_category(id) ON DELETE CASCADE,
    name        VARCHAR(100) NOT NULL,
    level       SMALLINT CHECK (level BETWEEN 1 AND 5),
    markets     VARCHAR(20)[],
    position    INT NOT NULL DEFAULT 0,
    visible     BOOLEAN NOT NULL DEFAULT TRUE
);

-- ---------- Certifications et langues ----------
CREATE TABLE IF NOT EXISTS certification (
    id             VARCHAR(255) PRIMARY KEY,
    name           JSONB NOT NULL,
    issuer         VARCHAR(200),
    issued_on      DATE,
    expires_on     DATE,
    credential_url VARCHAR(500),
    markets        VARCHAR(20)[],
    position       INT NOT NULL DEFAULT 0,
    visible        BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS spoken_language (
    id       VARCHAR(255) PRIMARY KEY,
    name     JSONB NOT NULL,
    level    VARCHAR(20),                -- A1..C2 (CECRL) ou Natif
    position INT NOT NULL DEFAULT 0,
    visible  BOOLEAN NOT NULL DEFAULT TRUE
);

-- ---------- Sections personnalisées (références, bénévolat, publications...) ----------
CREATE TABLE IF NOT EXISTS custom_section (
    id       VARCHAR(255) PRIMARY KEY,
    code     VARCHAR(50) NOT NULL UNIQUE,   -- utilisé dans market_section.section_type
    title    JSONB NOT NULL,
    markets  VARCHAR(20)[],
    position INT NOT NULL DEFAULT 0,
    visible  BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS custom_item (
    id          VARCHAR(255) PRIMARY KEY,
    section_id  VARCHAR(255) NOT NULL REFERENCES custom_section(id) ON DELETE CASCADE,
    title       JSONB NOT NULL,
    subtitle    VARCHAR(200),
    description JSONB,
    start_date  DATE,
    end_date    DATE,
    url         VARCHAR(500),
    markets     VARCHAR(20)[],
    position    INT NOT NULL DEFAULT 0,
    visible     BOOLEAN NOT NULL DEFAULT TRUE
);

-- ---------- Index ----------
CREATE INDEX IF NOT EXISTS idx_experience_task_exp ON experience_task(experience_id);
CREATE INDEX IF NOT EXISTS idx_project_highlight_project ON project_highlight(project_id);
CREATE INDEX IF NOT EXISTS idx_skill_category ON skill(category_id);
CREATE INDEX IF NOT EXISTS idx_custom_item_section ON custom_item(section_id);

-- ---------- Données initiales des marchés (idempotent) ----------
INSERT INTO market (code, label, default_locale, page_format, date_format, show_photo, show_personal_details, max_pages, notes) VALUES
 ('AFRICA', 'Afrique francophone', 'fr', 'A4',     'MM/yyyy',  TRUE,  TRUE,  2, 'Photo et état civil courants'),
 ('CANADA', 'Canada',              'en', 'LETTER', 'MMM yyyy', FALSE, FALSE, 2, 'Sans photo ni infos personnelles, résultats chiffrés'),
 ('EU',     'Union européenne',    'en', 'A4',     'MM/yyyy',  FALSE, FALSE, 2, 'Style Europass, photo optionnelle')
ON CONFLICT (code) DO NOTHING;

-- Regroupe sans supprimer de données les anciennes configs en une section SKILL.
INSERT INTO market_section (market_code, section_type, position, visible, max_items)
SELECT market_code,
       'SKILL',
       MIN(position),
       BOOL_OR(visible),
       CASE
           WHEN BOOL_OR(max_items IS NULL) THEN NULL
           ELSE MAX(max_items)
       END
FROM market_section
WHERE section_type IN ('SKILL', 'PERSONAL_SKILL', 'PROFESSIONAL_SKILL')
GROUP BY market_code
ON CONFLICT (market_code, section_type) DO UPDATE
SET position = EXCLUDED.position,
    visible = EXCLUDED.visible,
    max_items = EXCLUDED.max_items;

DELETE FROM market_section
WHERE section_type IN ('PERSONAL_SKILL', 'PROFESSIONAL_SKILL');

INSERT INTO market_section (market_code, section_type, position, visible) VALUES
 ('AFRICA','SUMMARY',1,TRUE),('AFRICA','EDUCATION',2,TRUE),('AFRICA','EXPERIENCE',3,TRUE),('AFRICA','SKILL',4,TRUE),('AFRICA','PROJECT',5,TRUE),('AFRICA','CERTIFICATION',6,TRUE),('AFRICA','LANGUAGE',7,TRUE),
 ('CANADA','SUMMARY',1,TRUE),('CANADA','SKILL',2,TRUE),('CANADA','EXPERIENCE',3,TRUE),('CANADA','PROJECT',4,TRUE),('CANADA','EDUCATION',5,TRUE),('CANADA','CERTIFICATION',6,TRUE),('CANADA','LANGUAGE',7,TRUE),
 ('EU','SUMMARY',1,TRUE),('EU','EXPERIENCE',2,TRUE),('EU','EDUCATION',3,TRUE),('EU','SKILL',4,TRUE),('EU','PROJECT',5,TRUE),('EU','CERTIFICATION',6,TRUE),('EU','LANGUAGE',7,TRUE)
ON CONFLICT (market_code, section_type) DO NOTHING;
