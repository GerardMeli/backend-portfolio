package gerard.meli.backend_portfolio.domain;

import gerard.meli.backend_portfolio.enums.ProjectKind;
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
@Table(name = "project")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "kind", length = 20, nullable = false)
    private ProjectKind kind = ProjectKind.PERSONAL;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "description", columnDefinition = "jsonb")
    private Map<String, String> description;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "tech_stack", columnDefinition = "varchar(50)[]")
    private List<String> techStack;

    @Column(name = "demo_url", length = 500)
    private String demoUrl;

    @Column(name = "repo_url", length = 500)
    private String repoUrl;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "markets", columnDefinition = "varchar(20)[]")
    private List<String> markets;

    @Column(name = "position", nullable = false)
    private Integer sortOrder = 0;

    @Column(name = "visible", nullable = false)
    private boolean visible = true;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<ProjectHighlight> highlights = new ArrayList<>();

    public void addHighlight(ProjectHighlight item) {
        highlights.add(item);
        item.setProject(this);
    }

    public void removeHighlight(ProjectHighlight item) {
        highlights.remove(item);
        item.setProject(null);
    }
}
