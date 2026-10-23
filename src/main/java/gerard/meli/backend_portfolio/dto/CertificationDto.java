package gerard.meli.backend_portfolio.dto;

import jakarta.validation.constraints.*;
import java.time.*;
import java.util.*;

/** Utilisé en entrée (id nul) et en sortie. Textes traduisibles : {"fr": "...", "en": "..."}. visible nul = true. */
public record CertificationDto(
        Long id,
        @NotEmpty Map<String, String> name,
        @Size(max = 200) String issuer,
        LocalDate issuedOn,
        LocalDate expiresOn,
        @Size(max = 500) String credentialUrl,
        List<String> markets,
        Integer sortOrder,
        Boolean visible
) {
}
