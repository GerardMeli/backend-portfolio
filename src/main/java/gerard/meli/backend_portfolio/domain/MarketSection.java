package gerard.meli.backend_portfolio.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.*;
import java.util.*;

@Entity
@Table(name = "market_section")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MarketSection {

    @EmbeddedId
    private MarketSectionId id = new MarketSectionId();

    @Column(name = "position", nullable = false)
    private Integer sortOrder = 0;

    @Column(name = "visible", nullable = false)
    private boolean visible = true;

    @Column(name = "max_items")
    private Integer maxItems;

}
