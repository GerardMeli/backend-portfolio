package gerard.meli.backend_portfolio.mapper;

import gerard.meli.backend_portfolio.domain.*;
import gerard.meli.backend_portfolio.dto.*;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class CustomSectionMapper {

    private final CustomItemMapper itemMapper;

    public CustomSectionMapper(CustomItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    public CustomSectionDto toDto(CustomSection s) {
        List<CustomItemDto> items = s.getItems().stream().map(itemMapper::toDto).toList();
        return new CustomSectionDto(
                s.getId(),
                s.getCode(),
                s.getTitle(),
                s.getMarkets(),
                s.getSortOrder(),
                s.isVisible(),
                items
        );
    }

    /** Création : crée aussi les éléments fournis dans le DTO. */
    public CustomSection toEntity(CustomSectionDto d) {
        CustomSection s = new CustomSection();
        s.setCode(d.code().trim().toUpperCase(Locale.ROOT));
        update(d, s);
        if (d.items() != null) {
            d.items().forEach(i -> s.addItem(itemMapper.toEntity(i)));
        }
        return s;
    }

    /** Mise à jour : le code n'est jamais modifié (il est référencé par market_section) ; les éléments non plus. */
    public void update(CustomSectionDto d, CustomSection s) {
        s.setTitle(MapperUtils.copy(d.title()));
        s.setMarkets(MapperUtils.markets(d.markets()));
        if (d.sortOrder() != null) s.setSortOrder(d.sortOrder());
        s.setVisible(MapperUtils.orElse(d.visible(), s.isVisible()));
    }

    public List<CustomItemView> toItemViews(CustomSection s, ViewContext ctx) {
        return s.getItems().stream()
                .filter(i -> ctx.shows(i.isVisible(), i.getMarkets()))
                .sorted(Comparator.comparing(CustomItem::getSortOrder))
                .map(i -> itemMapper.toView(i, ctx))
                .toList();
    }
}
