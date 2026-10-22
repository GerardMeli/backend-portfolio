package gerard.meli.backend_portfolio.experience;

import gerard.meli.backend_portfolio.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "experience")
@Getter
@Setter
@NoArgsConstructor
public class Experience extends AuditableEntity {

    @Column(nullable = false)
    private String company;

    private String companyUrl;
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentType employmentType = EmploymentType.FULL_TIME;

    @Column(nullable = false)
    private LocalDate startDate;

    /** null = poste actuel. */
    private LocalDate endDate;

    @Column(nullable = false)
    private int position;

    @Column(nullable = false)
    private boolean published;

    @ElementCollection
    @CollectionTable(name = "experience_translation", joinColumns = @JoinColumn(name = "experience_id"))
    @MapKeyColumn(name = "locale")
    private Map<String, Text> translations = new HashMap<>();

    @OneToMany(mappedBy = "experience", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<ExperienceTask> tasks = new ArrayList<>();

    public void addTask(ExperienceTask task) {
        task.setExperience(this);
        tasks.add(task);
    }

    public void removeTask(ExperienceTask task) {
        tasks.remove(task);
        task.setExperience(null);
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Text {
        @Column(nullable = false)
        private String roleTitle;

        @Column(columnDefinition = "text")
        private String summary;
    }
}