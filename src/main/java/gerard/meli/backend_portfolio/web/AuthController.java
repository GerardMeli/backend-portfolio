package gerard.meli.backend_portfolio.web;

import gerard.meli.backend_portfolio.dto.LoginRequest;
import gerard.meli.backend_portfolio.dto.TokenResponse;
import gerard.meli.backend_portfolio.security.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** Retourne un jeton JWT à envoyer ensuite dans l'en-tête "Authorization: Bearer ...". */
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
