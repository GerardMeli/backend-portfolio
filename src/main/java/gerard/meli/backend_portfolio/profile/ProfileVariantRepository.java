package gerard.meli.backend_portfolio.profile;

import gerard.meli.backend_portfolio.common.Market;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfileVariantRepository extends JpaRepository<ProfileVariant, UUID> {

    Optional<ProfileVariant> findByMarketAndLocaleAndActiveTrue(Market market, String locale);

    List<ProfileVariant> findAllByOrderByMarketAscLocaleAsc();
}