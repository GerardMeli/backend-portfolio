package gerard.meli.backend_portfolio.adminapi;

import gerard.meli.backend_portfolio.adminapi.AdminDtos.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/education")
@RequiredArgsConstructor
public class AdminEducationController {

    private final AdminEducationService service;

    @GetMapping
    public List<EducationResponse> list() {
        return service.list();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EducationResponse create(@Valid @RequestBody EducationRequest request) {
        return service.create(request);
    }

    @PutMapping("/order")
    public List<EducationResponse> reorder(@Valid @RequestBody ReorderRequest request) {
        return service.reorder(request.ids());
    }

    @PutMapping("/{id}")
    public EducationResponse update(@PathVariable UUID id, @Valid @RequestBody EducationRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    @PatchMapping("/{id}/published")
    public EducationResponse setPublished(@PathVariable UUID id, @RequestBody PublishRequest request) {
        return service.setPublished(id, request.published());
    }
}