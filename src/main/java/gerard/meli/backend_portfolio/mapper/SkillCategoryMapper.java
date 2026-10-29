package gerard.meli.backend_portfolio.mapper;

import gerard.meli.backend_portfolio.domain.*;
import gerard.meli.backend_portfolio.dto.*;
import gerard.meli.backend_portfolio.enums.SkillKind;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class SkillCategoryMapper {

    private final SkillMapper skillMapper;

    public SkillCategoryMapper(SkillMapper skillMapper) {
        this.skillMapper = skillMapper;
    }

    public SkillCategoryDto toDto(SkillCategory c) {
        List<SkillDto> skills = c.getSkills().stream().map(skillMapper::toDto).toList();
        return new SkillCategoryDto(c.getId(), c.getName(), c.getKind(), c.getSortOrder(), c.isVisible(), skills);
    }

    /** Création : crée aussi les compétences fournies dans le DTO. */
    public SkillCategory toEntity(SkillCategoryDto d) {
        SkillCategory c = new SkillCategory();
        update(d, c);
        if (d.skills() != null) {
            d.skills().forEach(s -> c.addSkill(skillMapper.toEntity(s)));
        }
        return c;
    }

    /** Mise à jour : ne touche pas aux compétences. */
    public void update(SkillCategoryDto d, SkillCategory c) {
        c.setName(MapperUtils.copy(d.name()));
        c.setKind(d.kind() != null ? d.kind() : SkillKind.PROFESSIONAL);
        if (d.sortOrder() != null) c.setSortOrder(d.sortOrder());
        c.setVisible(MapperUtils.orElse(d.visible(), c.isVisible()));
    }

    /** Le service peut ignorer les groupes dont skills() est vide après filtrage. */
    public SkillGroupView toView(SkillCategory c, ViewContext ctx) {
        List<SkillView> skills = c.getSkills().stream()
                .filter(s -> ctx.shows(s.isVisible(), s.getMarkets()))
                .sorted(Comparator.comparing(Skill::getSortOrder))
                .map(skillMapper::toView)
                .toList();
        return new SkillGroupView(ctx.text(c.getName()), c.getKind(), skills);
    }
}
