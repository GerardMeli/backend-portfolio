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
@RequestMapping("/api/admin/experiences")
@Tag(name = "Administration - Expériences", description = "Gestion des expériences professionnelles")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class ExperienceAdminController {

    private final ExperienceService service;

    @Operation(summary = "Lister les expériences", description = "Récupère la liste de toutes les expériences")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @GetMapping
    public List<ExperienceDto> list() {
        return service.list();
    }

    @Operation(summary = "Récupérer une expérience", description = "Récupère une expérience par son identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Expérience récupérée avec succès",
                    content = @Content(schema = @Schema(implementation = ExperienceDto.class))),
            @ApiResponse(responseCode = "404", description = "Expérience non trouvée", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @GetMapping("/{id}")
    public ExperienceDto get(@Parameter(description = "Identifiant de l'expérience") @PathVariable("id") String id) {
        return service.get(id);
    }

    @Operation(summary = "Créer une expérience", description = "Crée une nouvelle expérience")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Expérience créée avec succès",
                    content = @Content(schema = @Schema(implementation = ExperienceDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExperienceDto create(@Valid @RequestBody ExperienceDto dto) {
        return service.create(dto);
    }

    @Operation(summary = "Mettre à jour une expérience", description = "Met à jour une expérience existante")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Expérience mise à jour avec succès",
                    content = @Content(schema = @Schema(implementation = ExperienceDto.class))),
            @ApiResponse(responseCode = "404", description = "Expérience non trouvée", content = @Content),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @PutMapping("/{id}")
    public ExperienceDto update(@Parameter(description = "Identifiant de l'expérience") @PathVariable("id") String id,
                                @Valid @RequestBody ExperienceDto dto) {
        return service.update(id, dto);
    }

    @Operation(summary = "Supprimer une expérience", description = "Supprime une expérience existante")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Expérience supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Expérience non trouvée", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Parameter(description = "Identifiant de l'expérience") @PathVariable("id") String id) {
        service.delete(id);
    }

    @Operation(summary = "Réorganiser les expériences", description = "Réordonne les expériences selon la liste d'identifiants fournie")
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

    @Operation(summary = "Lister les tâches d'une expérience", description = "Récupère les tâches associées à une expérience")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tâches récupérées avec succès"),
            @ApiResponse(responseCode = "404", description = "Expérience non trouvée", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @GetMapping("/{id}/tasks")
    public List<ExperienceTaskDto> listTasks(@Parameter(description = "Identifiant de l'expérience") @PathVariable("id") String id) {
        return service.listTasks(id);
    }

    @Operation(summary = "Ajouter une tâche à une expérience", description = "Ajoute une nouvelle tâche à une expérience")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tâche créée avec succès",
                    content = @Content(schema = @Schema(implementation = ExperienceTaskDto.class))),
            @ApiResponse(responseCode = "404", description = "Expérience non trouvée", content = @Content),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @PostMapping("/{id}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public ExperienceTaskDto addTask(@Parameter(description = "Identifiant de l'expérience") @PathVariable("id") String id,
                                     @Valid @RequestBody ExperienceTaskDto dto) {
        return service.addTask(id, dto);
    }

    @Operation(summary = "Mettre à jour une tâche", description = "Met à jour une tâche d'une expérience")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tâche mise à jour avec succès",
                    content = @Content(schema = @Schema(implementation = ExperienceTaskDto.class))),
            @ApiResponse(responseCode = "404", description = "Expérience ou tâche non trouvée", content = @Content),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @PutMapping("/{id}/tasks/{childId}")
    public ExperienceTaskDto updateTask(@Parameter(description = "Identifiant de l'expérience") @PathVariable("id") String id,
                                        @Parameter(description = "Identifiant de la tâche") @PathVariable("childId") String childId,
                                        @Valid @RequestBody ExperienceTaskDto dto) {
        return service.updateTask(id, childId, dto);
    }

    @Operation(summary = "Supprimer une tâche", description = "Supprime une tâche d'une expérience")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tâche supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Expérience ou tâche non trouvée", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @DeleteMapping("/{id}/tasks/{childId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@Parameter(description = "Identifiant de l'expérience") @PathVariable("id") String id,
                           @Parameter(description = "Identifiant de la tâche") @PathVariable("childId") String childId) {
        service.deleteTask(id, childId);
    }

    @Operation(summary = "Réorganiser les tâches", description = "Réordonne les tâches d'une expérience selon la liste d'identifiants fournie")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Réorganisation effectuée avec succès"),
            @ApiResponse(responseCode = "404", description = "Expérience non trouvée", content = @Content),
            @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @PatchMapping("/{id}/tasks/reorder")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reorderTasks(@Parameter(description = "Identifiant de l'expérience") @PathVariable("id") String id,
                             @Valid @RequestBody ReorderRequest request) {
        service.reorderTasks(id, request);
    }
}
