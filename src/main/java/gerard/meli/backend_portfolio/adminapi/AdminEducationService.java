package gerard.meli.backend_portfolio.adminapi;

import gerard.meli.backend_portfolio.adminapi.AdminDtos.*;
import gerard.meli.backend_portfolio.education.Education;
import gerard.meli.backend_portfolio.education.EducationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminEducationService {

    private final EducationRepository educations;
    private final AdminSupport support;

    @Transactional(readOnly = true)
    public List<EducationResponse> list() {
        return educations.findAllByOrderByPositionAsc().stream().map(this::toResponse).toList();
    }

    public EducationResponse create(EducationRequest r) {
        Education e = new Education();
        e.setPosition(educations.nextPosition());
        apply(e, r);
        return toResponse(educations.saveAndFlush(e));
    }

    public EducationResponse update(UUID id, EducationRequest r) {
        Education e = education(id);
        apply(e, r);
        return toResponse(e);
    }

    public void delete(UUID id) {
        educations.delete(education(id));
    }

    public EducationResponse setPublished(UUID id, boolean published) {
        Education e = education(id);
        e.setPublished(published);
        return toResponse(e);
    }

    public List<EducationResponse> reorder(List<UUID> ids) {
        support.applyOrder(ids, educations.findAll(), Education::setPosition);
        return list();
    }

    private Education education(UUID id) {
        return educations.findById(id).orElseThrow(() -> support.notFound("Education"));
    }

    private void apply(Education e, EducationRequest r) {
        support.checkDates(r.startDate(), r.endDate());
        e.setInstitution(r.institution().trim());
        e.setStartDate(r.startDate());
        e.setEndDate(r.endDate());
        e.setPublished(support.flag(r.published(), false));
        support.syncTranslations(e.getTranslations(), r.translations(), t -> {
            Education.Text text = new Education.Text();
            text.setDegree(t.degree());
            text.setDescription(t.description());
            return text;
        });
    }

    private EducationResponse toResponse(Education e) {
        return new EducationResponse(e.getId(), e.getInstitution(), e.getStartDate(), e.getEndDate(),
                e.getPosition(), e.isPublished(),
                AdminSupport.mapValues(e.getTranslations(), t -> new EducationText(t.getDegree(), t.getDescription())));
    }
}