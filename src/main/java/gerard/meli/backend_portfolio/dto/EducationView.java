package gerard.meli.backend_portfolio.dto;

import java.time.*;

public record EducationView(
        String institution,
        String location,
        String degree,
        String fieldOfStudy,
        String description,
        LocalDate startDate,
        LocalDate endDate
) {
}
