package gerard.meli.backend_portfolio.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.util.*;

/** Utilisé en entrée (id nul) et en sortie. Textes traduisibles : {"fr": "...", "en": "..."}. visible nul = true. */
public record CustomSectionDto(
        String id,
        @NotBlank @Pattern(regexp = "^[A-Z0-9_]{2,50}$", message = "code : majuscules, chiffres et _ (2 à 50 caractères)") String code,
        @NotEmpty Map<String, String> title,
        List<String> markets,
        Integer sortOrder,
        Boolean visible,
        @Valid List<CustomItemDto> items
) {
}
