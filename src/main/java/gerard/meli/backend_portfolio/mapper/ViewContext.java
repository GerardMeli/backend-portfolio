package gerard.meli.backend_portfolio.mapper;

import java.util.*;

/**
 * Contexte d'une requête publique : marché choisi, langue demandée et langue de repli.
 * Sert à choisir la traduction et à décider si un élément est visible pour ce marché.
 */
public record ViewContext(String market, String lang, String fallbackLang) {

    public ViewContext {
        market = market == null ? null : market.toUpperCase(Locale.ROOT);
        lang = lang == null ? null : lang.toLowerCase(Locale.ROOT);
        fallbackLang = fallbackLang == null ? null : fallbackLang.toLowerCase(Locale.ROOT);
    }

    /** Traduction demandée, sinon langue de repli, sinon la première traduction non vide. */
    public String text(Map<String, String> values) {
        if (values == null || values.isEmpty()) return null;
        String value = values.get(lang);
        if (isBlank(value)) value = values.get(fallbackLang);
        if (isBlank(value)) {
            value = values.values().stream().filter(s -> !isBlank(s)).findFirst().orElse(null);
        }
        return value;
    }

    /** Liste de marchés nulle ou vide = visible partout. */
    public boolean allows(List<String> markets) {
        return markets == null || markets.isEmpty() || markets.contains(market);
    }

    public boolean shows(boolean visible, List<String> markets) {
        return visible && allows(markets);
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
