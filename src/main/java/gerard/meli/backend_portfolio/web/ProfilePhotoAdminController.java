package gerard.meli.backend_portfolio.web;

import gerard.meli.backend_portfolio.dto.PhotoResponse;
import gerard.meli.backend_portfolio.service.ProfilePhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/** Gestion de la photo de profil réservée à l'administrateur. */
@RestController
@RequestMapping("/api/admin/profile/photo")
@RequiredArgsConstructor
public class ProfilePhotoAdminController {

    private final ProfilePhotoService service;

    @GetMapping
    public PhotoResponse get() {
        return new PhotoResponse(service.publicUrl().orElse(null));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PhotoResponse upload(@RequestParam("file") MultipartFile file) {
        return new PhotoResponse(service.save(file));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete() {
        service.delete();
    }
}
