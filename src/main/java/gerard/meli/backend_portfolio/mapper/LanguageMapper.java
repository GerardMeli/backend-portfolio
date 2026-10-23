package gerard.meli.backend_portfolio.mapper;

import gerard.meli.backend_portfolio.domain.*;
import gerard.meli.backend_portfolio.dto.*;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class LanguageMapper {

    public LanguageDto toDto(SpokenLanguage l) {
        return new LanguageDto(l.getId(), l.getName(), l.getLevel(), l.getSortOrder(), l.isVisible());
    }

    public SpokenLanguage toEntity(LanguageDto d) {
        SpokenLanguage l = new SpokenLanguage();
        update(d, l);
        return l;
    }

    public void update(LanguageDto d, SpokenLanguage l) {
        l.setName(MapperUtils.copy(d.name()));
        l.setLevel(d.level());
        if (d.sortOrder() != null) l.setSortOrder(d.sortOrder());
        l.setVisible(MapperUtils.orElse(d.visible(), l.isVisible()));
    }

    public LanguageView toView(SpokenLanguage l, ViewContext ctx) {
        return new LanguageView(ctx.text(l.getName()), l.getLevel());
    }
}
