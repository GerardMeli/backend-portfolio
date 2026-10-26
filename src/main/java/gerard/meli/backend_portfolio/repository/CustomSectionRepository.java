package gerard.meli.backend_portfolio.repository;

import gerard.meli.backend_portfolio.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface CustomSectionRepository extends JpaRepository<CustomSection, String> {

    Optional<CustomSection> findByCode(String code);

    boolean existsByCode(String code);

    List<CustomSection> findAllByOrderBySortOrderAsc();

    @Query("select distinct s from CustomSection s left join fetch s.items where s.visible = true order by s.sortOrder asc")
    List<CustomSection> findAllVisibleWithItems();

    @Query("select s from CustomSection s left join fetch s.items where s.id = :id")
    Optional<CustomSection> findByIdWithItems(@Param("id") String id);

    @Query("select coalesce(max(s.sortOrder), -1) from CustomSection s")
    int maxSortOrder();
}
