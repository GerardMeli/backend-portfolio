package gerard.meli.backend_portfolio.config;

import gerard.meli.backend_portfolio.domain.AppUser;
import gerard.meli.backend_portfolio.repository.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Locale;

/**
 * Au démarrage : en production, refuse les valeurs par défaut non sécurisées ;
 * crée l'administrateur (ADMIN_EMAIL / ADMIN_PASSWORD) s'il n'existe encore aucun compte.
 */
@Component
public class AdminInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);
    private static final String DEFAULT_PASSWORD = "ChangeMe123!";
    private static final String DEFAULT_SECRET_PREFIX = "change-me";

    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties props;
    private final Environment environment;

    public AdminInitializer(AppUserRepository repository, PasswordEncoder passwordEncoder, AppProperties props,
            Environment environment) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.props = props;
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) {
        AppProperties.Admin admin = props.admin();
        boolean production = environment.acceptsProfiles(Profiles.of("prod"));

        if (production) {
            String secret = props.jwt() == null ? "" : props.jwt().secret();
            if (secret == null || secret.startsWith(DEFAULT_SECRET_PREFIX)) {
                throw new IllegalStateException("JWT_SECRET doit être défini avec une valeur secrète en production.");
            }
            if (admin == null || DEFAULT_PASSWORD.equals(admin.password())) {
                throw new IllegalStateException("ADMIN_PASSWORD doit être changé en production.");
            }
            if ("postgres".equals(environment.getProperty("spring.datasource.password"))) {
                throw new IllegalStateException("DB_PASSWORD doit être défini en production (mot de passe par défaut refusé).");
            }
        }

        if (admin == null || isBlank(admin.email()) || isBlank(admin.password())) {
            log.warn("ADMIN_EMAIL / ADMIN_PASSWORD absents : aucun administrateur créé.");
            return;
        }
        if (repository.count() > 0) {
            return;   // un compte existe déjà
        }

        AppUser user = new AppUser();
        user.setEmail(admin.email().trim().toLowerCase(Locale.ROOT));
        user.setPasswordHash(passwordEncoder.encode(admin.password()));
        repository.save(user);
        log.info("Administrateur créé : {}", user.getEmail());
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
