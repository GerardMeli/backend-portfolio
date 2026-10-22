package gerard.meli.backend_portfolio.adminapi;

import gerard.meli.backend_portfolio.adminapi.AdminDtos.*;
import gerard.meli.backend_portfolio.section.Section;
import gerard.meli.backend_portfolio.section.SectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/** Les sections correspondent à des composants Angular (code fixe) : on modifie leurs titres, visibilité et ordre. */
@Service
@RequiredArgsConstructor
@Transactional
public class AdminSectionService {

    private final SectionRepository sections;
    private final AdminSupport support;

    @Transactional(readOnly = true)
    public List<SectionResponse> list() {
        return sections.findAllByOrderByPositionAsc().stream().map(this::toResponse).toList();
    }

    public SectionResponse update(UUID id, SectionRequest r) {
        Section s = sections.findById(id).orElseThrow(() -> support.notFound("Section"));
        s.setVisible(support.flag(r.visible(), true));
        support.syncTranslations(s.getTranslations(), r.translations(), t -> {
            Section.Text text = new Section.Text();
            text.setTitle(t.title());
            text.setSubtitle(t.subtitle());
            return text;
        });
        return toResponse(s);
    }

    public List<SectionResponse> reorder(List<UUID> ids) {
        support.applyOrder(ids, sections.findAll(), Section::setPosition);
        return list();
    }

    private SectionResponse toResponse(Section s) {
        return new SectionResponse(s.getId(), s.getCode(), s.getPosition(), s.isVisible(),
                AdminSupport.mapValues(s.getTranslations(), t -> new SectionText(t.getTitle(), t.getSubtitle())));
    }
}