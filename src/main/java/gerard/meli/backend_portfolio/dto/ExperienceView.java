package gerard.meli.backend_portfolio.dto;

import java.time.*;
import java.util.*;
import gerard.meli.backend_portfolio.enums.EmploymentType;

public record ExperienceView(
        String company,
        String location,
        String title,
        String description,
        EmploymentType employmentType,
        LocalDate startDate,
        LocalDate endDate,
        boolean current,
        List<String> tasks
) {
}
