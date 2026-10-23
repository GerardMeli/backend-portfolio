package gerard.meli.backend_portfolio.dto;

import java.time.*;

public record CustomItemView(
        String title,
        String subtitle,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        String url
) {
}
