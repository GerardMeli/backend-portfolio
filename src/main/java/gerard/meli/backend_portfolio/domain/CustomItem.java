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
@Table(name = "custom_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "section_id", nullable = false)
    private CustomSection section;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "title", columnDefinition = "jsonb", nullable = false)
    private Map<String, String> title;

    @Column(name = "subtitle", length = 200)
    private String subtitle;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "description", columnDefinition = "jsonb")
    private Map<String, String> description;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "url", length = 500)
    private String url;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "markets", columnDefinition = "varchar(20)[]")
    private List<String> markets;

    @Column(name = "position", nullable = false)
    private Integer sortOrder = 0;

    @Column(name = "visible", nullable = false)
    private boolean visible = true;

}
