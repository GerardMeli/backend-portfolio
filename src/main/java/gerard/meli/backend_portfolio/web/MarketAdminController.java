package gerard.meli.backend_portfolio.web;

import gerard.meli.backend_portfolio.dto.*;
import gerard.meli.backend_portfolio.service.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/markets")
public class MarketAdminController {

    private final MarketService service;

    public MarketAdminController(MarketService service) {
        this.service = service;
    }

    @GetMapping
    public List<MarketDto> list() {
        return service.list();
    }

    @GetMapping("/{code}")
    public MarketDto get(@PathVariable("code") String code) {
        return service.get(code);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MarketDto create(@Valid @RequestBody MarketDto dto) {
        return service.create(dto);
    }

    /** Règles de rendu : photo, infos personnelles, format de page, format de date, nombre de pages. */
    @PutMapping("/{code}")
    public MarketDto update(@PathVariable("code") String code, @Valid @RequestBody MarketDto dto) {
        return service.update(code, dto);
    }

    @DeleteMapping("/{code}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("code") String code) {
        service.delete(code);
    }

    // ---------- Sections du marché ----------

    @GetMapping("/{code}/sections")
    public List<MarketSectionDto> listSections(@PathVariable("code") String code) {
        return service.listSections(code);
    }

    /** Ordre, visibilité et plafond d'éléments des sections fournies (les autres restent inchangées). */
    @PutMapping("/{code}/sections")
    public List<MarketSectionDto> replaceSections(@PathVariable("code") String code,
            @RequestBody List<@Valid MarketSectionDto> sections) {
        return service.replaceSections(code, sections);
    }
}
