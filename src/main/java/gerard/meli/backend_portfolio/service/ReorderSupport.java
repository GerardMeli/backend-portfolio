package gerard.meli.backend_portfolio.service;

import gerard.meli.backend_portfolio.exception.BadRequestException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Réordonnancement : la position de chaque élément devient son index dans la liste d'ids reçue. */
public final class ReorderSupport {

    private ReorderSupport() {
    }

    public static <T> void apply(List<T> current, List<String> ids, Function<T, String> idOf,
                                 BiConsumer<T, Integer> setPosition) {
        Map<String, T> byId = current.stream().collect(Collectors.toMap(idOf, Function.identity()));
        if (ids.size() != byId.size() || !byId.keySet().equals(new HashSet<>(ids))) {
            throw new BadRequestException("La liste d'ids doit contenir exactement tous les éléments, sans doublon.");
        }
        for (int i = 0; i < ids.size(); i++) {
            setPosition.accept(byId.get(ids.get(i)), i);
        }
    }
}
