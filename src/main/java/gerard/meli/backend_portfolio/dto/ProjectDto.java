package gerard.meli.backend_portfolio.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.time.*;
import java.util.*;
import gerard.meli.backend_portfolio.enums.ProjectKind;

/** Utilisé en entrée (id nul) et en sortie. Textes traduisibles : {"fr": "...", "en": "..."}. visible nul = true. kind nul = PERSONAL. */
public record ProjectDto(
        String id,
        @NotBlank @Size(max = 200) String name,
        ProjectKind kind,
        Map<String, String> description,
        List<String> techStack,
        @Size(max = 500) String demoUrl,
        @Size(max = 500) String repoUrl,
        LocalDate startDate,
        LocalDate endDate,
        List<String> markets,
        Integer sortOrder,
        Boolean visible,
        @Valid List<ProjectHighlightDto> highlights
) {
}
