package gerard.meli.backend_portfolio.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(Jwt jwt, Bootstrap bootstrap) {

    public record Jwt(String secret, @DefaultValue("120") long expirationMinutes) {
    }

    /** Premier compte admin, créé au démarrage s'il n'existe aucun admin. */
    public record Bootstrap(@DefaultValue("") String email, @DefaultValue("") String password) {
    }
}