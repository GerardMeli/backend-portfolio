package gerard.meli.backend_portfolio.dto;

import jakarta.validation.constraints.*;
import java.util.*;

/** Utilisé en entrée (id nul) et en sortie. Textes traduisibles : {"fr": "...", "en": "..."}. visible nul = true. level : A1..C2 ou Natif. */
public record LanguageDto(
        String id,
        @NotEmpty Map<String, String> name,
        @Size(max = 20) String level,
        Integer sortOrder,
        Boolean visible
) {
}
