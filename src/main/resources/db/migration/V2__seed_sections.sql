-- V2 : sections par défaut de la page d'accueil (modifiables ensuite depuis l'admin)
INSERT INTO section (code, position) VALUES
    ('hero', 1),
    ('about', 2),
    ('experience', 3),
    ('projects', 4),
    ('skills', 5),
    ('education', 6),
    ('contact', 7);

INSERT INTO section_translation (section_id, locale, title)
SELECT s.id, t.locale, t.title
FROM section s
JOIN (VALUES
    ('hero',       'fr', 'Accueil'),
    ('hero',       'en', 'Home'),
    ('about',      'fr', 'À propos'),
    ('about',      'en', 'About'),
    ('experience', 'fr', 'Expériences'),
    ('experience', 'en', 'Experience'),
    ('projects',   'fr', 'Projets'),
    ('projects',   'en', 'Projects'),
    ('skills',     'fr', 'Compétences'),
    ('skills',     'en', 'Skills'),
    ('education',  'fr', 'Formation'),
    ('education',  'en', 'Education'),
    ('contact',    'fr', 'Contact'),
    ('contact',    'en', 'Contact')
) AS t (code, locale, title) ON t.code = s.code;
