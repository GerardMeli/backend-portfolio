package gerard.meli.backend_portfolio.experience;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExperienceRepository extends JpaRepository<Experience, UUID> {

    /** Côté visiteurs : seulement les expériences publiées. */
    @EntityGraph(attributePaths = {"translations", "tasks", "tasks.translations"})
    List<Experience> findByPublishedTrueOrderByPositionAsc();

    /** Côté admin : tout, brouillons inclus. */
    @EntityGraph(attributePaths = {"translations", "tasks", "tasks.translations"})
    List<Experience> findAllByOrderByPositionAsc();

    @EntityGraph(attributePaths = {"translations", "tasks", "tasks.translations"})
    Optional<Experience> findWithDetailsById(UUID id);

    @Query("select coalesce(max(e.position), -1) + 1 from Experience e")
    int nextPosition();
}