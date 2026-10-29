package gerard.meli.backend_portfolio.repository;

import gerard.meli.backend_portfolio.domain.ProfilePhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;

public interface ProfilePhotoRepository extends JpaRepository<ProfilePhoto, Long> {

    Optional<ProfilePhoto> findFirstByOrderByIdAsc();

    /** Date de la dernière mise à jour, sans charger l'image (sert à versionner l'URL). */
    @Query("select max(p.updatedAt) from ProfilePhoto p")
    Optional<Instant> latestUpdate();
}
