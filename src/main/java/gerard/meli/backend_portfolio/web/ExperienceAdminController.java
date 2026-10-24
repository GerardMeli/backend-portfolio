package gerard.meli.backend_portfolio.web;

import gerard.meli.backend_portfolio.dto.*;
import gerard.meli.backend_portfolio.service.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/experiences")
public class ExperienceAdminController {

    private final ExperienceService service;

    public ExperienceAdminController(ExperienceService service) {
        this.service = service;
    }

    @GetMapping
    public List<ExperienceDto> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public ExperienceDto get(@PathVariable("id") Long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExperienceDto create(@Valid @RequestBody ExperienceDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public ExperienceDto update(@PathVariable("id") Long id, @Valid @RequestBody ExperienceDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        service.delete(id);
    }

    /** Corps : {"ids": [3, 1, 2]} dans le nouvel ordre. */
    @PatchMapping("/reorder")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reorder(@Valid @RequestBody ReorderRequest request) {
        service.reorder(request);
    }

    // ---------- Tâches d'une expérience ----------

    @GetMapping("/{id}/tasks")
    public List<ExperienceTaskDto> listTasks(@PathVariable("id") Long id) {
        return service.listTasks(id);
    }

    @PostMapping("/{id}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public ExperienceTaskDto addTask(@PathVariable("id") Long id, @Valid @RequestBody ExperienceTaskDto dto) {
        return service.addTask(id, dto);
    }

    @PutMapping("/{id}/tasks/{childId}")
    public ExperienceTaskDto updateTask(@PathVariable("id") Long id, @PathVariable("childId") Long childId,
            @Valid @RequestBody ExperienceTaskDto dto) {
        return service.updateTask(id, childId, dto);
    }

    @DeleteMapping("/{id}/tasks/{childId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable("id") Long id, @PathVariable("childId") Long childId) {
        service.deleteTask(id, childId);
    }

    @PatchMapping("/{id}/tasks/reorder")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reorderTasks(@PathVariable("id") Long id, @Valid @RequestBody ReorderRequest request) {
        service.reorderTasks(id, request);
    }
}
