package gerard.meli.backend_portfolio.repository;

import gerard.meli.backend_portfolio.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface MarketSectionRepository extends JpaRepository<MarketSection, MarketSectionId> {

    @Query("select ms from MarketSection ms where ms.id.marketCode = :market order by ms.sortOrder asc")
    List<MarketSection> findByMarket(@Param("market") String market);

    @Query("select coalesce(max(ms.sortOrder), 0) from MarketSection ms where ms.id.marketCode = :market")
    int maxSortOrder(@Param("market") String market);

    /** Utilisé à la suppression d'une section personnalisée. */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from MarketSection ms where ms.id.sectionType = :type")
    int deleteBySectionType(@Param("type") String type);
}
