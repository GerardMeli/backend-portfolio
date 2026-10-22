package gerard.meli.backend_portfolio.adminapi;

import gerard.meli.backend_portfolio.adminapi.AdminDtos.*;
import gerard.meli.backend_portfolio.project.Project;
import gerard.meli.backend_portfolio.project.ProjectRepository;
import gerard.meli.backend_portfolio.skill.Skill;
import gerard.meli.backend_portfolio.skill.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminProjectService {

    private final ProjectRepository projects;
    private final SkillRepository skills;
    private final AdminSupport support;

    @Transactional(readOnly = true)
    public List<ProjectResponse> list() {
        return projects.findAllByOrderByPositionAsc().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse get(UUID id) {
        return toResponse(project(id));
    }

    public ProjectResponse create(ProjectRequest r) {
        if (projects.existsBySlug(r.slug())) {
            throw support.conflict("Slug already used: " + r.slug());
        }
        Project p = new Project();
        p.setPosition(projects.nextPosition());
        apply(p, r);
        return toResponse(projects.saveAndFlush(p));
    }

    public ProjectResponse update(UUID id, ProjectRequest r) {
        Project p = project(id);
        projects.findBySlug(r.slug())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw support.conflict("Slug already used: " + r.slug());
                });
        apply(p, r);
        return toResponse(p);
    }

    public void delete(UUID id) {
        projects.delete(project(id));
    }

    public ProjectResponse setPublished(UUID id, boolean published) {
        Project p = project(id);
        p.setPublished(published);
        return toResponse(p);
    }

    public List<ProjectResponse> reorder(List<UUID> ids) {
        support.applyOrder(ids, projects.findAll(), Project::setPosition);
        return list();
    }

    // ------------------------------------------------------------------ interne

    private Project project(UUID id) {
        return projects.findWithDetailsById(id).orElseThrow(() -> support.notFound("Project"));
    }

    private void apply(Project p, ProjectRequest r) {
        support.checkDates(r.startDate(), r.endDate());
        p.setSlug(r.slug());
        p.setType(r.type());
        p.setRepoUrl(r.repoUrl());
        p.setDemoUrl(r.demoUrl());
        p.setCoverImageUrl(r.coverImageUrl());
        p.setStartDate(r.startDate());
        p.setEndDate(r.endDate());
        p.setFeatured(support.flag(r.featured(), false));
        p.setPublished(support.flag(r.published(), false));
        p.getSkills().clear();
        p.getSkills().addAll(resolveSkills(r.skillIds()));
        support.syncTranslations(p.getTranslations(), r.translations(), t -> {
            Project.Text text = new Project.Text();
            text.setTitle(t.title());
            text.setSummary(t.summary());
            text.setProblem(t.problem());
            text.setSolution(t.solution());
            text.setResults(t.results());
            return text;
        });
    }

    private Set<Skill> resolveSkills(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return Set.of();
        }
        List<Skill> found = skills.findAllById(ids);
        if (found.size() != ids.size()) {
            throw support.badRequest("Unknown skill id in skillIds");
        }
        return new HashSet<>(found);
    }

    private ProjectResponse toResponse(Project p) {
        return new ProjectResponse(p.getId(), p.getSlug(), p.getType(), p.getRepoUrl(), p.getDemoUrl(),
                p.getCoverImageUrl(), p.getStartDate(), p.getEndDate(), p.isFeatured(), p.getPosition(),
                p.isPublished(),
                p.getSkills().stream()
                        .sorted(Comparator.comparing(Skill::getName, String.CASE_INSENSITIVE_ORDER))
                        .map(s -> new SkillRef(s.getId(), s.getName()))
                        .toList(),
                AdminSupport.mapValues(p.getTranslations(), t -> new ProjectText(
                        t.getTitle(), t.getSummary(), t.getProblem(), t.getSolution(), t.getResults())));
    }
}