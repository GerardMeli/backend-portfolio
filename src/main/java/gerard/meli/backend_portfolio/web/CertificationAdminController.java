package gerard.meli.backend_portfolio.web;

import gerard.meli.backend_portfolio.dto.*;
import gerard.meli.backend_portfolio.service.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/certifications")
public class CertificationAdminController {

    private final CertificationService service;

    public CertificationAdminController(CertificationService service) {
        this.service = service;
    }

    @GetMapping
    public List<CertificationDto> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public CertificationDto get(@PathVariable("id") Long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CertificationDto create(@Valid @RequestBody CertificationDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public CertificationDto update(@PathVariable("id") Long id, @Valid @RequestBody CertificationDto dto) {
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
