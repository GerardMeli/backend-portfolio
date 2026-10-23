package gerard.meli.backend_portfolio.mapper;

import gerard.meli.backend_portfolio.domain.*;
import gerard.meli.backend_portfolio.dto.*;
import gerard.meli.backend_portfolio.enums.ProjectKind;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class ProjectMapper {

    private final ProjectHighlightMapper highlightMapper;

    public ProjectMapper(ProjectHighlightMapper highlightMapper) {
        this.highlightMapper = highlightMapper;
    }

    public ProjectDto toDto(Project p) {
        List<ProjectHighlightDto> highlights = p.getHighlights().stream().map(highlightMapper::toDto).toList();
        return new ProjectDto(p.getId(), p.getName(), p.getKind(), p.getDescription(), p.getTechStack(),
                p.getDemoUrl(), p.getRepoUrl(), p.getStartDate(), p.getEndDate(), p.getMarkets(),
                p.getSortOrder(), p.isVisible(), highlights);
    }

    /** Création : crée aussi les points clés fournis dans le DTO. */
    public Project toEntity(ProjectDto d) {
        Project p = new Project();
        update(d, p);
        if (d.highlights() != null) {
            d.highlights().forEach(h -> p.addHighlight(highlightMapper.toEntity(h)));
        }
        return p;
    }

    /** Mise à jour : ne touche pas aux points clés. */
    public void update(ProjectDto d, Project p) {
        p.setName(d.name());
        p.setKind(d.kind() != null ? d.kind() : ProjectKind.PERSONAL);
        p.setDescription(MapperUtils.copy(d.description()));
        p.setTechStack(MapperUtils.copy(d.techStack()));
        p.setDemoUrl(d.demoUrl());
        p.setRepoUrl(d.repoUrl());
        p.setStartDate(d.startDate());
        p.setEndDate(d.endDate());
        p.setMarkets(MapperUtils.markets(d.markets()));
        if (d.sortOrder() != null) p.setSortOrder(d.sortOrder());
        p.setVisible(MapperUtils.orElse(d.visible(), p.isVisible()));
    }

    public ProjectView toView(Project p, ViewContext ctx) {
        List<String> highlights = p.getHighlights().stream()
                .filter(h -> h.isVisible())
                .sorted(Comparator.comparing(ProjectHighlight::getSortOrder))
                .map(h -> ctx.text(h.getContent()))
                .filter(Objects::nonNull)
                .toList();
        return new ProjectView(p.getName(), p.getKind(), ctx.text(p.getDescription()),
                p.getTechStack() == null ? List.of() : p.getTechStack(), p.getDemoUrl(), p.getRepoUrl(),
                p.getStartDate(), p.getEndDate(), highlights);
    }
}
