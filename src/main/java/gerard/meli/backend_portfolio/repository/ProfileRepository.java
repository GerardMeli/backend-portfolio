package gerard.meli.backend_portfolio.repository;

import gerard.meli.backend_portfolio.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    /** Le portfolio n'a qu'un seul profil. */
    Optional<Profile> findFirstByOrderByIdAsc();
}
