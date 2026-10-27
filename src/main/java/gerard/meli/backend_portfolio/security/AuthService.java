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
    private static final int MAX_IP_ATTEMPTS = 20;
    private static final Duration IP_WINDOW = Duration.ofMinutes(15);

    private record Attempt(int failures, Instant lockedUntil) {
    }

    private record IpWindow(Instant startedAt, int attempts) {
    }

    public record Tokens(TokenResponse accessToken, String refreshToken) {
    }

    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final String dummyHash;
    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();
    private final Map<String, IpWindow> ipAttempts = new ConcurrentHashMap<>();

    public AuthService(AppUserRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService,
                       RefreshTokenService refreshTokenService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.dummyHash = passwordEncoder.encode("dummy-password-for-timing");
    }

    public Tokens login(LoginRequest request, String remoteAddress) {
        registerIpAttempt(remoteAddress);
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
        return tokensFor(user.get());
    }

    public Tokens refresh(String refreshToken) {
        return tokensFor(refreshTokenService.consume(refreshToken));
    }

    public void logout(String refreshToken) {
        refreshTokenService.revoke(refreshToken);
    }

    private Tokens tokensFor(AppUser user) {
        return new Tokens(
                new TokenResponse(jwtService.generateToken(user.getEmail()), jwtService.expirationMinutes()),
                refreshTokenService.issue(user));
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

    private void registerIpAttempt(String remoteAddress) {
        String key = remoteAddress == null || remoteAddress.isBlank() ? "unknown" : remoteAddress;
        Instant now = Instant.now();
        if (ipAttempts.size() > 10_000) ipAttempts.clear();
        IpWindow window = ipAttempts.compute(key, (ignored, current) -> {
            if (current == null || !now.isBefore(current.startedAt().plus(IP_WINDOW))) {
                return new IpWindow(now, 1);
            }
            return new IpWindow(current.startedAt(), current.attempts() + 1);
        });
        if (window.attempts() > MAX_IP_ATTEMPTS) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Trop de tentatives. Réessayez dans quelques minutes.");
        }
    }
}
