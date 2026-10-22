package gerard.meli.backend_portfolio.skill;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SkillCategoryRepository extends JpaRepository<SkillCategory, UUID> {

    @EntityGraph(attributePaths = {"translations", "skills"})
    List<SkillCategory> findAllByOrderByPositionAsc();

    @EntityGraph(attributePaths = {"translations", "skills"})
    Optional<SkillCategory> findWithDetailsById(UUID id);

    @Query("select coalesce(max(c.position), -1) + 1 from SkillCategory c")
    int nextPosition();
}