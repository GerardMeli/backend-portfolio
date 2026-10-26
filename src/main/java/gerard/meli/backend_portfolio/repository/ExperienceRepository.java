package gerard.meli.backend_portfolio.repository;

import gerard.meli.backend_portfolio.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface ExperienceRepository extends JpaRepository<Experience, String> {

    List<Experience> findAllByOrderBySortOrderAscStartDateDesc();

    @Query("select distinct e from Experience e left join fetch e.tasks where e.visible = true order by e.sortOrder asc, e.startDate desc")
    List<Experience> findAllVisibleWithTasks();

    @Query("select e from Experience e left join fetch e.tasks where e.id = :id")
    Optional<Experience> findByIdWithTasks(@Param("id") String id);

    @Query("select coalesce(max(e.sortOrder), -1) from Experience e")
    int maxSortOrder();
}
