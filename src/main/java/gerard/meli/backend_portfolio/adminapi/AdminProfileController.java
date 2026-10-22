package gerard.meli.backend_portfolio.adminapi;

import gerard.meli.backend_portfolio.adminapi.AdminDtos.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/** Toutes les routes /api/admin/** exigent un JWT avec le rôle ADMIN (voir SecurityConfig). */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminProfileController {

    private final AdminProfileService service;

    @GetMapping("/profile")
    public ProfileResponse get() {
        return service.get();
    }

    /** Crée ou met à jour le profil unique. */
    @PutMapping("/profile")
    public ProfileResponse save(@Valid @RequestBody ProfileRequest request) {
        return service.save(request);
    }

    @GetMapping("/profile-variants")
    public List<VariantResponse> listVariants() {
        return service.listVariants();
    }

    @PostMapping("/profile-variants")
    @ResponseStatus(HttpStatus.CREATED)
    public VariantResponse createVariant(@Valid @RequestBody VariantRequest request) {
        return service.createVariant(request);
    }

    @PutMapping("/profile-variants/{id}")
    public VariantResponse updateVariant(@PathVariable UUID id, @Valid @RequestBody VariantRequest request) {
        return service.updateVariant(id, request);
    }

    @DeleteMapping("/profile-variants/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVariant(@PathVariable UUID id) {
        service.deleteVariant(id);
    }
}