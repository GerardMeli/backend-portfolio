package gerard.meli.backend_portfolio.web;

import gerard.meli.backend_portfolio.dto.*;
import gerard.meli.backend_portfolio.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/public")
@Tag(name = "Public", description = "Endpoints publics sans authentification consommés par le frontend")
@RequiredArgsConstructor
public class PublicController {

    private final PortfolioService service;

    @Operation(
            summary = "Lister les marchés disponibles",
            description = "Retourne la liste des marchés disponibles pour le sélecteur du visiteur"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des marchés récupérée avec succès",
                    content = @Content(schema = @Schema(implementation = MarketInfoView.class)))
    })
    @GetMapping("/markets")
    public List<MarketInfoView> markets() {
        return service.listMarkets();
    }

    @Operation(
            summary = "Récupérer le portfolio adapté au marché",
            description = "CV adapté au marché choisi. market et lang sont facultatifs (valeurs par défaut : marché par défaut et langue du marché)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Portfolio récupéré avec succès",
                    content = @Content(schema = @Schema(implementation = PortfolioView.class))),
            @ApiResponse(responseCode = "400", description = "Marché ou langue invalide", content = @Content)
    })
    @GetMapping("/portfolio")
    public PortfolioView portfolio(
            @Parameter(description = "Code du marché (ex: CANADA, FRANCE, CAMEROUN)", example = "CANADA")
            @RequestParam(name = "market", required = false) String market,
            @Parameter(description = "Code langue (ex: fr, en)", example = "en")
            @RequestParam(name = "lang", required = false) String lang) {
        return service.assemble(market, lang);
    }
}
