package gerard.meli.backend_portfolio.web;

import gerard.meli.backend_portfolio.dto.*;
import gerard.meli.backend_portfolio.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/markets")
@Tag(name = "Administration - Marchés", description = "Gestion des marchés et configuration des sections")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class MarketAdminController {

    private final MarketService service;

    @Operation(summary = "Lister les marchés", description = "Récupère la liste de tous les marchés")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @GetMapping
    public List<MarketDto> list() {
        return service.list();
    }

    @Operation(summary = "Récupérer un marché", description = "Récupère un marché par son code")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Marché récupéré avec succès",
                    content = @Content(schema = @Schema(implementation = MarketDto.class))),
            @ApiResponse(responseCode = "404", description = "Marché non trouvé", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @GetMapping("/{code}")
    public MarketDto get(@Parameter(description = "Code du marché (ex: CANADA, FRANCE)") @PathVariable("code") String code) {
        return service.get(code);
    }

    @Operation(summary = "Créer un marché", description = "Crée un nouveau marché")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Marché créé avec succès",
                    content = @Content(schema = @Schema(implementation = MarketDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content),
            @ApiResponse(responseCode = "409", description = "Marché déjà existant", content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MarketDto create(@Valid @RequestBody MarketDto dto) {
        return service.create(dto);
    }

    @Operation(
            summary = "Mettre à jour un marché",
            description = "Met à jour les règles de rendu d'un marché : photo, infos personnelles, format de page, format de date, nombre de pages"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Marché mis à jour avec succès",
                    content = @Content(schema = @Schema(implementation = MarketDto.class))),
            @ApiResponse(responseCode = "404", description = "Marché non trouvé", content = @Content),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @PutMapping("/{code}")
    public MarketDto update(@Parameter(description = "Code du marché") @PathVariable("code") String code,
                            @Valid @RequestBody MarketDto dto) {
        return service.update(code, dto);
    }

    @Operation(summary = "Supprimer un marché", description = "Supprime un marché existant")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Marché supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Marché non trouvé", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @DeleteMapping("/{code}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Parameter(description = "Code du marché") @PathVariable("code") String code) {
        service.delete(code);
    }

    @Operation(summary = "Lister les sections d'un marché", description = "Récupère la configuration des sections pour un marché donné")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sections récupérées avec succès"),
            @ApiResponse(responseCode = "404", description = "Marché non trouvé", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @GetMapping("/{code}/sections")
    public List<MarketSectionDto> listSections(@Parameter(description = "Code du marché") @PathVariable("code") String code) {
        return service.listSections(code);
    }

    @Operation(
            summary = "Remplacer les sections d'un marché",
            description = "Met à jour l'ordre, la visibilité et le plafond d'éléments des sections fournies (les autres restent inchangées)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sections mises à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Marché non trouvé", content = @Content),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @PutMapping("/{code}/sections")
    public List<MarketSectionDto> replaceSections(@Parameter(description = "Code du marché") @PathVariable("code") String code,
                                                 @RequestBody List<@Valid MarketSectionDto> sections) {
        return service.replaceSections(code, sections);
    }
}
