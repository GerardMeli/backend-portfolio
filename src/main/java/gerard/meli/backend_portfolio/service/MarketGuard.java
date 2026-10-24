package gerard.meli.backend_portfolio.service;

import gerard.meli.backend_portfolio.domain.Market;
import gerard.meli.backend_portfolio.exception.BadRequestException;
import gerard.meli.backend_portfolio.repository.MarketRepository;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

/** Vérifie que les codes de marchés utilisés dans les DTO existent. */
@Component
public class MarketGuard {

    private final MarketRepository marketRepository;

    public MarketGuard(MarketRepository marketRepository) {
        this.marketRepository = marketRepository;
    }

    public void check(List<String> markets) {
        if (markets == null || markets.isEmpty()) return;
        Set<String> known = marketRepository.findAll().stream().map(Market::getCode).collect(Collectors.toSet());
        List<String> unknown = markets.stream()
                .map(m -> m.trim().toUpperCase(Locale.ROOT))
                .filter(m -> !known.contains(m))
                .toList();
        if (!unknown.isEmpty()) {
            throw new BadRequestException("Marché(s) inconnu(s) : " + String.join(", ", unknown));
        }
    }
}
