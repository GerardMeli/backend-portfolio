package gerard.meli.backend_portfolio.dto;

import java.time.*;

/** photoUrl, birthDate, nationality et maritalStatus sont nuls si le marché ne les autorise pas. */
public record ProfileView(
        String fullName,
        String headline,
        String summary,
        String email,
        String phone,
        String city,
        String country,
        String photoUrl,
        LocalDate birthDate,
        String nationality,
        String maritalStatus,
        String workAuthorization,
        String linkedinUrl,
        String githubUrl,
        String websiteUrl
) {
}
