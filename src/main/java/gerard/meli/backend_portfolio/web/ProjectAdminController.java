package gerard.meli.backend_portfolio.web;

import gerard.meli.backend_portfolio.dto.*;
import gerard.meli.backend_portfolio.service.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/projects")
public class ProjectAdminController {

    private final ProjectService service;

    public ProjectAdminController(ProjectService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProjectDto> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public ProjectDto get(@PathVariable("id") Long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDto create(@Valid @RequestBody ProjectDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public ProjectDto update(@PathVariable("id") Long id, @Valid @RequestBody ProjectDto dto) {
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

    // ---------- Points clés d'un projet ----------

    @GetMapping("/{id}/highlights")
    public List<ProjectHighlightDto> listHighlights(@PathVariable("id") Long id) {
        return service.listHighlights(id);
    }

    @PostMapping("/{id}/highlights")
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectHighlightDto addHighlight(@PathVariable("id") Long id, @Valid @RequestBody ProjectHighlightDto dto) {
        return service.addHighlight(id, dto);
    }

    @PutMapping("/{id}/highlights/{childId}")
    public ProjectHighlightDto updateHighlight(@PathVariable("id") Long id, @PathVariable("childId") Long childId,
            @Valid @RequestBody ProjectHighlightDto dto) {
        return service.updateHighlight(id, childId, dto);
    }

    @DeleteMapping("/{id}/highlights/{childId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHighlight(@PathVariable("id") Long id, @PathVariable("childId") Long childId) {
        service.deleteHighlight(id, childId);
    }

    @PatchMapping("/{id}/highlights/reorder")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reorderHighlights(@PathVariable("id") Long id, @Valid @RequestBody ReorderRequest request) {
        service.reorderHighlights(id, request);
    }
}
