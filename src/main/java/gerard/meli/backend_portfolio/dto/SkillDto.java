package gerard.meli.backend_portfolio.dto;

import jakarta.validation.constraints.*;
import java.util.*;

/** Utilisé en entrée (id nul) et en sortie. Textes traduisibles : {"fr": "...", "en": "..."}. visible nul = true. */
public record SkillDto(
        String id,
        String categoryId,
        @NotBlank @Size(max = 100) String name,
        @Min(1) @Max(5) Short level,
        List<String> markets,
        Integer sortOrder,
        Boolean visible
) {
}
