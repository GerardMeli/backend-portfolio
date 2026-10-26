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
@RequestMapping("/api/admin/certifications")
@Tag(name = "Administration - Certifications", description = "Gestion des certifications")
@SecurityRequirement(name = "bearerAuth")
public class CertificationAdminController {

    private final CertificationService service;

    public CertificationAdminController(CertificationService service) {
        this.service = service;
    }

    @Operation(summary = "Lister les certifications", description = "Récupère la liste de toutes les certifications")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @GetMapping
    public List<CertificationDto> list() {
        return service.list();
    }

    @Operation(summary = "Récupérer une certification", description = "Récupère une certification par son identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Certification récupérée avec succès",
                    content = @Content(schema = @Schema(implementation = CertificationDto.class))),
            @ApiResponse(responseCode = "404", description = "Certification non trouvée", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @GetMapping("/{id}")
    public CertificationDto get(@Parameter(description = "Identifiant de la certification") @PathVariable("id") Long id) {
        return service.get(id);
    }

    @Operation(summary = "Créer une certification", description = "Crée une nouvelle certification")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Certification créée avec succès",
                    content = @Content(schema = @Schema(implementation = CertificationDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CertificationDto create(@Valid @RequestBody CertificationDto dto) {
        return service.create(dto);
    }

    @Operation(summary = "Mettre à jour une certification", description = "Met à jour une certification existante")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Certification mise à jour avec succès",
                    content = @Content(schema = @Schema(implementation = CertificationDto.class))),
            @ApiResponse(responseCode = "404", description = "Certification non trouvée", content = @Content),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @PutMapping("/{id}")
    public CertificationDto update(@Parameter(description = "Identifiant de la certification") @PathVariable("id") Long id,
                                   @Valid @RequestBody CertificationDto dto) {
        return service.update(id, dto);
    }

    @Operation(summary = "Supprimer une certification", description = "Supprime une certification existante")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Certification supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Certification non trouvée", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Parameter(description = "Identifiant de la certification") @PathVariable("id") Long id) {
        service.delete(id);
    }

    @Operation(summary = "Réorganiser les certifications", description = "Réordonne les certifications selon la liste d'identifiants fournie")
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
