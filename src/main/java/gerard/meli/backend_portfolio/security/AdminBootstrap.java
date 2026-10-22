package gerard.meli.backend_portfolio.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Crée le premier compte admin à partir de ADMIN_EMAIL / ADMIN_PASSWORD, s'il n'existe aucun admin. */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminBootstrap implements ApplicationRunner {

    private final AdminUserRepository users;
    private final PasswordEncoder encoder;
    private final SecurityProperties props;

    @Override
    public void run(ApplicationArguments args) {
        SecurityProperties.Bootstrap cfg = props.bootstrap();
        if (cfg == null || cfg.email().isBlank() || cfg.password().isBlank() || users.count() > 0) {
            return;
        }
        if (cfg.password().length() < 12) {
            throw new IllegalStateException("ADMIN_PASSWORD must be at least 12 characters long");
        }
        AdminUser admin = new AdminUser();
        admin.setEmail(cfg.email().trim().toLowerCase());
        admin.setPasswordHash(encoder.encode(cfg.password()));
        users.save(admin);
        log.info("Initial admin account created for {}", admin.getEmail());
    }
}