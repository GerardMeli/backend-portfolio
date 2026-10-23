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
@Table(name = "project_highlight")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectHighlight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "content", columnDefinition = "jsonb", nullable = false)
    private Map<String, String> content;

    @Column(name = "position", nullable = false)
    private Integer sortOrder = 0;

    @Column(name = "visible", nullable = false)
    private boolean visible = true;

}
