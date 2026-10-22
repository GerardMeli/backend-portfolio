package gerard.meli.backend_portfolio.project;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    @EntityGraph(attributePaths = {"translations", "skills"})
    List<Project> findByPublishedTrueOrderByPositionAsc();

    @EntityGraph(attributePaths = {"translations", "skills"})
    List<Project> findByPublishedTrueAndFeaturedTrueOrderByPositionAsc();

    @EntityGraph(attributePaths = {"translations", "skills"})
    List<Project> findAllByOrderByPositionAsc();

    @EntityGraph(attributePaths = {"translations", "skills"})
    Optional<Project> findBySlug(String slug);

    @EntityGraph(attributePaths = {"translations", "skills"})
    Optional<Project> findWithDetailsById(UUID id);

    boolean existsBySlug(String slug);

    @Query("select coalesce(max(p.position), -1) + 1 from Project p")
    int nextPosition();
}