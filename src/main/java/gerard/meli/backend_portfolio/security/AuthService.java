package gerard.meli.backend_portfolio.security;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final AdminUserRepository users;
    private final PasswordEncoder encoder;
    private final TokenService tokens;
    private final LoginRateLimiter rateLimiter;
    private final String dummyHash;

    public AuthService(AdminUserRepository users, PasswordEncoder encoder,
                       TokenService tokens, LoginRateLimiter rateLimiter) {
        this.users = users;
        this.encoder = encoder;
        this.tokens = tokens;
        this.rateLimiter = rateLimiter;
        // hash factice : on compare toujours un mot de passe, même si l'email n'existe pas
        // (évite de révéler quels emails existent par le temps de réponse)
        this.dummyHash = encoder.encode(UUID.randomUUID().toString());
    }

    public TokenService.IssuedToken login(String email, String password, String clientIp) {
        String key = clientIp + "|" + email.toLowerCase();
        rateLimiter.check(key);

        Optional<AdminUser> user = users.findByEmailIgnoreCase(email);
        boolean valid = encoder.matches(password, user.map(AdminUser::getPasswordHash).orElse(dummyHash));

        if (user.isEmpty() || !valid) {
            rateLimiter.recordFailure(key);
            throw new BadCredentialsException("Invalid credentials");
        }
        rateLimiter.reset(key);
        return tokens.issue(user.get());
    }
}