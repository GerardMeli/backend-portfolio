package gerard.meli.backend_portfolio.profile;

import gerard.meli.backend_portfolio.common.BaseEntity;
import gerard.meli.backend_portfolio.common.Market;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Accroche et CV adaptés à un marché (Afrique, Canada, UE) dans une langue donnée. */
@Entity
@Table(name = "profile_variant")
@Getter
@Setter
@NoArgsConstructor
public class ProfileVariant extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Market market;

    @Column(nullable = false)
    private String locale;

    private String headline;

    @Column(columnDefinition = "text")
    private String summary;

    private String ctaLabel;
    private String cvFileUrl;

    @Column(nullable = false)
    private boolean active = true;
}