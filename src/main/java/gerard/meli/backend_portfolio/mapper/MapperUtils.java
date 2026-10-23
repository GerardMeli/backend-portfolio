package gerard.meli.backend_portfolio.mapper;

import java.util.*;

/** Petits utilitaires partagés par les mappers. */
public final class MapperUtils {

    private MapperUtils() {
    }

    /** Valeur du DTO si fournie, sinon valeur actuelle (nul = ne pas changer). */
    public static boolean orElse(Boolean value, boolean current) {
        return value != null ? value : current;
    }

    public static Map<String, String> copy(Map<String, String> source) {
        return source == null ? null : new LinkedHashMap<>(source);
    }

    public static List<String> copy(List<String> source) {
        return source == null ? null : new ArrayList<>(source);
    }

    /** Codes de marchés en majuscules, sans doublon ; liste vide ou nulle = visible partout (null). */
    public static List<String> markets(List<String> source) {
        if (source == null) return null;
        List<String> cleaned = source.stream()
                .filter(Objects::nonNull)
                .map(s -> s.trim().toUpperCase(Locale.ROOT))
                .filter(s -> !s.isEmpty())
                .distinct()
                .toList();
        return cleaned.isEmpty() ? null : new ArrayList<>(cleaned);
    }
}
