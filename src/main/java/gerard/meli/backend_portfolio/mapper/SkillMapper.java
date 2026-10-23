package gerard.meli.backend_portfolio.mapper;

import gerard.meli.backend_portfolio.domain.*;
import gerard.meli.backend_portfolio.dto.*;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class SkillMapper {

    public SkillDto toDto(Skill s) {
        return new SkillDto(s.getId(), s.getCategory() == null ? null : s.getCategory().getId(), s.getName(),
                s.getLevel(), s.getMarkets(), s.getSortOrder(), s.isVisible());
    }

    /** Le rattachement à la catégorie se fait via SkillCategory.addSkill(...). */
    public Skill toEntity(SkillDto d) {
        Skill s = new Skill();
        update(d, s);
        return s;
    }

    public void update(SkillDto d, Skill s) {
        s.setName(d.name());
        s.setLevel(d.level());
        s.setMarkets(MapperUtils.markets(d.markets()));
        if (d.sortOrder() != null) s.setSortOrder(d.sortOrder());
        s.setVisible(MapperUtils.orElse(d.visible(), s.isVisible()));
    }

    public SkillView toView(Skill s) {
        return new SkillView(s.getName(), s.getLevel());
    }
}
