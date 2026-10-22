package gerard.meli.backend_portfolio.security;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Limite les échecs de connexion (5 par fenêtre de 15 min, par IP + email).
 * En mémoire : suffisant pour une seule instance (remis à zéro au redémarrage).
 */
@Component
public class LoginRateLimiter {

    private static final int MAX_FAILURES = 5;
    private static final Duration WINDOW = Duration.ofMinutes(15);

    private record Attempts(int failures, Instant windowStart) {
    }

    private final Map<String, Attempts> attempts = new ConcurrentHashMap<>();

    public void check(String key) {
        purgeExpired();
        Attempts a = attempts.get(key);
        if (a != null && a.failures() >= MAX_FAILURES && !expired(a)) {
            throw new TooManyAttemptsException();
        }
    }

    public void recordFailure(String key) {
        attempts.merge(key, new Attempts(1, Instant.now()), (old, ignored) ->
                expired(old)
                        ? new Attempts(1, Instant.now())
                        : new Attempts(old.failures() + 1, old.windowStart()));
    }

    public void reset(String key) {
        attempts.remove(key);
    }

    private boolean expired(Attempts a) {
        return a.windowStart().plus(WINDOW).isBefore(Instant.now());
    }

    private void purgeExpired() {
        if (attempts.size() > 1000) {
            attempts.values().removeIf(this::expired);
        }
    }
}