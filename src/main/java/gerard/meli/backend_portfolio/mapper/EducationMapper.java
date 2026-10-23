package gerard.meli.backend_portfolio.mapper;

import gerard.meli.backend_portfolio.domain.*;
import gerard.meli.backend_portfolio.dto.*;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class EducationMapper {

    public EducationDto toDto(Education e) {
        return new EducationDto(e.getId(), e.getInstitution(), e.getLocation(), e.getDegree(), e.getFieldOfStudy(),
                e.getDescription(), e.getStartDate(), e.getEndDate(), e.getMarkets(), e.getSortOrder(), e.isVisible());
    }

    public Education toEntity(EducationDto d) {
        Education e = new Education();
        update(d, e);
        return e;
    }

    public void update(EducationDto d, Education e) {
        e.setInstitution(d.institution());
        e.setLocation(d.location());
        e.setDegree(MapperUtils.copy(d.degree()));
        e.setFieldOfStudy(MapperUtils.copy(d.fieldOfStudy()));
        e.setDescription(MapperUtils.copy(d.description()));
        e.setStartDate(d.startDate());
        e.setEndDate(d.endDate());
        e.setMarkets(MapperUtils.markets(d.markets()));
        if (d.sortOrder() != null) e.setSortOrder(d.sortOrder());
        e.setVisible(MapperUtils.orElse(d.visible(), e.isVisible()));
    }

    public EducationView toView(Education e, ViewContext ctx) {
        return new EducationView(e.getInstitution(), e.getLocation(), ctx.text(e.getDegree()),
                ctx.text(e.getFieldOfStudy()), ctx.text(e.getDescription()), e.getStartDate(), e.getEndDate());
    }
}
