package gerard.meli.backend_portfolio.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.util.*;

/** Utilisé en entrée (id nul) et en sortie. Textes traduisibles : {"fr": "...", "en": "..."}. visible nul = true. */
public record SkillCategoryDto(
        String id,
        @NotEmpty Map<String, String> name,
        Integer sortOrder,
        Boolean visible,
        @Valid List<SkillDto> skills
) {
}
