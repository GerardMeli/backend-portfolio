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
@RequestMapping("/api/admin/educations")
@Tag(name = "Administration - Formations", description = "Gestion des formations")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class EducationAdminController {

    private final EducationService service;

    @Operation(summary = "Lister les formations", description = "Récupère la liste de toutes les formations")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @GetMapping
    public List<EducationDto> list() {
        return service.list();
    }

    @Operation(summary = "Récupérer une formation", description = "Récupère une formation par son identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Formation récupérée avec succès",
                    content = @Content(schema = @Schema(implementation = EducationDto.class))),
            @ApiResponse(responseCode = "404", description = "Formation non trouvée", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @GetMapping("/{id}")
    public EducationDto get(@Parameter(description = "Identifiant de la formation") @PathVariable("id") String id) {
        return service.get(id);
    }

    @Operation(summary = "Créer une formation", description = "Crée une nouvelle formation")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Formation créée avec succès",
                    content = @Content(schema = @Schema(implementation = EducationDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EducationDto create(@Valid @RequestBody EducationDto dto) {
        return service.create(dto);
    }

    @Operation(summary = "Mettre à jour une formation", description = "Met à jour une formation existante")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Formation mise à jour avec succès",
                    content = @Content(schema = @Schema(implementation = EducationDto.class))),
            @ApiResponse(responseCode = "404", description = "Formation non trouvée", content = @Content),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @PutMapping("/{id}")
    public EducationDto update(@Parameter(description = "Identifiant de la formation") @PathVariable("id") String id,
                               @Valid @RequestBody EducationDto dto) {
        return service.update(id, dto);
    }

    @Operation(summary = "Supprimer une formation", description = "Supprime une formation existante")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Formation supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Formation non trouvée", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Parameter(description = "Identifiant de la formation") @PathVariable("id") String id) {
        service.delete(id);
    }

    @Operation(summary = "Réorganiser les formations", description = "Réordonne les formations selon la liste d'identifiants fournie")
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
