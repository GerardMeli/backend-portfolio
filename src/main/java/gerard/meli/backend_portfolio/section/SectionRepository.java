package gerard.meli.backend_portfolio.section;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SectionRepository extends JpaRepository<Section, UUID> {

    @EntityGraph(attributePaths = "translations")
    List<Section> findByVisibleTrueOrderByPositionAsc();

    @EntityGraph(attributePaths = "translations")
    List<Section> findAllByOrderByPositionAsc();

    Optional<Section> findByCode(String code);
}