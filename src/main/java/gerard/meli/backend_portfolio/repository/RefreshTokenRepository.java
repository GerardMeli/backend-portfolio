package gerard.meli.backend_portfolio.repository;

import gerard.meli.backend_portfolio.domain.RefreshToken;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    @EntityGraph(attributePaths = "user")
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    /** Révocation atomique : un même jeton ne peut être utilisé qu'une fois. */
    @Modifying
    @Query("update RefreshToken t set t.revokedAt = :now where t.tokenHash = :tokenHash "
            + "and t.revokedAt is null and t.expiresAt > :now")
    int revokeIfActive(@Param("tokenHash") String tokenHash, @Param("now") Instant now);
}
