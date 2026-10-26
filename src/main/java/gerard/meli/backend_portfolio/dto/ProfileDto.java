package gerard.meli.backend_portfolio.dto;

import jakarta.validation.constraints.*;
import java.time.*;
import java.util.*;

/** Utilisé en entrée (id nul) et en sortie. Textes traduisibles : {"fr": "...", "en": "..."}. visible nul = true. */
public record ProfileDto(
        String id,
        @NotBlank @Size(max = 150) String fullName,
        Map<String, String> headline,
        Map<String, String> summary,
        @Email @Size(max = 255) String email,
        @Size(max = 50) String phone,
        @Size(max = 100) String city,
        @Size(max = 100) String country,
        @Size(max = 500) String photoUrl,
        LocalDate birthDate,
        @Size(max = 100) String nationality,
        @Size(max = 50) String maritalStatus,
        Map<String, String> workAuthorization,
        @Size(max = 500) String linkedinUrl,
        @Size(max = 500) String githubUrl,
        @Size(max = 500) String websiteUrl
) {
}
