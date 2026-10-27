package gerard.meli.backend_portfolio.security;

import gerard.meli.backend_portfolio.config.AppProperties;
import gerard.meli.backend_portfolio.domain.AppUser;
import gerard.meli.backend_portfolio.domain.RefreshToken;
import gerard.meli.backend_portfolio.repository.RefreshTokenRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshTokenService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final RefreshTokenRepository repository;
    private final long expirationDays;

    public RefreshTokenService(RefreshTokenRepository repository, AppProperties properties) {
        this.repository = repository;
        this.expirationDays = properties.jwt().refreshExpirationDays();
        if (expirationDays < 1 || expirationDays > 30) {
            throw new IllegalStateException("app.jwt.refresh-expiration-days doit être compris entre 1 et 30.");
        }
    }

    @Transactional
    public String issue(AppUser user) {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setTokenHash(hash(rawToken));
        token.setExpiresAt(Instant.now().plus(expirationDays, ChronoUnit.DAYS));
        repository.save(token);
        return rawToken;
    }

    /** Consomme le jeton courant avant d'en émettre un nouveau, ce qui assure sa rotation. */
    @Transactional
    public AppUser consume(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new BadCredentialsException("Jeton de rafraîchissement invalide");
        }
        String tokenHash = hash(rawToken);
        RefreshToken token = repository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new BadCredentialsException("Jeton de rafraîchissement invalide"));
        Instant now = Instant.now();
        if (repository.revokeIfActive(tokenHash, now) != 1) {
            throw new BadCredentialsException("Jeton de rafraîchissement invalide");
        }
        return token.getUser();
    }

    @Transactional
    public void revoke(String rawToken) {
        if (rawToken != null && !rawToken.isBlank()) {
            repository.revokeIfActive(hash(rawToken), Instant.now());
        }
    }

    private static String hash(String rawToken) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponible", e);
        }
    }
}
