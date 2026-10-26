package gerard.meli.backend_portfolio.dto;

import jakarta.validation.constraints.*;
import java.time.*;
import java.util.*;

/** Utilisé en entrée (id nul) et en sortie. Textes traduisibles : {"fr": "...", "en": "..."}. visible nul = true. */
public record EducationDto(
        String id,
        @NotBlank @Size(max = 200) String institution,
        @Size(max = 150) String location,
        @NotEmpty Map<String, String> degree,
        Map<String, String> fieldOfStudy,
        Map<String, String> description,
        LocalDate startDate,
        LocalDate endDate,
        List<String> markets,
        Integer sortOrder,
        Boolean visible
) {
}
