package gerard.meli.backend_portfolio.adminapi;

import gerard.meli.backend_portfolio.adminapi.AdminDtos.*;
import gerard.meli.backend_portfolio.experience.Experience;
import gerard.meli.backend_portfolio.experience.ExperienceRepository;
import gerard.meli.backend_portfolio.experience.ExperienceTask;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminExperienceService {

    private final ExperienceRepository experiences;
    private final AdminSupport support;

    // ------------------------------------------------------------------ expériences

    @Transactional(readOnly = true)
    public List<ExperienceResponse> list() {
        return experiences.findAllByOrderByPositionAsc().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ExperienceResponse get(UUID id) {
        return toResponse(experience(id));
    }

    public ExperienceResponse create(ExperienceRequest r) {
        Experience e = new Experience();
        e.setPosition(experiences.nextPosition());
        apply(e, r);
        return toResponse(experiences.saveAndFlush(e));
    }

    public ExperienceResponse update(UUID id, ExperienceRequest r) {
        Experience e = experience(id);
        apply(e, r);
        return toResponse(e);
    }

    /** Supprime aussi toutes les tâches de l'expérience. */
    public void delete(UUID id) {
        experiences.delete(experience(id));
    }

    public ExperienceResponse setPublished(UUID id, boolean published) {
        Experience e = experience(id);
        e.setPublished(published);
        return toResponse(e);
    }

    public List<ExperienceResponse> reorder(List<UUID> ids) {
        support.applyOrder(ids, experiences.findAll(), Experience::setPosition);
        return list();
    }

    // ------------------------------------------------------------------ tâches

    public TaskResponse addTask(UUID experienceId, TaskRequest r) {
        Experience e = experience(experienceId);
        ExperienceTask task = new ExperienceTask();
        task.setPosition(e.getTasks().stream().mapToInt(ExperienceTask::getPosition).max().orElse(-1) + 1);
        applyTask(task, r);
        e.addTask(task);
        experiences.saveAndFlush(e); // flush : l'id de la nouvelle tâche est attribué
        return toTask(task);
    }

    public TaskResponse updateTask(UUID experienceId, UUID taskId, TaskRequest r) {
        ExperienceTask task = task(experience(experienceId), taskId);
        applyTask(task, r);
        return toTask(task);
    }

    public void deleteTask(UUID experienceId, UUID taskId) {
        Experience e = experience(experienceId);
        e.getTasks().remove(task(e, taskId)); // orphanRemoval supprime la ligne
    }

    public ExperienceResponse reorderTasks(UUID experienceId, List<UUID> ids) {
        Experience e = experience(experienceId);
        support.applyOrder(ids, e.getTasks(), ExperienceTask::setPosition);
        return toResponse(e);
    }

    // ------------------------------------------------------------------ interne

    private Experience experience(UUID id) {
        return experiences.findWithDetailsById(id).orElseThrow(() -> support.notFound("Experience"));
    }

    private ExperienceTask task(Experience e, UUID taskId) {
        return e.getTasks().stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> support.notFound("Task"));
    }

    private void apply(Experience e, ExperienceRequest r) {
        support.checkDates(r.startDate(), r.endDate());
        e.setCompany(r.company().trim());
        e.setCompanyUrl(r.companyUrl());
        e.setLocation(r.location());
        e.setEmploymentType(r.employmentType());
        e.setStartDate(r.startDate());
        e.setEndDate(r.endDate());
        e.setPublished(support.flag(r.published(), false));
        support.syncTranslations(e.getTranslations(), r.translations(), t -> {
            Experience.Text text = new Experience.Text();
            text.setRoleTitle(t.roleTitle());
            text.setSummary(t.summary());
            return text;
        });
    }

    private void applyTask(ExperienceTask task, TaskRequest r) {
        support.syncTranslations(task.getTranslations(), r.translations(), t -> {
            ExperienceTask.Text text = new ExperienceTask.Text();
            text.setContent(t.content());
            return text;
        });
    }

    private ExperienceResponse toResponse(Experience e) {
        return new ExperienceResponse(e.getId(), e.getCompany(), e.getCompanyUrl(), e.getLocation(),
                e.getEmploymentType(), e.getStartDate(), e.getEndDate(), e.getPosition(), e.isPublished(),
                AdminSupport.mapValues(e.getTranslations(), t -> new ExperienceText(t.getRoleTitle(), t.getSummary())),
                e.getTasks().stream()
                        .sorted(Comparator.comparingInt(ExperienceTask::getPosition))
                        .map(this::toTask)
                        .toList());
    }

    private TaskResponse toTask(ExperienceTask t) {
        return new TaskResponse(t.getId(), t.getPosition(),
                AdminSupport.mapValues(t.getTranslations(), x -> new TaskText(x.getContent())));
    }
}
