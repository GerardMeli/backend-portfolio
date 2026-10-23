package gerard.meli.backend_portfolio.mapper;

import gerard.meli.backend_portfolio.domain.*;
import gerard.meli.backend_portfolio.dto.*;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class ExperienceMapper {

    private final ExperienceTaskMapper taskMapper;

    public ExperienceMapper(ExperienceTaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    public ExperienceDto toDto(Experience e) {
        List<ExperienceTaskDto> tasks = e.getTasks().stream().map(taskMapper::toDto).toList();
        return new ExperienceDto(e.getId(), e.getCompany(), e.getLocation(), e.getTitle(), e.getDescription(),
                e.getEmploymentType(), e.getStartDate(), e.getEndDate(), e.isCurrent(), e.getMarkets(),
                e.getSortOrder(), e.isVisible(), tasks);
    }

    /** Création : crée aussi les tâches fournies dans le DTO. */
    public Experience toEntity(ExperienceDto d) {
        Experience e = new Experience();
        update(d, e);
        if (d.tasks() != null) {
            d.tasks().forEach(t -> e.addTask(taskMapper.toEntity(t)));
        }
        return e;
    }

    /** Mise à jour : ne touche pas aux tâches (gérées par leurs propres endpoints). */
    public void update(ExperienceDto d, Experience e) {
        e.setCompany(d.company());
        e.setLocation(d.location());
        e.setTitle(MapperUtils.copy(d.title()));
        e.setDescription(MapperUtils.copy(d.description()));
        e.setEmploymentType(d.employmentType());
        e.setStartDate(d.startDate());
        e.setCurrent(d.current());
        e.setEndDate(d.current() ? null : d.endDate());
        e.setMarkets(MapperUtils.markets(d.markets()));
        if (d.sortOrder() != null) e.setSortOrder(d.sortOrder());
        e.setVisible(MapperUtils.orElse(d.visible(), e.isVisible()));
    }

    public ExperienceView toView(Experience e, ViewContext ctx) {
        List<String> tasks = e.getTasks().stream()
                .filter(t -> ctx.shows(t.isVisible(), t.getMarkets()))
                .sorted(Comparator.comparing(ExperienceTask::getSortOrder))
                .map(t -> ctx.text(t.getContent()))
                .filter(Objects::nonNull)
                .toList();
        return new ExperienceView(e.getCompany(), e.getLocation(), ctx.text(e.getTitle()),
                ctx.text(e.getDescription()), e.getEmploymentType(), e.getStartDate(), e.getEndDate(),
                e.isCurrent(), tasks);
    }
}
