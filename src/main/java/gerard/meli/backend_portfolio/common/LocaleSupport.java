package gerard.meli.backend_portfolio.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Choix de la langue : paramètre ?lang= > en-tête Accept-Language > langue par défaut.
 * Choix de la traduction : langue demandée > langue par défaut > première disponible.
 */
@Component
public class LocaleSupport {

    private final List<String> supported;
    private final String defaultLocale;

    public LocaleSupport(@Value("${app.locales.supported}") String[] supported,
                         @Value("${app.locales.default}") String defaultLocale) {
        this.supported = Arrays.stream(supported).map(String::trim).toList();
        this.defaultLocale = defaultLocale.trim();
    }

    public String resolve(String lang, String acceptLanguage) {
        String fromParam = normalize(lang);
        if (fromParam != null) {
            return fromParam;
        }
        if (acceptLanguage != null && !acceptLanguage.isBlank()) {
            try {
                for (Locale.LanguageRange range : Locale.LanguageRange.parse(acceptLanguage)) {
                    String candidate = normalize(range.getRange());
                    if (candidate != null) {
                        return candidate;
                    }
                }
            } catch (IllegalArgumentException ignored) {
                // en-tête mal formé : on retombe sur la langue par défaut
            }
        }
        return defaultLocale;
    }

    public <T> Optional<T> pick(Map<String, T> translations, String locale) {
        if (translations == null || translations.isEmpty()) {
            return Optional.empty();
        }
        T value = translations.get(locale);
        if (value == null) {
            value = translations.get(defaultLocale);
        }
        if (value == null) {
            value = translations.values().iterator().next();
        }
        return Optional.ofNullable(value);
    }

    public boolean isSupported(String locale) {
        return locale != null && supported.contains(locale);
    }

    /** "fr-CA" -> "fr" ; retourne null si la langue n'est pas supportée. */
    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String lang = value.trim().toLowerCase(Locale.ROOT).split("[-_]")[0];
        return supported.contains(lang) ? lang : null;
    }
}