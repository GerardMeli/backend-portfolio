package gerard.meli.backend_portfolio.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.util.*;
import gerard.meli.backend_portfolio.enums.SkillKind;

/** Utilisé en entrée (id nul) et en sortie. Textes traduisibles : {"fr": "...", "en": "..."}. visible nul = true, kind nul = PROFESSIONAL. */
public record SkillCategoryDto(
        String id,
        @NotEmpty Map<String, String> name,
        SkillKind kind,
        Integer sortOrder,
        Boolean visible,
        @Valid List<SkillDto> skills
) {
}
