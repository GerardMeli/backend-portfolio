package gerard.meli.backend_portfolio.service;

import gerard.meli.backend_portfolio.domain.ProfilePhoto;
import gerard.meli.backend_portfolio.exception.BadRequestException;
import gerard.meli.backend_portfolio.repository.ProfilePhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

/** Photo de profil : validation du format, stockage en base et URL versionnée. */
@Service
@Transactional
@RequiredArgsConstructor
public class ProfilePhotoService {

    private static final long MAX_BYTES = 2L * 1024 * 1024;

    private final ProfilePhotoRepository repository;

    @Transactional(readOnly = true)
    public Optional<ProfilePhoto> find() {
        return repository.findFirstByOrderByIdAsc();
    }

    /** URL publique relative; changer la photo change l'URL et renouvelle le cache navigateur. */
    @Transactional(readOnly = true)
    public Optional<String> publicUrl() {
        return repository.latestUpdate().map(updated -> "/api/public/photo?v=" + updated.toEpochMilli());
    }

    public String save(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Aucun fichier reçu.");
        }
        if (file.getSize() > MAX_BYTES) {
            throw new BadRequestException("Image trop lourde : 2 Mo maximum.");
        }

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new BadRequestException("Lecture du fichier impossible.");
        }
        String contentType = detectType(bytes);
        if (contentType == null) {
            throw new BadRequestException("Format non pris en charge : JPEG, PNG ou WebP uniquement.");
        }

        ProfilePhoto photo = repository.findFirstByOrderByIdAsc().orElseGet(ProfilePhoto::new);
        photo.setContentType(contentType);
        photo.setData(bytes);
        repository.saveAndFlush(photo);
        return publicUrl().orElseThrow();
    }

    public void delete() {
        repository.deleteAll();
    }

    private static String detectType(byte[] bytes) {
        if (bytes.length > 3 && (bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xD8
                && (bytes[2] & 0xFF) == 0xFF) {
            return "image/jpeg";
        }
        if (bytes.length > 8 && (bytes[0] & 0xFF) == 0x89 && bytes[1] == 'P' && bytes[2] == 'N'
                && bytes[3] == 'G' && bytes[4] == 0x0D && bytes[5] == 0x0A
                && bytes[6] == 0x1A && bytes[7] == 0x0A) {
            return "image/png";
        }
        if (bytes.length > 12 && bytes[0] == 'R' && bytes[1] == 'I' && bytes[2] == 'F'
                && bytes[3] == 'F' && bytes[8] == 'W' && bytes[9] == 'E'
                && bytes[10] == 'B' && bytes[11] == 'P') {
            return "image/webp";
        }
        return null;
    }
}
