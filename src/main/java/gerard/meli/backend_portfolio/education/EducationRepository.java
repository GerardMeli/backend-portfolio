package gerard.meli.backend_portfolio.education;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface EducationRepository extends JpaRepository<Education, UUID> {

    @EntityGraph(attributePaths = "translations")
    List<Education> findByPublishedTrueOrderByPositionAsc();

    @EntityGraph(attributePaths = "translations")
    List<Education> findAllByOrderByPositionAsc();

    @Query("select coalesce(max(e.position), -1) + 1 from Education e")
    int nextPosition();
}