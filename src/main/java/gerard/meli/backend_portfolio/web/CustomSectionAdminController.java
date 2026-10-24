package gerard.meli.backend_portfolio.web;

import gerard.meli.backend_portfolio.dto.*;
import gerard.meli.backend_portfolio.service.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/custom-sections")
public class CustomSectionAdminController {

    private final CustomSectionService service;

    public CustomSectionAdminController(CustomSectionService service) {
        this.service = service;
    }

    @GetMapping
    public List<CustomSectionDto> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public CustomSectionDto get(@PathVariable("id") Long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomSectionDto create(@Valid @RequestBody CustomSectionDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public CustomSectionDto update(@PathVariable("id") Long id, @Valid @RequestBody CustomSectionDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        service.delete(id);
    }

    // ---------- Éléments d'une section personnalisée ----------

    @GetMapping("/{id}/items")
    public List<CustomItemDto> listItems(@PathVariable("id") Long id) {
        return service.listItems(id);
    }

    @PostMapping("/{id}/items")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomItemDto addItem(@PathVariable("id") Long id, @Valid @RequestBody CustomItemDto dto) {
        return service.addItem(id, dto);
    }

    @PutMapping("/{id}/items/{childId}")
    public CustomItemDto updateItem(@PathVariable("id") Long id, @PathVariable("childId") Long childId,
            @Valid @RequestBody CustomItemDto dto) {
        return service.updateItem(id, childId, dto);
    }

    @DeleteMapping("/{id}/items/{childId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable("id") Long id, @PathVariable("childId") Long childId) {
        service.deleteItem(id, childId);
    }

    @PatchMapping("/{id}/items/reorder")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reorderItems(@PathVariable("id") Long id, @Valid @RequestBody ReorderRequest request) {
        service.reorderItems(id, request);
    }
}
