package gerard.meli.backend_portfolio.repository;

import gerard.meli.backend_portfolio.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findAllByOrderBySortOrderAscStartDateDesc();

    @Query("select distinct p from Project p left join fetch p.highlights where p.visible = true order by p.sortOrder asc, p.startDate desc")
    List<Project> findAllVisibleWithHighlights();

    @Query("select p from Project p left join fetch p.highlights where p.id = :id")
    Optional<Project> findByIdWithHighlights(@Param("id") Long id);

    @Query("select coalesce(max(p.sortOrder), -1) from Project p")
    int maxSortOrder();
}
