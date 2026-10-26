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
@RequestMapping("/api/admin/skill-categories")
@Tag(name = "Administration - Compétences", description = "Gestion des catégories de compétences et compétences")
@SecurityRequirement(name = "bearerAuth")
public class SkillAdminController {

    private final SkillService service;

    public SkillAdminController(SkillService service) {
        this.service = service;
    }

    @Operation(summary = "Lister les catégories de compétences", description = "Récupère la liste de toutes les catégories de compétences")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @GetMapping
    public List<SkillCategoryDto> list() {
        return service.list();
    }

    @Operation(summary = "Récupérer une catégorie de compétences", description = "Récupère une catégorie par son identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Catégorie récupérée avec succès",
                    content = @Content(schema = @Schema(implementation = SkillCategoryDto.class))),
            @ApiResponse(responseCode = "404", description = "Catégorie non trouvée", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @GetMapping("/{id}")
    public SkillCategoryDto get(@Parameter(description = "Identifiant de la catégorie") @PathVariable("id") Long id) {
        return service.get(id);
    }

    @Operation(summary = "Créer une catégorie de compétences", description = "Crée une nouvelle catégorie")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Catégorie créée avec succès",
                    content = @Content(schema = @Schema(implementation = SkillCategoryDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SkillCategoryDto create(@Valid @RequestBody SkillCategoryDto dto) {
        return service.create(dto);
    }

    @Operation(summary = "Mettre à jour une catégorie", description = "Met à jour une catégorie existante")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Catégorie mise à jour avec succès",
                    content = @Content(schema = @Schema(implementation = SkillCategoryDto.class))),
            @ApiResponse(responseCode = "404", description = "Catégorie non trouvée", content = @Content),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @PutMapping("/{id}")
    public SkillCategoryDto update(@Parameter(description = "Identifiant de la catégorie") @PathVariable("id") Long id,
                                   @Valid @RequestBody SkillCategoryDto dto) {
        return service.update(id, dto);
    }

    @Operation(summary = "Supprimer une catégorie", description = "Supprime une catégorie existante")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Catégorie supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Catégorie non trouvée", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Parameter(description = "Identifiant de la catégorie") @PathVariable("id") Long id) {
        service.delete(id);
    }

    @Operation(summary = "Réorganiser les catégories", description = "Réordonne les catégories selon la liste d'identifiants fournie")
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

    @Operation(summary = "Lister les compétences d'une catégorie", description = "Récupère les compétences associées à une catégorie")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Compétences récupérées avec succès"),
            @ApiResponse(responseCode = "404", description = "Catégorie non trouvée", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @GetMapping("/{id}/skills")
    public List<SkillDto> listSkills(@Parameter(description = "Identifiant de la catégorie") @PathVariable("id") Long id) {
        return service.listSkills(id);
    }

    @Operation(summary = "Ajouter une compétence à une catégorie", description = "Ajoute une nouvelle compétence à une catégorie")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Compétence créée avec succès",
                    content = @Content(schema = @Schema(implementation = SkillDto.class))),
            @ApiResponse(responseCode = "404", description = "Catégorie non trouvée", content = @Content),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @PostMapping("/{id}/skills")
    @ResponseStatus(HttpStatus.CREATED)
    public SkillDto addSkill(@Parameter(description = "Identifiant de la catégorie") @PathVariable("id") Long id,
                             @Valid @RequestBody SkillDto dto) {
        return service.addSkill(id, dto);
    }

    @Operation(summary = "Mettre à jour une compétence", description = "Met à jour une compétence d'une catégorie")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Compétence mise à jour avec succès",
                    content = @Content(schema = @Schema(implementation = SkillDto.class))),
            @ApiResponse(responseCode = "404", description = "Catégorie ou compétence non trouvée", content = @Content),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @PutMapping("/{id}/skills/{childId}")
    public SkillDto updateSkill(@Parameter(description = "Identifiant de la catégorie") @PathVariable("id") Long id,
                                @Parameter(description = "Identifiant de la compétence") @PathVariable("childId") Long childId,
                                @Valid @RequestBody SkillDto dto) {
        return service.updateSkill(id, childId, dto);
    }

    @Operation(summary = "Supprimer une compétence", description = "Supprime une compétence d'une catégorie")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Compétence supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Catégorie ou compétence non trouvée", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @DeleteMapping("/{id}/skills/{childId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSkill(@Parameter(description = "Identifiant de la catégorie") @PathVariable("id") Long id,
                            @Parameter(description = "Identifiant de la compétence") @PathVariable("childId") Long childId) {
        service.deleteSkill(id, childId);
    }

    @Operation(summary = "Réorganiser les compétences", description = "Réordonne les compétences d'une catégorie selon la liste d'identifiants fournie")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Réorganisation effectuée avec succès"),
            @ApiResponse(responseCode = "404", description = "Catégorie non trouvée", content = @Content),
            @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @PatchMapping("/{id}/skills/reorder")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reorderSkills(@Parameter(description = "Identifiant de la catégorie") @PathVariable("id") Long id,
                              @Valid @RequestBody ReorderRequest request) {
        service.reorderSkills(id, request);
    }
}
