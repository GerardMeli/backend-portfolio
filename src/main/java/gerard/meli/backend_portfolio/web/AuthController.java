package gerard.meli.backend_portfolio.web;

import gerard.meli.backend_portfolio.dto.LoginRequest;
import gerard.meli.backend_portfolio.dto.TokenResponse;
import gerard.meli.backend_portfolio.security.AuthService;
import gerard.meli.backend_portfolio.config.AppProperties;
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
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentification", description = "Authentification administrateur pour obtenir un jeton JWT")
public class AuthController {

    private final AuthService authService;
    private final AppProperties properties;

    public AuthController(AuthService authService, AppProperties properties) {
        this.authService = authService;
        this.properties = properties;
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
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest) {
        return response(authService.login(request, servletRequest.getRemoteAddr()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
            @CookieValue(name = "refresh_token", required = false) String refreshToken) {
        return response(authService.refresh(refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refresh_token", required = false) String refreshToken) {
        authService.logout(refreshToken);
        return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, clearRefreshCookie().toString()).build();
    }

    private ResponseEntity<TokenResponse> response(AuthService.Tokens tokens) {
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshCookie(tokens.refreshToken()).toString())
                .body(tokens.accessToken());
    }

    private ResponseCookie refreshCookie(String refreshToken) {
        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(properties.jwt().refreshCookieSecure())
                .sameSite("Strict")
                .path("/api/auth")
                .maxAge(Duration.ofDays(properties.jwt().refreshExpirationDays()))
                .build();
    }

    private ResponseCookie clearRefreshCookie() {
        return ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(properties.jwt().refreshCookieSecure())
                .sameSite("Strict")
                .path("/api/auth")
                .maxAge(Duration.ZERO)
                .build();
    }
}
