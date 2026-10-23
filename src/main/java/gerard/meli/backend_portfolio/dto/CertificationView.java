package gerard.meli.backend_portfolio.dto;

import java.time.*;

public record CertificationView(
        String name,
        String issuer,
        LocalDate issuedOn,
        LocalDate expiresOn,
        String credentialUrl
) {
}
