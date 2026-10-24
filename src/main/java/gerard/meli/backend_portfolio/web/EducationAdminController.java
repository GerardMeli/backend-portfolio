package gerard.meli.backend_portfolio.web;

import gerard.meli.backend_portfolio.dto.*;
import gerard.meli.backend_portfolio.service.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/educations")
public class EducationAdminController {

    private final EducationService service;

    public EducationAdminController(EducationService service) {
        this.service = service;
    }

    @GetMapping
    public List<EducationDto> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public EducationDto get(@PathVariable("id") Long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EducationDto create(@Valid @RequestBody EducationDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public EducationDto update(@PathVariable("id") Long id, @Valid @RequestBody EducationDto dto) {
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
}
