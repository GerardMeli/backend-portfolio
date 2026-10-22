package gerard.meli.backend_portfolio.section;

import gerard.meli.backend_portfolio.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "section")
@Getter
@Setter
@NoArgsConstructor
public class Section extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private int position;

    @Column(nullable = false)
    private boolean visible = true;

    @ElementCollection
    @CollectionTable(name = "section_translation", joinColumns = @JoinColumn(name = "section_id"))
    @MapKeyColumn(name = "locale")
    private Map<String, Text> translations = new HashMap<>();

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Text {
        @Column(nullable = false)
        private String title;

        private String subtitle;
    }
}