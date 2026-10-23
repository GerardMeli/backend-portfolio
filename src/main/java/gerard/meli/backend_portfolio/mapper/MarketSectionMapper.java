package gerard.meli.backend_portfolio.mapper;

import gerard.meli.backend_portfolio.domain.*;
import gerard.meli.backend_portfolio.dto.*;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class MarketSectionMapper {

    public MarketSectionDto toDto(MarketSection ms) {
        return new MarketSectionDto(ms.getId().getSectionType(), ms.getSortOrder(), ms.isVisible(), ms.getMaxItems());
    }

    public MarketSection toEntity(String marketCode, MarketSectionDto d) {
        MarketSection ms = new MarketSection();
        ms.setId(new MarketSectionId(marketCode.toUpperCase(Locale.ROOT), d.sectionType().trim().toUpperCase(Locale.ROOT)));
        update(d, ms);
        return ms;
    }

    public void update(MarketSectionDto d, MarketSection ms) {
        ms.setSortOrder(d.sortOrder());
        ms.setVisible(d.visible());
        ms.setMaxItems(d.maxItems());
    }
}
