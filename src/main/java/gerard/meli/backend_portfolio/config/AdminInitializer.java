package gerard.meli.backend_portfolio.config;

import gerard.meli.backend_portfolio.domain.AppUser;
import gerard.meli.backend_portfolio.repository.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Locale;

/**
 * Crée l'administrateur (ADMIN_EMAIL / ADMIN_PASSWORD) au premier démarrage.
 */
@Component
public class AdminInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);

    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties props;
    public AdminInitializer(AppUserRepository repository, PasswordEncoder passwordEncoder, AppProperties props) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.props = props;
    }

    @Override
    public void run(ApplicationArguments args) {
        AppProperties.Admin admin = props.admin();
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
