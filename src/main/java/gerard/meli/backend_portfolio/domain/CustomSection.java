package gerard.meli.backend_portfolio.domain;

import jakarta.persistence.*;
import java.time.*;
import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "custom_section")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", length = 50, nullable = false, unique = true)
    private String code;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "title", columnDefinition = "jsonb", nullable = false)
    private Map<String, String> title;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "markets", columnDefinition = "varchar(20)[]")
    private List<String> markets;

    @Column(name = "position", nullable = false)
    private Integer sortOrder = 0;

    @Column(name = "visible", nullable = false)
    private boolean visible = true;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<CustomItem> items = new ArrayList<>();

    public void addItem(CustomItem item) {
        items.add(item);
        item.setSection(this);
    }

    public void removeItem(CustomItem item) {
        items.remove(item);
        item.setSection(null);
    }
}
