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
@RequestMapping("/api/admin/languages")
@Tag(name = "Administration - Langues", description = "Gestion des langues parlées")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class LanguageAdminController {

    private final LanguageService service;

    @Operation(summary = "Lister les langues", description = "Récupère la liste de toutes les langues")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @GetMapping
    public List<LanguageDto> list() {
        return service.list();
    }

    @Operation(summary = "Récupérer une langue", description = "Récupère une langue par son identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Langue récupérée avec succès",
                    content = @Content(schema = @Schema(implementation = LanguageDto.class))),
            @ApiResponse(responseCode = "404", description = "Langue non trouvée", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @GetMapping("/{id}")
    public LanguageDto get(@Parameter(description = "Identifiant de la langue") @PathVariable("id") String id) {
        return service.get(id);
    }

    @Operation(summary = "Créer une langue", description = "Crée une nouvelle langue")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Langue créée avec succès",
                    content = @Content(schema = @Schema(implementation = LanguageDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LanguageDto create(@Valid @RequestBody LanguageDto dto) {
        return service.create(dto);
    }

    @Operation(summary = "Mettre à jour une langue", description = "Met à jour une langue existante")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Langue mise à jour avec succès",
                    content = @Content(schema = @Schema(implementation = LanguageDto.class))),
            @ApiResponse(responseCode = "404", description = "Langue non trouvée", content = @Content),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @PutMapping("/{id}")
    public LanguageDto update(@Parameter(description = "Identifiant de la langue") @PathVariable("id") String id,
                              @Valid @RequestBody LanguageDto dto) {
        return service.update(id, dto);
    }

    @Operation(summary = "Supprimer une langue", description = "Supprime une langue existante")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Langue supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Langue non trouvée", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Parameter(description = "Identifiant de la langue") @PathVariable("id") String id) {
        service.delete(id);
    }

    @Operation(summary = "Réorganiser les langues", description = "Réordonne les langues selon la liste d'identifiants fournie")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Réorganisation effectuée avec succès"),
            @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @PatchMapping("/reorder")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reorder(@Valid @RequestBody ReorderRequest request) {
        service.reorder(request);
    }
}
