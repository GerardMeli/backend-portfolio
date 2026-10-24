package gerard.meli.backend_portfolio.web;

import gerard.meli.backend_portfolio.dto.*;
import gerard.meli.backend_portfolio.service.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/skill-categories")
public class SkillAdminController {

    private final SkillService service;

    public SkillAdminController(SkillService service) {
        this.service = service;
    }

    @GetMapping
    public List<SkillCategoryDto> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public SkillCategoryDto get(@PathVariable("id") Long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SkillCategoryDto create(@Valid @RequestBody SkillCategoryDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public SkillCategoryDto update(@PathVariable("id") Long id, @Valid @RequestBody SkillCategoryDto dto) {
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

    // ---------- Compétences d'une catégorie ----------

    @GetMapping("/{id}/skills")
    public List<SkillDto> listSkills(@PathVariable("id") Long id) {
        return service.listSkills(id);
    }

    @PostMapping("/{id}/skills")
    @ResponseStatus(HttpStatus.CREATED)
    public SkillDto addSkill(@PathVariable("id") Long id, @Valid @RequestBody SkillDto dto) {
        return service.addSkill(id, dto);
    }

    @PutMapping("/{id}/skills/{childId}")
    public SkillDto updateSkill(@PathVariable("id") Long id, @PathVariable("childId") Long childId,
            @Valid @RequestBody SkillDto dto) {
        return service.updateSkill(id, childId, dto);
    }

    @DeleteMapping("/{id}/skills/{childId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSkill(@PathVariable("id") Long id, @PathVariable("childId") Long childId) {
        service.deleteSkill(id, childId);
    }

    @PatchMapping("/{id}/skills/reorder")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reorderSkills(@PathVariable("id") Long id, @Valid @RequestBody ReorderRequest request) {
        service.reorderSkills(id, request);
    }
}
