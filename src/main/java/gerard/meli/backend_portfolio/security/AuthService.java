package gerard.meli.backend_portfolio.security;

import gerard.meli.backend_portfolio.domain.AppUser;
import gerard.meli.backend_portfolio.dto.LoginRequest;
import gerard.meli.backend_portfolio.dto.TokenResponse;
import gerard.meli.backend_portfolio.repository.AppUserRepository;
import gerard.meli.backend_portfolio.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Connexion de l'administrateur, avec blocage temporaire après des échecs répétés. */
@Service
public class AuthService {

    private static final int MAX_FAILURES = 5;
    private static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    private record Attempt(int failures, Instant lockedUntil) {
    }

    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final String dummyHash;
    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();

    public AuthService(AppUserRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.dummyHash = passwordEncoder.encode("dummy-password-for-timing");
    }

    public TokenResponse login(LoginRequest request) {
        String key = request.email().trim().toLowerCase(Locale.ROOT);
        checkNotLocked(key);

        Optional<AppUser> user = repository.findByEmailIgnoreCase(key);
        // On compare toujours un hash pour que le temps de réponse ne révèle pas si l'email existe.
        boolean passwordOk = passwordEncoder.matches(request.password(),
                user.map(AppUser::getPasswordHash).orElse(dummyHash));

        if (user.isEmpty() || !passwordOk) {
            registerFailure(key);
            throw new BadCredentialsException("Identifiants invalides");
        }
        attempts.remove(key);
        return new TokenResponse(jwtService.generateToken(user.get().getEmail()), jwtService.expirationMinutes());
    }

    private void checkNotLocked(String key) {
        Attempt attempt = attempts.get(key);
        if (attempt == null || attempt.lockedUntil() == null) return;
        if (Instant.now().isBefore(attempt.lockedUntil())) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Trop de tentatives. Réessayez dans quelques minutes.");
        }
        attempts.remove(key);
    }

    private void registerFailure(String key) {
        if (attempts.size() > 10_000) attempts.clear();   // borne la mémoire
        attempts.compute(key, (k, attempt) -> {
            int failures = (attempt == null ? 0 : attempt.failures()) + 1;
            return failures >= MAX_FAILURES
                    ? new Attempt(0, Instant.now().plus(LOCK_DURATION))
                    : new Attempt(failures, null);
        });
    }
}
