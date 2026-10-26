package gerard.meli.backend_portfolio.repository;

import gerard.meli.backend_portfolio.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface SkillCategoryRepository extends JpaRepository<SkillCategory, String> {

    List<SkillCategory> findAllByOrderBySortOrderAsc();

    @Query("select distinct c from SkillCategory c left join fetch c.skills where c.visible = true order by c.sortOrder asc")
    List<SkillCategory> findAllVisibleWithSkills();

    @Query("select c from SkillCategory c left join fetch c.skills where c.id = :id")
    Optional<SkillCategory> findByIdWithSkills(@Param("id") String id);

    @Query("select coalesce(max(c.sortOrder), -1) from SkillCategory c")
    int maxSortOrder();
}
