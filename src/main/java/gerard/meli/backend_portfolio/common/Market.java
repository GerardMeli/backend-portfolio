package gerard.meli.backend_portfolio.common;

import java.util.Locale;

public enum Market {
    AFRICA, CANADA, EU;

    /** Lecture tolérante (casse ignorée). Retourne null si vide ou inconnu. */
    public static Market fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}