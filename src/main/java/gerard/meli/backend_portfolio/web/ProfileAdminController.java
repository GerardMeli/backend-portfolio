package gerard.meli.backend_portfolio.web;

import gerard.meli.backend_portfolio.dto.*;
import gerard.meli.backend_portfolio.service.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/profile")
public class ProfileAdminController {

    private final ProfileService service;

    public ProfileAdminController(ProfileService service) {
        this.service = service;
    }

    @GetMapping
    public ProfileDto get() {
        return service.get();
    }

    /** Crée le profil au premier appel, le met à jour ensuite. */
    @PutMapping
    public ProfileDto save(@Valid @RequestBody ProfileDto dto) {
        return service.save(dto);
    }
}
