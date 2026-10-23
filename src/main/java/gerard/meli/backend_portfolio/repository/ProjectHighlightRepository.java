package gerard.meli.backend_portfolio.repository;

import gerard.meli.backend_portfolio.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface ProjectHighlightRepository extends JpaRepository<ProjectHighlight, Long> {

    List<ProjectHighlight> findByProjectIdOrderBySortOrderAsc(Long projectId);

    @Query("select coalesce(max(h.sortOrder), -1) from ProjectHighlight h where h.project.id = :projectId")
    int maxSortOrder(@Param("projectId") Long projectId);
}
