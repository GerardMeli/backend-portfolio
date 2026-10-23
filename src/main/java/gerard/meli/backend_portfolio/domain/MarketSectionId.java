package gerard.meli.backend_portfolio.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class MarketSectionId implements Serializable {

    @Column(name = "market_code", length = 20, nullable = false)
    private String marketCode;

    @Column(name = "section_type", length = 50, nullable = false)
    private String sectionType;

    public MarketSectionId() {
    }

    public MarketSectionId(String marketCode, String sectionType) {
        this.marketCode = marketCode;
        this.sectionType = sectionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MarketSectionId that)) return false;
        return Objects.equals(marketCode, that.marketCode) && Objects.equals(sectionType, that.sectionType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(marketCode, sectionType);
    }
}
