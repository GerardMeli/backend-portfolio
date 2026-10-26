package gerard.meli.backend_portfolio.web;

import gerard.meli.backend_portfolio.dto.LoginRequest;
import gerard.meli.backend_portfolio.dto.TokenResponse;
import gerard.meli.backend_portfolio.security.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentification", description = "Authentification administrateur pour obtenir un jeton JWT")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "Connexion administrateur",
            description = "Authentifie un administrateur et retourne un jeton JWT à envoyer dans l'en-tête 'Authorization: Bearer ...'"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentification réussie - jeton JWT retourné",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "Identifiants invalides", content = @Content),
            @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content)
    })
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
