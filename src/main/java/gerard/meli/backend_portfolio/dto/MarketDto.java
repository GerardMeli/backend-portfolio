package gerard.meli.backend_portfolio.dto;

import jakarta.validation.constraints.*;
import gerard.meli.backend_portfolio.enums.PageFormat;

/** Règles de rendu d'un marché (admin) ; aussi renvoyé au public. */
public record MarketDto(
        @NotBlank @Size(max = 20) String code,
        @NotBlank @Size(max = 100) String label,
        @NotBlank @Size(max = 5) String defaultLocale,
        @NotNull PageFormat pageFormat,
        @NotBlank @Size(max = 20) String dateFormat,
        boolean showPhoto,
        boolean showPersonalDetails,
        @Min(1) @Max(10) Short maxPages,
        String notes
) {
}
