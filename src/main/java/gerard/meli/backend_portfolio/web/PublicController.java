package gerard.meli.backend_portfolio.web;

import gerard.meli.backend_portfolio.dto.*;
import gerard.meli.backend_portfolio.service.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/** Endpoints publics (sans authentification) consommés par le frontend Angular. */
@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final PortfolioService service;

    public PublicController(PortfolioService service) {
        this.service = service;
    }

    /** Marchés disponibles pour le sélecteur du visiteur. */
    @GetMapping("/markets")
    public List<MarketInfoView> markets() {
        return service.listMarkets();
    }

    /**
     * CV adapté au marché choisi.
     * Exemple : GET /api/public/portfolio?market=CANADA&lang=en
     * market et lang sont facultatifs (valeurs par défaut : marché par défaut et langue du marché).
     */
    @GetMapping("/portfolio")
    public PortfolioView portfolio(@RequestParam(name = "market", required = false) String market,
            @RequestParam(name = "lang", required = false) String lang) {
        return service.assemble(market, lang);
    }
}
