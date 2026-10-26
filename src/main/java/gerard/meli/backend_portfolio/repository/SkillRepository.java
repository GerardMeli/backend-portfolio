package gerard.meli.backend_portfolio.repository;

import gerard.meli.backend_portfolio.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface SkillRepository extends JpaRepository<Skill, String> {

    List<Skill> findByCategoryIdOrderBySortOrderAsc(String categoryId);

    @Query("select coalesce(max(s.sortOrder), -1) from Skill s where s.category.id = :categoryId")
    int maxSortOrder(@Param("categoryId") String categoryId);
}
