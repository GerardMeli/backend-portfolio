package gerard.meli.backend_portfolio.contact;

import gerard.meli.backend_portfolio.common.LocaleSupport;
import gerard.meli.backend_portfolio.common.Market;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactMessageRepository messages;
    private final ContactRateLimiter rateLimiter;
    private final LocaleSupport locales;

    @Transactional
    public void submit(ContactRequest request, String clientIp) {
        if (request.website() != null && !request.website().isBlank()) {
            return; // robot détecté : on répond "OK" sans rien enregistrer
        }
        if (!rateLimiter.tryAcquire(clientIp)) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many messages, try again later");
        }

        ContactMessage entity = new ContactMessage();
        entity.setName(request.name().trim());
        entity.setEmail(request.email().trim());
        entity.setSubject(request.subject() == null ? null : request.subject().trim());
        entity.setMessage(request.message().trim());
        entity.setLocale(locales.resolve(request.lang(), null));
        entity.setMarket(Market.fromString(request.market()));
        messages.save(entity);
    }
}