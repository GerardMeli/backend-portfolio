package gerard.meli.backend_portfolio.repository;

import gerard.meli.backend_portfolio.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface MarketRepository extends JpaRepository<Market, String> {

    List<Market> findAllByOrderByCodeAsc();
}
