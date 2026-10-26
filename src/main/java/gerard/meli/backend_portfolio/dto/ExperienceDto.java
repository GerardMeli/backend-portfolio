package gerard.meli.backend_portfolio.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.time.*;
import java.util.*;
import gerard.meli.backend_portfolio.enums.EmploymentType;

/** Utilisé en entrée (id nul) et en sortie. Textes traduisibles : {"fr": "...", "en": "..."}. visible nul = true. */
public record ExperienceDto(
        String id,
        @NotBlank @Size(max = 200) String company,
        @Size(max = 150) String location,
        @NotEmpty Map<String, String> title,
        Map<String, String> description,
        EmploymentType employmentType,
        @NotNull LocalDate startDate,
        LocalDate endDate,
        boolean current,
        List<String> markets,
        Integer sortOrder,
        Boolean visible,
        @Valid List<ExperienceTaskDto> tasks
) {
}
