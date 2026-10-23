package gerard.meli.backend_portfolio.mapper;

import gerard.meli.backend_portfolio.domain.*;
import gerard.meli.backend_portfolio.dto.*;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class CustomItemMapper {

    public CustomItemDto toDto(CustomItem i) {
        return new CustomItemDto(
                i.getId(),
                i.getTitle(),
                i.getSubtitle(),
                i.getDescription(),
                i.getStartDate(),
                i.getEndDate(),
                i.getUrl(),
                i.getMarkets(),
                i.getSortOrder(),
                i.isVisible());
    }

    /** Le rattachement à la section se fait via CustomSection.addItem(...). */
    public CustomItem toEntity(CustomItemDto d) {
        CustomItem i = new CustomItem();
        update(d, i);
        return i;
    }

    public void update(CustomItemDto d, CustomItem i) {
        i.setTitle(MapperUtils.copy(d.title()));
        i.setSubtitle(d.subtitle());
        i.setDescription(MapperUtils.copy(d.description()));
        i.setStartDate(d.startDate());
        i.setEndDate(d.endDate());
        i.setUrl(d.url());
        i.setMarkets(MapperUtils.markets(d.markets()));
        if (d.sortOrder() != null) i.setSortOrder(d.sortOrder());
        i.setVisible(MapperUtils.orElse(d.visible(), i.isVisible()));
    }

    public CustomItemView toView(CustomItem i, ViewContext ctx) {
        return new CustomItemView(
                ctx.text(i.getTitle()),
                i.getSubtitle(),
                ctx.text(i.getDescription()),
                i.getStartDate(),
                i.getEndDate(),
                i.getUrl()
        );
    }
}
