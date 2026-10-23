package gerard.meli.backend_portfolio.mapper;

import gerard.meli.backend_portfolio.domain.*;
import gerard.meli.backend_portfolio.dto.*;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class ExperienceTaskMapper {

    public ExperienceTaskDto toDto(ExperienceTask t) {
        return new ExperienceTaskDto(t.getId(), t.getContent(), t.getMarkets(), t.getSortOrder(), t.isVisible());
    }

    /** Le rattachement à l'expérience se fait via Experience.addTask(...). */
    public ExperienceTask toEntity(ExperienceTaskDto d) {
        ExperienceTask t = new ExperienceTask();
        update(d, t);
        return t;
    }

    public void update(ExperienceTaskDto d, ExperienceTask t) {
        t.setContent(MapperUtils.copy(d.content()));
        t.setMarkets(MapperUtils.markets(d.markets()));
        if (d.sortOrder() != null) t.setSortOrder(d.sortOrder());
        t.setVisible(MapperUtils.orElse(d.visible(), t.isVisible()));
    }
}
