package gerard.meli.backend_portfolio.education;

import gerard.meli.backend_portfolio.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "education")
@Getter
@Setter
@NoArgsConstructor
public class Education extends AuditableEntity {

    @Column(nullable = false)
    private String institution;

    private LocalDate startDate;
    private LocalDate endDate;

    @Column(nullable = false)
    private int position;

    @Column(nullable = false)
    private boolean published;

    @ElementCollection
    @CollectionTable(name = "education_translation", joinColumns = @JoinColumn(name = "education_id"))
    @MapKeyColumn(name = "locale")
    private Map<String, Text> translations = new HashMap<>();

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Text {
        @Column(nullable = false)
        private String degree;

        @Column(columnDefinition = "text")
        private String description;
    }
}