package gerard.meli.backend_portfolio.dto;

import java.time.*;
import java.util.*;
import gerard.meli.backend_portfolio.enums.ProjectKind;

public record ProjectView(
        String name,
        ProjectKind kind,
        String description,
        List<String> techStack,
        String demoUrl,
        String repoUrl,
        LocalDate startDate,
        LocalDate endDate,
        List<String> highlights
) {
}
