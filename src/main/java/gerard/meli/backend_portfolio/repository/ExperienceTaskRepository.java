package gerard.meli.backend_portfolio.repository;

import gerard.meli.backend_portfolio.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface ExperienceTaskRepository extends JpaRepository<ExperienceTask, String> {

    List<ExperienceTask> findByExperienceIdOrderBySortOrderAsc(String experienceId);

    @Query("select coalesce(max(t.sortOrder), -1) from ExperienceTask t where t.experience.id = :experienceId")
    int maxSortOrder(@Param("experienceId") String experienceId);
}
