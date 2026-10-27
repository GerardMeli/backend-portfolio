package gerard.meli.backend_portfolio.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.util.*;
import gerard.meli.backend_portfolio.enums.SkillCategoryType;

/** Utilisé en entrée (id nul) et en sortie. Textes traduisibles : {"fr": "...", "en": "..."}. visible nul = true. */
public record SkillCategoryDto(
        String id,
        @NotEmpty Map<String, String> name,
        SkillCategoryType type,
        Integer sortOrder,
        Boolean visible,
        @Valid List<SkillDto> skills
) {
}
