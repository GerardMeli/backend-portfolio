package gerard.meli.backend_portfolio.publicapi;

import gerard.meli.backend_portfolio.common.LocaleSupport;
import gerard.meli.backend_portfolio.common.Market;
import gerard.meli.backend_portfolio.publicapi.PublicDtos.PortfolioResponse;
import gerard.meli.backend_portfolio.publicapi.PublicDtos.ProjectDetailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

/** API ouverte à tous les visiteurs (aucune authentification). Lecture seule. */
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicPortfolioController {

    // 5 minutes de cache navigateur/CDN : une modification faite dans l'admin peut mettre jusqu'à 5 min à apparaître
    private static final CacheControl CACHE = CacheControl.maxAge(Duration.ofMinutes(5)).cachePublic();

    private final PublicPortfolioService service;
    private final LocaleSupport locales;

    /**
     * Tout le contenu en un seul appel (utile avec un hébergeur qui s'endort : un seul réveil).
     * Exemples : /api/public/portfolio?lang=fr&market=canada
     */
    @GetMapping("/portfolio")
    public ResponseEntity<PortfolioResponse> portfolio(
            @RequestParam(required = false) String lang,
            @RequestParam(required = false) String market,
            @RequestHeader(value = HttpHeaders.ACCEPT_LANGUAGE, required = false) String acceptLanguage) {
        String locale = locales.resolve(lang, acceptLanguage);
        return ResponseEntity.ok()
                .cacheControl(CACHE)
                .varyBy(HttpHeaders.ACCEPT_LANGUAGE)
                .body(service.getPortfolio(locale, Market.fromString(market)));
    }

    /** Détail d'un projet (étude de cas complète). */
    @GetMapping("/projects/{slug}")
    public ResponseEntity<ProjectDetailDto> project(
            @PathVariable String slug,
            @RequestParam(required = false) String lang,
            @RequestHeader(value = HttpHeaders.ACCEPT_LANGUAGE, required = false) String acceptLanguage) {
        String locale = locales.resolve(lang, acceptLanguage);
        ProjectDetailDto project = service.getProject(slug, locale)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
        return ResponseEntity.ok()
                .cacheControl(CACHE)
                .varyBy(HttpHeaders.ACCEPT_LANGUAGE)
                .body(project);
    }
}