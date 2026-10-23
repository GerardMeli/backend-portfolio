package gerard.meli.backend_portfolio.repository;

import gerard.meli.backend_portfolio.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface CustomItemRepository extends JpaRepository<CustomItem, Long> {

    List<CustomItem> findBySectionIdOrderBySortOrderAsc(Long sectionId);

    @Query("select coalesce(max(i.sortOrder), -1) from CustomItem i where i.section.id = :sectionId")
    int maxSortOrder(@Param("sectionId") Long sectionId);
}
