package gerard.meli.backend_portfolio.dto;

import java.util.*;

/** items contient des ExperienceView, EducationView, ProjectView, SkillGroupView, CertificationView, LanguageView, CustomItemView ou du texte (SUMMARY). */
public record SectionView(
        String type,
        String title,
        List<Object> items
) {
}
