package gerard.meli.backend_portfolio.repository;

import gerard.meli.backend_portfolio.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface CertificationRepository extends JpaRepository<Certification, Long> {

    List<Certification> findAllByOrderBySortOrderAsc();

    List<Certification> findByVisibleTrueOrderBySortOrderAsc();

    @Query("select coalesce(max(c.sortOrder), -1) from Certification c")
    int maxSortOrder();
}
