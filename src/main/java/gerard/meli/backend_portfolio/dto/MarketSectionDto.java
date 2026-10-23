package gerard.meli.backend_portfolio.dto;

import jakarta.validation.constraints.*;

/** Ordre, visibilité et plafond d'éléments d'une section pour un marché. */
public record MarketSectionDto(
        @NotBlank @Size(max = 50) String sectionType,
        @NotNull Integer sortOrder,
        boolean visible,
        @Min(1) Integer maxItems
) {
}
