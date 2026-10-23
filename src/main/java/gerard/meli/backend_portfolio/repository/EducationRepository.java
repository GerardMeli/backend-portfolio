package gerard.meli.backend_portfolio.repository;

import gerard.meli.backend_portfolio.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface EducationRepository extends JpaRepository<Education, Long> {

    List<Education> findAllByOrderBySortOrderAsc();

    List<Education> findByVisibleTrueOrderBySortOrderAsc();

    @Query("select coalesce(max(e.sortOrder), -1) from Education e")
    int maxSortOrder();
}
