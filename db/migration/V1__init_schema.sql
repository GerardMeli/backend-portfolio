-- V1 : schéma initial du portfolio
-- Conventions : UUID en clé primaire, contenu traduisible dans des tables *_translation
-- (clé composite = id + locale), "position" pour l'ordre, "published" pour brouillon/publié.

-- ---------------------------------------------------------------- Admin
CREATE TABLE admin_user (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- ---------------------------------------------------------------- Profil
CREATE TABLE profile (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name    VARCHAR(150) NOT NULL,
    email        VARCHAR(255) NOT NULL,
    phone        VARCHAR(40),
    location     VARCHAR(150),
    github_url   VARCHAR(500),
    linkedin_url VARCHAR(500),
    avatar_url   VARCHAR(500),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE profile_translation (
    profile_id UUID        NOT NULL REFERENCES profile (id) ON DELETE CASCADE,
    locale     VARCHAR(5)  NOT NULL CHECK (locale IN ('fr', 'en')),
    headline   VARCHAR(255) NOT NULL,
    bio        TEXT,
    PRIMARY KEY (profile_id, locale)
);

-- Variante d'accroche / CV selon le marché ciblé
CREATE TABLE profile_variant (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    market      VARCHAR(10) NOT NULL CHECK (market IN ('AFRICA', 'CANADA', 'EU')),
    locale      VARCHAR(5)  NOT NULL CHECK (locale IN ('fr', 'en')),
    headline    VARCHAR(255),
    summary     TEXT,
    cta_label   VARCHAR(100),
    cv_file_url VARCHAR(500),
    active      BOOLEAN     NOT NULL DEFAULT TRUE,
    UNIQUE (market, locale)
);

-- ---------------------------------------------------------------- Sections de la page
CREATE TABLE section (
    id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code     VARCHAR(50) NOT NULL UNIQUE,
    position INT         NOT NULL,
    visible  BOOLEAN     NOT NULL DEFAULT TRUE
);

CREATE TABLE section_translation (
    section_id UUID         NOT NULL REFERENCES section (id) ON DELETE CASCADE,
    locale     VARCHAR(5)   NOT NULL CHECK (locale IN ('fr', 'en')),
    title      VARCHAR(150) NOT NULL,
    subtitle   VARCHAR(255),
    PRIMARY KEY (section_id, locale)
);

-- ---------------------------------------------------------------- Compétences
CREATE TABLE skill_category (
    id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    position INT NOT NULL DEFAULT 0
);

CREATE TABLE skill_category_translation (
    skill_category_id UUID         NOT NULL REFERENCES skill_category (id) ON DELETE CASCADE,
    locale            VARCHAR(5)   NOT NULL CHECK (locale IN ('fr', 'en')),
    name              VARCHAR(100) NOT NULL,
    PRIMARY KEY (skill_category_id, locale)
);

CREATE TABLE skill (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    category_id UUID         NOT NULL REFERENCES skill_category (id) ON DELETE CASCADE,
    name        VARCHAR(100) NOT NULL,
    level       SMALLINT CHECK (level BETWEEN 1 AND 5),
    icon        VARCHAR(100),
    position    INT          NOT NULL DEFAULT 0
);

-- ---------------------------------------------------------------- Expériences
CREATE TABLE experience (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company         VARCHAR(150) NOT NULL,
    company_url     VARCHAR(500),
    location        VARCHAR(150),
    employment_type VARCHAR(20)  NOT NULL DEFAULT 'FULL_TIME'
        CHECK (employment_type IN ('FULL_TIME', 'PART_TIME', 'INTERNSHIP', 'FREELANCE', 'CONTRACT')),
    start_date      DATE         NOT NULL,
    end_date        DATE,
    position        INT          NOT NULL DEFAULT 0,
    published       BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CHECK (end_date IS NULL OR end_date >= start_date)
);

CREATE TABLE experience_translation (
    experience_id UUID         NOT NULL REFERENCES experience (id) ON DELETE CASCADE,
    locale        VARCHAR(5)   NOT NULL CHECK (locale IN ('fr', 'en')),
    role_title    VARCHAR(150) NOT NULL,
    summary       TEXT,
    PRIMARY KEY (experience_id, locale)
);

-- Une tâche = une ligne, ajoutable / modifiable / supprimable / réordonnable
CREATE TABLE experience_task (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    experience_id UUID NOT NULL REFERENCES experience (id) ON DELETE CASCADE,
    position      INT  NOT NULL DEFAULT 0
);

CREATE TABLE experience_task_translation (
    task_id UUID       NOT NULL REFERENCES experience_task (id) ON DELETE CASCADE,
    locale  VARCHAR(5) NOT NULL CHECK (locale IN ('fr', 'en')),
    content TEXT       NOT NULL,
    PRIMARY KEY (task_id, locale)
);

-- ---------------------------------------------------------------- Formations
CREATE TABLE education (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    institution VARCHAR(200) NOT NULL,
    start_date  DATE,
    end_date    DATE,
    position    INT          NOT NULL DEFAULT 0,
    published   BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE education_translation (
    education_id UUID         NOT NULL REFERENCES education (id) ON DELETE CASCADE,
    locale       VARCHAR(5)   NOT NULL CHECK (locale IN ('fr', 'en')),
    degree       VARCHAR(200) NOT NULL,
    description  TEXT,
    PRIMARY KEY (education_id, locale)
);

-- ---------------------------------------------------------------- Projets
CREATE TABLE project (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    slug            VARCHAR(120) NOT NULL UNIQUE,
    type            VARCHAR(20)  NOT NULL DEFAULT 'PERSONAL'
        CHECK (type IN ('PROFESSIONAL', 'PERSONAL', 'OPEN_SOURCE')),
    repo_url        VARCHAR(500),
    demo_url        VARCHAR(500),
    cover_image_url VARCHAR(500),
    start_date      DATE,
    end_date        DATE,
    featured        BOOLEAN      NOT NULL DEFAULT FALSE,
    position        INT          NOT NULL DEFAULT 0,
    published       BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- Étude de cas : problème / solution / résultats
CREATE TABLE project_translation (
    project_id UUID         NOT NULL REFERENCES project (id) ON DELETE CASCADE,
    locale     VARCHAR(5)   NOT NULL CHECK (locale IN ('fr', 'en')),
    title      VARCHAR(200) NOT NULL,
    summary    VARCHAR(500),
    problem    TEXT,
    solution   TEXT,
    results    TEXT,
    PRIMARY KEY (project_id, locale)
);

CREATE TABLE project_skill (
    project_id UUID NOT NULL REFERENCES project (id) ON DELETE CASCADE,
    skill_id   UUID NOT NULL REFERENCES skill (id) ON DELETE CASCADE,
    PRIMARY KEY (project_id, skill_id)
);

-- ---------------------------------------------------------------- Contact
CREATE TABLE contact_message (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name       VARCHAR(150) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    subject    VARCHAR(200),
    message    TEXT         NOT NULL,
    locale     VARCHAR(5),
    market     VARCHAR(10),
    is_read    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- ---------------------------------------------------------------- Index
CREATE INDEX idx_experience_pub_pos  ON experience (published, position);
CREATE INDEX idx_education_pub_pos   ON education (published, position);
CREATE INDEX idx_project_pub_pos     ON project (published, position);
CREATE INDEX idx_task_experience_pos ON experience_task (experience_id, position);
CREATE INDEX idx_skill_category_pos  ON skill (category_id, position);
CREATE INDEX idx_project_skill_skill ON project_skill (skill_id);
CREATE INDEX idx_contact_created     ON contact_message (created_at DESC);
