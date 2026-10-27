package gerard.meli.backend_portfolio.service;

import gerard.meli.backend_portfolio.enums.SectionTypes;

import java.util.Map;

/** Titres des sections intégrées selon la langue (fr, en ; repli sur en). Les sections perso ont leur propre titre. */
public final class SectionTitles {

    private static final Map<String, Map<String, String>> TITLES = Map.of(
            SectionTypes.SUMMARY, Map.of("fr", "Profil", "en", "Summary"),
            SectionTypes.EXPERIENCE, Map.of("fr", "Expérience professionnelle", "en", "Experience"),
            SectionTypes.EDUCATION, Map.of("fr", "Formation", "en", "Education"),
            SectionTypes.PROJECT, Map.of("fr", "Projets", "en", "Projects"),
            SectionTypes.SKILL, Map.of("fr", "Compétences", "en", "Skills"),
            SectionTypes.PERSONAL_SKILL, Map.of("fr", "Compétences personnelles", "en", "Personal skills"),
            SectionTypes.PROFESSIONAL_SKILL, Map.of("fr", "Compétences professionnelles", "en", "Professional skills"),
            SectionTypes.CERTIFICATION, Map.of("fr", "Certifications", "en", "Certifications"),
            SectionTypes.LANGUAGE, Map.of("fr", "Langues", "en", "Languages"));

    private SectionTitles() {
    }

    public static String of(String type, String lang) {
        Map<String, String> byLang = TITLES.get(type);
        if (byLang == null) return type;
        return byLang.getOrDefault(lang, byLang.get("en"));
    }
}
