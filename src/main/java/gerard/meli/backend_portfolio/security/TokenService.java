package gerard.meli.backend_portfolio.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtEncoder encoder;
    private final SecurityProperties props;

    public IssuedToken issue(AdminUser user) {
        Instant now = Instant.now();
        Duration ttl = Duration.ofMinutes(props.jwt().expirationMinutes());
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("backend-portfolio")
                .issuedAt(now)
                .expiresAt(now.plus(ttl))
                .subject(user.getEmail())
                .claim("roles", List.of("ADMIN"))
                .build();
        String token = encoder
                .encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
                .getTokenValue();
        return new IssuedToken(token, ttl.toSeconds());
    }

    public record IssuedToken(String accessToken, long expiresInSeconds) {
    }
}