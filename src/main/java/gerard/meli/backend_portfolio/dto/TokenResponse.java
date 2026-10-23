package gerard.meli.backend_portfolio.dto;

public record TokenResponse(
        String token,
        long expiresInMinutes
) {
}
