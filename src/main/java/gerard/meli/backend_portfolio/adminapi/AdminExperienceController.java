package gerard.meli.backend_portfolio.adminapi;

import gerard.meli.backend_portfolio.adminapi.AdminDtos.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/experiences")
@RequiredArgsConstructor
public class AdminExperienceController {

    private final AdminExperienceService service;

    @GetMapping
    public List<ExperienceResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public ExperienceResponse get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExperienceResponse create(@Valid @RequestBody ExperienceRequest request) {
        return service.create(request);
    }

    @PutMapping("/order")
    public List<ExperienceResponse> reorder(@Valid @RequestBody ReorderRequest request) {
        return service.reorder(request.ids());
    }

    @PutMapping("/{id}")
    public ExperienceResponse update(@PathVariable UUID id, @Valid @RequestBody ExperienceRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    /** Publier / dépublier sans renvoyer tout le formulaire. */
    @PatchMapping("/{id}/published")
    public ExperienceResponse setPublished(@PathVariable UUID id, @RequestBody PublishRequest request) {
        return service.setPublished(id, request.published());
    }

    // ------------------------------------------------------------------ tâches d'une expérience

    @PostMapping("/{id}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse addTask(@PathVariable UUID id, @Valid @RequestBody TaskRequest request) {
        return service.addTask(id, request);
    }

    @PutMapping("/{id}/tasks/order")
    public ExperienceResponse reorderTasks(@PathVariable UUID id, @Valid @RequestBody ReorderRequest request) {
        return service.reorderTasks(id, request.ids());
    }

    @PutMapping("/{id}/tasks/{taskId}")
    public TaskResponse updateTask(@PathVariable UUID id, @PathVariable UUID taskId,
                                   @Valid @RequestBody TaskRequest request) {
        return service.updateTask(id, taskId, request);
    }

    @DeleteMapping("/{id}/tasks/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable UUID id, @PathVariable UUID taskId) {
        service.deleteTask(id, taskId);
    }
}