package gerard.meli.backend_portfolio.repository;

import gerard.meli.backend_portfolio.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface SpokenLanguageRepository extends JpaRepository<SpokenLanguage, String> {

    List<SpokenLanguage> findAllByOrderBySortOrderAsc();

    List<SpokenLanguage> findByVisibleTrueOrderBySortOrderAsc();

    @Query("select coalesce(max(l.sortOrder), -1) from SpokenLanguage l")
    int maxSortOrder();
}
