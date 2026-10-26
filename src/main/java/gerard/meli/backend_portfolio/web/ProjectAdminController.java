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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/projects")
@Tag(name = "Administration - Projets", description = "Gestion des projets")
@SecurityRequirement(name = "bearerAuth")
public class ProjectAdminController {

    private final ProjectService service;

    public ProjectAdminController(ProjectService service) {
        this.service = service;
    }

    @Operation(summary = "Lister les projets", description = "Récupère la liste de tous les projets")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @GetMapping
    public List<ProjectDto> list() {
        return service.list();
    }

    @Operation(summary = "Récupérer un projet", description = "Récupère un projet par son identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Projet récupéré avec succès",
                    content = @Content(schema = @Schema(implementation = ProjectDto.class))),
            @ApiResponse(responseCode = "404", description = "Projet non trouvé", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @GetMapping("/{id}")
    public ProjectDto get(@Parameter(description = "Identifiant du projet") @PathVariable("id") Long id) {
        return service.get(id);
    }

    @Operation(summary = "Créer un projet", description = "Crée un nouveau projet")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Projet créé avec succès",
                    content = @Content(schema = @Schema(implementation = ProjectDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDto create(@Valid @RequestBody ProjectDto dto) {
        return service.create(dto);
    }

    @Operation(summary = "Mettre à jour un projet", description = "Met à jour un projet existant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Projet mis à jour avec succès",
                    content = @Content(schema = @Schema(implementation = ProjectDto.class))),
            @ApiResponse(responseCode = "404", description = "Projet non trouvé", content = @Content),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @PutMapping("/{id}")
    public ProjectDto update(@Parameter(description = "Identifiant du projet") @PathVariable("id") Long id,
                             @Valid @RequestBody ProjectDto dto) {
        return service.update(id, dto);
    }

    @Operation(summary = "Supprimer un projet", description = "Supprime un projet existant")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Projet supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Projet non trouvé", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Parameter(description = "Identifiant du projet") @PathVariable("id") Long id) {
        service.delete(id);
    }

    @Operation(summary = "Réorganiser les projets", description = "Réordonne les projets selon la liste d'identifiants fournie")
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

    @Operation(summary = "Lister les points clés d'un projet", description = "Récupère les points clés associés à un projet")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Points clés récupérés avec succès"),
            @ApiResponse(responseCode = "404", description = "Projet non trouvé", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @GetMapping("/{id}/highlights")
    public List<ProjectHighlightDto> listHighlights(@Parameter(description = "Identifiant du projet") @PathVariable("id") Long id) {
        return service.listHighlights(id);
    }

    @Operation(summary = "Ajouter un point clé à un projet", description = "Ajoute un nouveau point clé à un projet")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Point clé créé avec succès",
                    content = @Content(schema = @Schema(implementation = ProjectHighlightDto.class))),
            @ApiResponse(responseCode = "404", description = "Projet non trouvé", content = @Content),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @PostMapping("/{id}/highlights")
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectHighlightDto addHighlight(@Parameter(description = "Identifiant du projet") @PathVariable("id") Long id,
                                           @Valid @RequestBody ProjectHighlightDto dto) {
        return service.addHighlight(id, dto);
    }

    @Operation(summary = "Mettre à jour un point clé", description = "Met à jour un point clé d'un projet")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Point clé mis à jour avec succès",
                    content = @Content(schema = @Schema(implementation = ProjectHighlightDto.class))),
            @ApiResponse(responseCode = "404", description = "Projet ou point clé non trouvé", content = @Content),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @PutMapping("/{id}/highlights/{childId}")
    public ProjectHighlightDto updateHighlight(@Parameter(description = "Identifiant du projet") @PathVariable("id") Long id,
                                              @Parameter(description = "Identifiant du point clé") @PathVariable("childId") Long childId,
                                              @Valid @RequestBody ProjectHighlightDto dto) {
        return service.updateHighlight(id, childId, dto);
    }

    @Operation(summary = "Supprimer un point clé", description = "Supprime un point clé d'un projet")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Point clé supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Projet ou point clé non trouvé", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @DeleteMapping("/{id}/highlights/{childId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHighlight(@Parameter(description = "Identifiant du projet") @PathVariable("id") Long id,
                                @Parameter(description = "Identifiant du point clé") @PathVariable("childId") Long childId) {
        service.deleteHighlight(id, childId);
    }

    @Operation(summary = "Réorganiser les points clés", description = "Réordonne les points clés d'un projet selon la liste d'identifiants fournie")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Réorganisation effectuée avec succès"),
            @ApiResponse(responseCode = "404", description = "Projet non trouvé", content = @Content),
            @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @PatchMapping("/{id}/highlights/reorder")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reorderHighlights(@Parameter(description = "Identifiant du projet") @PathVariable("id") Long id,
                                  @Valid @RequestBody ReorderRequest request) {
        service.reorderHighlights(id, request);
    }
}
