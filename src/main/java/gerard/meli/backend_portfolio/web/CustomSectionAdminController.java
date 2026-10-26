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
@RequestMapping("/api/admin/custom-sections")
@Tag(name = "Administration - Sections personnalisées", description = "Gestion des sections personnalisées")
@SecurityRequirement(name = "bearerAuth")
public class CustomSectionAdminController {

    private final CustomSectionService service;

    public CustomSectionAdminController(CustomSectionService service) {
        this.service = service;
    }

    @Operation(summary = "Lister les sections personnalisées", description = "Récupère la liste de toutes les sections personnalisées")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @GetMapping
    public List<CustomSectionDto> list() {
        return service.list();
    }

    @Operation(summary = "Récupérer une section personnalisée", description = "Récupère une section par son identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Section récupérée avec succès",
                    content = @Content(schema = @Schema(implementation = CustomSectionDto.class))),
            @ApiResponse(responseCode = "404", description = "Section non trouvée", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @GetMapping("/{id}")
    public CustomSectionDto get(@Parameter(description = "Identifiant de la section") @PathVariable("id") Long id) {
        return service.get(id);
    }

    @Operation(summary = "Créer une section personnalisée", description = "Crée une nouvelle section personnalisée")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Section créée avec succès",
                    content = @Content(schema = @Schema(implementation = CustomSectionDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomSectionDto create(@Valid @RequestBody CustomSectionDto dto) {
        return service.create(dto);
    }

    @Operation(summary = "Mettre à jour une section personnalisée", description = "Met à jour une section existante")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Section mise à jour avec succès",
                    content = @Content(schema = @Schema(implementation = CustomSectionDto.class))),
            @ApiResponse(responseCode = "404", description = "Section non trouvée", content = @Content),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @PutMapping("/{id}")
    public CustomSectionDto update(@Parameter(description = "Identifiant de la section") @PathVariable("id") Long id,
                                   @Valid @RequestBody CustomSectionDto dto) {
        return service.update(id, dto);
    }

    @Operation(summary = "Supprimer une section personnalisée", description = "Supprime une section existante")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Section supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Section non trouvée", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Parameter(description = "Identifiant de la section") @PathVariable("id") Long id) {
        service.delete(id);
    }

    @Operation(summary = "Lister les éléments d'une section personnalisée", description = "Récupère les éléments associés à une section")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Éléments récupérés avec succès"),
            @ApiResponse(responseCode = "404", description = "Section non trouvée", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @GetMapping("/{id}/items")
    public List<CustomItemDto> listItems(@Parameter(description = "Identifiant de la section") @PathVariable("id") Long id) {
        return service.listItems(id);
    }

    @Operation(summary = "Ajouter un élément à une section", description = "Ajoute un nouvel élément à une section personnalisée")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Élément créé avec succès",
                    content = @Content(schema = @Schema(implementation = CustomItemDto.class))),
            @ApiResponse(responseCode = "404", description = "Section non trouvée", content = @Content),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @PostMapping("/{id}/items")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomItemDto addItem(@Parameter(description = "Identifiant de la section") @PathVariable("id") Long id,
                                @Valid @RequestBody CustomItemDto dto) {
        return service.addItem(id, dto);
    }

    @Operation(summary = "Mettre à jour un élément", description = "Met à jour un élément d'une section personnalisée")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Élément mis à jour avec succès",
                    content = @Content(schema = @Schema(implementation = CustomItemDto.class))),
            @ApiResponse(responseCode = "404", description = "Section ou élément non trouvé", content = @Content),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @PutMapping("/{id}/items/{childId}")
    public CustomItemDto updateItem(@Parameter(description = "Identifiant de la section") @PathVariable("id") Long id,
                                   @Parameter(description = "Identifiant de l'élément") @PathVariable("childId") Long childId,
                                   @Valid @RequestBody CustomItemDto dto) {
        return service.updateItem(id, childId, dto);
    }

    @Operation(summary = "Supprimer un élément", description = "Supprime un élément d'une section personnalisée")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Élément supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Section ou élément non trouvé", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @DeleteMapping("/{id}/items/{childId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@Parameter(description = "Identifiant de la section") @PathVariable("id") Long id,
                           @Parameter(description = "Identifiant de l'élément") @PathVariable("childId") Long childId) {
        service.deleteItem(id, childId);
    }

    @Operation(summary = "Réorganiser les éléments", description = "Réordonne les éléments d'une section selon la liste d'identifiants fournie")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Réorganisation effectuée avec succès"),
            @ApiResponse(responseCode = "404", description = "Section non trouvée", content = @Content),
            @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @PatchMapping("/{id}/items/reorder")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reorderItems(@Parameter(description = "Identifiant de la section") @PathVariable("id") Long id,
                             @Valid @RequestBody ReorderRequest request) {
        service.reorderItems(id, request);
    }
}
