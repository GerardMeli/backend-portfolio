package gerard.meli.backend_portfolio.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/** Paramètres applicatifs (préfixe "app" dans application.yml). */
@ConfigurationProperties(prefix = "app")
public record AppProperties(Jwt jwt, Admin admin, Cors cors) {

    /** secret : 32 caractères minimum ; durée des jetons d'accès et de rafraîchissement. */
    public record Jwt(String secret, @DefaultValue("15") long expirationMinutes,
                      @DefaultValue("7") long refreshExpirationDays,
                      @DefaultValue("true") boolean refreshCookieSecure) {
    }

    /** Compte administrateur créé au premier démarrage si aucun n'existe. */
    public record Admin(String email, String password) {
    }

    public record Cors(List<String> allowedOrigins) {
    }
}
