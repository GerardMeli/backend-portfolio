package gerard.meli.backend_portfolio.dto;

import jakarta.validation.constraints.*;
import java.time.*;
import java.util.*;

/** Utilisé en entrée (id nul) et en sortie. Textes traduisibles : {"fr": "...", "en": "..."}. visible nul = true. */
public record CustomItemDto(
        String id,
        @NotEmpty Map<String, String> title,
        @Size(max = 200) String subtitle,
        Map<String, String> description,
        LocalDate startDate,
        LocalDate endDate,
        @Size(max = 500) @Pattern(regexp = "^https?://.+", message = "doit commencer par http:// ou https://") String url,
        List<String> markets,
        Integer sortOrder,
        Boolean visible
) {
}