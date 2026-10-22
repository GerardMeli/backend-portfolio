package gerard.meli.backend_portfolio.contact;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** 5 messages par heure et par IP. En mémoire (remis à zéro au redémarrage). */
@Component
public class ContactRateLimiter {

    private static final int MAX_PER_WINDOW = 5;
    private static final Duration WINDOW = Duration.ofHours(1);

    private final Map<String, Deque<Instant>> hits = new ConcurrentHashMap<>();

    public boolean tryAcquire(String key) {
        Instant now = Instant.now();
        Instant limit = now.minus(WINDOW);
        purge(limit);

        Deque<Instant> queue = hits.computeIfAbsent(key, k -> new ArrayDeque<>());
        synchronized (queue) {
            while (!queue.isEmpty() && queue.peekFirst().isBefore(limit)) {
                queue.pollFirst();
            }
            if (queue.size() >= MAX_PER_WINDOW) {
                return false;
            }
            queue.addLast(now);
            return true;
        }
    }

    private void purge(Instant limit) {
        if (hits.size() > 1000) {
            hits.values().removeIf(q -> {
                synchronized (q) {
                    return q.isEmpty() || q.peekLast().isBefore(limit);
                }
            });
        }
    }
}