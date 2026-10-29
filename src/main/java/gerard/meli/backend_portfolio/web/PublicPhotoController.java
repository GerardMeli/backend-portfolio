package gerard.meli.backend_portfolio.web;

import gerard.meli.backend_portfolio.service.ProfilePhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

/** Sert publiquement la photo du profil. */
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicPhotoController {

    private final ProfilePhotoService service;

    @GetMapping("/photo")
    public ResponseEntity<byte[]> photo() {
        return service.find()
                .map(photo -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(photo.getContentType()))
                        .cacheControl(CacheControl.maxAge(Duration.ofDays(7)).cachePublic())
                        .eTag("\"" + photo.getUpdatedAt().toEpochMilli() + "\"")
                        .body(photo.getData()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
