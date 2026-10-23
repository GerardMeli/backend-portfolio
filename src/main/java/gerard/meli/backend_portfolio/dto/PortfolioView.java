package gerard.meli.backend_portfolio.dto;

import java.util.*;

/** Réponse de GET /api/public/portfolio?market=&lang= */
public record PortfolioView(
        MarketInfoView market,
        String lang,
        ProfileView profile,
        List<SectionView> sections
) {
}
