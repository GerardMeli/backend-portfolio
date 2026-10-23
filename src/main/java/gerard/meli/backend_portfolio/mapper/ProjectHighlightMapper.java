package gerard.meli.backend_portfolio.mapper;

import gerard.meli.backend_portfolio.domain.*;
import gerard.meli.backend_portfolio.dto.*;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class ProjectHighlightMapper {

    public ProjectHighlightDto toDto(ProjectHighlight h) {
        return new ProjectHighlightDto(h.getId(), h.getContent(), h.getSortOrder(), h.isVisible());
    }

    /** Le rattachement au projet se fait via Project.addHighlight(...). */
    public ProjectHighlight toEntity(ProjectHighlightDto d) {
        ProjectHighlight h = new ProjectHighlight();
        update(d, h);
        return h;
    }

    public void update(ProjectHighlightDto d, ProjectHighlight h) {
        h.setContent(MapperUtils.copy(d.content()));
        if (d.sortOrder() != null) h.setSortOrder(d.sortOrder());
        h.setVisible(MapperUtils.orElse(d.visible(), h.isVisible()));
    }
}
