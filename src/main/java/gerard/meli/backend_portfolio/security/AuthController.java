package gerard.meli.backend_portfolio.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/api/auth/login")
    public TokenService.IssuedToken login(@Valid @RequestBody LoginRequest body, HttpServletRequest request) {
        return authService.login(body.email(), body.password(), request.getRemoteAddr());
    }

    /** Permet à l'admin Angular de vérifier que son token est encore valide. */
    @GetMapping("/api/admin/me")
    public Map<String, String> me(Authentication authentication) {
        return Map.of("email", authentication.getName());
    }

    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {
    }
}