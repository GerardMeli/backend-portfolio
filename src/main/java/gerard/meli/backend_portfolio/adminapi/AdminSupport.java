package gerard.meli.backend_portfolio.adminapi;

import gerard.meli.backend_portfolio.common.BaseEntity;
import gerard.meli.backend_portfolio.common.LocaleSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;
import java.util.stream.Collectors;

/** Outils communs aux services admin. */
@Component
@RequiredArgsConstructor
public class AdminSupport {

    private final LocaleSupport locales;

    public ResponseStatusException notFound(String what) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, what + " not found");
    }

    public ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    public ResponseStatusException conflict(String message) {
        return new ResponseStatusException(HttpStatus.CONFLICT, message);
    }

    /** Aligne les traductions de l'entité sur celles reçues : ajoute, met à jour et supprime les langues retirées. */
    public <S, T> void syncTranslations(Map<String, T> target, Map<String, S> source, Function<S, T> mapper) {
        for (String locale : source.keySet()) {
            if (!locales.isSupported(locale)) {
                throw badRequest("Unsupported locale: " + locale);
            }
        }
        target.keySet().retainAll(source.keySet());
        source.forEach((locale, dto) -> target.put(locale, mapper.apply(dto)));
    }

    /** Convertit une Map de traductions entité -> DTO (triée par langue pour une réponse stable). */
    public static <S, T> Map<String, T> mapValues(Map<String, S> source, Function<S, T> mapper) {
        Map<String, T> out = new TreeMap<>();
        source.forEach((locale, value) -> out.put(locale, mapper.apply(value)));
        return out;
    }

    public void checkDates(LocalDate start, LocalDate end) {
        if (start != null && end != null && end.isBefore(start)) {
            throw badRequest("endDate must not be before startDate");
        }
    }

    public boolean flag(Boolean value, boolean defaultValue) {
        return value == null ? defaultValue : value;
    }

    /** La liste d'ids doit contenir chaque élément exactement une fois ; la position = l'index dans la liste. */
    public <T extends BaseEntity> void applyOrder(List<UUID> ids, Collection<T> items, ObjIntConsumer<T> setter) {
        Set<UUID> requested = new HashSet<>(ids);
        Map<UUID, T> byId = items.stream().collect(Collectors.toMap(BaseEntity::getId, Function.identity()));
        if (requested.size() != ids.size() || !requested.equals(byId.keySet())) {
            throw badRequest("ids must list every item exactly once");
        }
        for (int i = 0; i < ids.size(); i++) {
            setter.accept(byId.get(ids.get(i)), i);
        }
    }
}