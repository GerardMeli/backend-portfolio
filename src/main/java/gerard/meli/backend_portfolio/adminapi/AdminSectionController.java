package gerard.meli.backend_portfolio.adminapi;

import gerard.meli.backend_portfolio.adminapi.AdminDtos.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/sections")
@RequiredArgsConstructor
public class AdminSectionController {

    private final AdminSectionService service;

    @GetMapping
    public List<SectionResponse> list() {
        return service.list();
    }

    @PutMapping("/{id}")
    public SectionResponse update(@PathVariable UUID id, @Valid @RequestBody SectionRequest request) {
        return service.update(id, request);
    }

    /** Corps : {"ids": [...]} avec toutes les sections dans le nouvel ordre. */
    @PutMapping("/order")
    public List<SectionResponse> reorder(@Valid @RequestBody ReorderRequest request) {
        return service.reorder(request.ids());
    }
}