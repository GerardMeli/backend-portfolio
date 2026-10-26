package gerard.meli.backend_portfolio.web;

import gerard.meli.backend_portfolio.dto.*;
import gerard.meli.backend_portfolio.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/profile")
@Tag(name = "Administration - Profil", description = "Gestion du profil administrateur")
@SecurityRequirement(name = "bearerAuth")
public class ProfileAdminController {

    private final ProfileService service;

    public ProfileAdminController(ProfileService service) {
        this.service = service;
    }

    @Operation(
            summary = "Récupérer le profil",
            description = "Récupère les informations du profil"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profil récupéré avec succès",
                    content = @Content(schema = @Schema(implementation = ProfileDto.class))),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @GetMapping
    public ProfileDto get() {
        return service.get();
    }

    @Operation(
            summary = "Créer ou mettre à jour le profil",
            description = "Crée le profil au premier appel, le met à jour ensuite"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profil enregistré avec succès",
                    content = @Content(schema = @Schema(implementation = ProfileDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @PutMapping
    public ProfileDto save(@Valid @RequestBody ProfileDto dto) {
        return service.save(dto);
    }
}
