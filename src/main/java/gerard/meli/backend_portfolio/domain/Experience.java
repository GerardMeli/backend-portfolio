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
@Table(name = "experience")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company", length = 200, nullable = false)
    private String company;

    @Column(name = "location", length = 150)
    private String location;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "title", columnDefinition = "jsonb", nullable = false)
    private Map<String, String> title;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "description", columnDefinition = "jsonb")
    private Map<String, String> description;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", length = 30)
    private EmploymentType employmentType;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_current", nullable = false)
    private boolean current = false;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "markets", columnDefinition = "varchar(20)[]")
    private List<String> markets;

    @Column(name = "position", nullable = false)
    private Integer sortOrder = 0;

    @Column(name = "visible", nullable = false)
    private boolean visible = true;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "experience", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<ExperienceTask> tasks = new ArrayList<>();

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        if (createdAt == null) createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public void addTask(ExperienceTask item) {
        tasks.add(item);
        item.setExperience(this);
    }

    public void removeTask(ExperienceTask item) {
        tasks.remove(item);
        item.setExperience(null);
    }
}
