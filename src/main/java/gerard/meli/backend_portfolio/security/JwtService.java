package gerard.meli.backend_portfolio.security;

import gerard.meli.backend_portfolio.config.AppProperties;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/** Création et vérification des jetons JWT (HS256). */
@Service
public class JwtService {

    private static final String ISSUER = "backend_portfolio";

    private final SecretKey key;
    private final JwtParser parser;
    private final long expirationMinutes;

    public JwtService(AppProperties props) {
        String secret = props.jwt() == null ? null : props.jwt().secret();
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("app.jwt.secret doit contenir au moins 32 caractères (variable JWT_SECRET).");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.parser = Jwts.parser().verifyWith(key).requireIssuer(ISSUER).build();
        this.expirationMinutes = props.jwt().expirationMinutes();
    }

    public String generateToken(String subject) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(ISSUER)
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(Duration.ofMinutes(expirationMinutes))))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    /** Sujet du jeton s'il est valide (signature, émetteur, expiration), sinon vide. */
    public Optional<String> subjectOf(String token) {
        try {
            return Optional.ofNullable(parser.parseSignedClaims(token).getPayload().getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public long expirationMinutes() {
        return expirationMinutes;
    }
}
