package gerard.meli.backend_portfolio.dto;

import gerard.meli.backend_portfolio.enums.PageFormat;

/** Marché pour le sélecteur et les règles de rendu côté Angular. */
public record MarketInfoView(
        String code,
        String label,
        String defaultLocale,
        PageFormat pageFormat,
        String dateFormat,
        boolean showPhoto,
        boolean showPersonalDetails,
        short maxPages
) {
}
