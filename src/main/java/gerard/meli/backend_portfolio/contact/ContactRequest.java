package gerard.meli.backend_portfolio.contact;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * "website" est un champ piège (honeypot) : caché dans le formulaire Angular,
 * jamais rempli par un humain. S'il est rempli, le message est ignoré.
 */
public record ContactRequest(
        @NotBlank @Size(max = 150) String name,
        @NotBlank @Email @Size(max = 255) String email,
        @Size(max = 200) String subject,
        @NotBlank @Size(min = 10, max = 5000) String message,
        String lang,
        String market,
        String website) {
}