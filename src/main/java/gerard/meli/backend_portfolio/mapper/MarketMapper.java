package gerard.meli.backend_portfolio.mapper;

import gerard.meli.backend_portfolio.domain.*;
import gerard.meli.backend_portfolio.dto.*;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class MarketMapper {

    public MarketDto toDto(Market m) {
        return new MarketDto(m.getCode(), m.getLabel(), m.getDefaultLocale(), m.getPageFormat(), m.getDateFormat(),
                m.isShowPhoto(), m.isShowPersonalDetails(), m.getMaxPages(), m.getNotes());
    }

    public Market toEntity(MarketDto d) {
        Market m = new Market();
        m.setCode(d.code().trim().toUpperCase(Locale.ROOT));
        update(d, m);
        return m;
    }

    /** Le code (clé primaire) n'est jamais modifié. */
    public void update(MarketDto d, Market m) {
        m.setLabel(d.label());
        m.setDefaultLocale(d.defaultLocale().toLowerCase(Locale.ROOT));
        m.setPageFormat(d.pageFormat());
        m.setDateFormat(d.dateFormat());
        m.setShowPhoto(d.showPhoto());
        m.setShowPersonalDetails(d.showPersonalDetails());
        m.setMaxPages(d.maxPages() != null ? d.maxPages() : Short.valueOf((short) 2));
        m.setNotes(d.notes());
    }

    public MarketInfoView toInfoView(Market m) {
        return new MarketInfoView(m.getCode(), m.getLabel(), m.getDefaultLocale(), m.getPageFormat(),
                m.getDateFormat(), m.isShowPhoto(), m.isShowPersonalDetails(), m.getMaxPages());
    }
}
