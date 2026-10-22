package gerard.meli.backend_portfolio.adminapi;

import gerard.meli.backend_portfolio.adminapi.AdminDtos.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminSkillController {

    private final AdminSkillService service;

    // ------------------------------------------------------------------ catégories

    @GetMapping("/skill-categories")
    public List<CategoryResponse> listCategories() {
        return service.listCategories();
    }

    @PostMapping("/skill-categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(@Valid @RequestBody CategoryRequest request) {
        return service.createCategory(request);
    }

    @PutMapping("/skill-categories/order")
    public List<CategoryResponse> reorderCategories(@Valid @RequestBody ReorderRequest request) {
        return service.reorderCategories(request.ids());
    }

    @PutMapping("/skill-categories/{id}")
    public CategoryResponse updateCategory(@PathVariable UUID id, @Valid @RequestBody CategoryRequest request) {
        return service.updateCategory(id, request);
    }

    @DeleteMapping("/skill-categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable UUID id) {
        service.deleteCategory(id);
    }

    // ------------------------------------------------------------------ compétences

    @PostMapping("/skill-categories/{categoryId}/skills")
    @ResponseStatus(HttpStatus.CREATED)
    public SkillResponse addSkill(@PathVariable UUID categoryId, @Valid @RequestBody SkillRequest request) {
        return service.addSkill(categoryId, request);
    }

    @PutMapping("/skill-categories/{categoryId}/skills/order")
    public CategoryResponse reorderSkills(@PathVariable UUID categoryId, @Valid @RequestBody ReorderRequest request) {
        return service.reorderSkills(categoryId, request.ids());
    }

    @PutMapping("/skills/{id}")
    public SkillResponse updateSkill(@PathVariable UUID id, @Valid @RequestBody SkillRequest request) {
        return service.updateSkill(id, request);
    }

    @DeleteMapping("/skills/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSkill(@PathVariable UUID id) {
        service.deleteSkill(id);
    }
}