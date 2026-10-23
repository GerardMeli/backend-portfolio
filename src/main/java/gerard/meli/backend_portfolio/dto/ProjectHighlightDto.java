package gerard.meli.backend_portfolio.dto;

import jakarta.validation.constraints.*;
import java.util.*;

/** Utilisé en entrée (id nul) et en sortie. Textes traduisibles : {"fr": "...", "en": "..."}. visible nul = true. */
public record ProjectHighlightDto(
        Long id,
        @NotEmpty Map<String, String> content,
        Integer sortOrder,
        Boolean visible
) {
}
