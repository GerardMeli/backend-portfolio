package gerard.meli.backend_portfolio.project;

import gerard.meli.backend_portfolio.common.AuditableEntity;
import gerard.meli.backend_portfolio.skill.Skill;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "project")
@Getter
@Setter
@NoArgsConstructor
public class Project extends AuditableEntity {

    @Column(nullable = false, unique = true)
    private String slug;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectType type = ProjectType.PERSONAL;

    private String repoUrl;
    private String demoUrl;
    private String coverImageUrl;
    private LocalDate startDate;
    private LocalDate endDate;

    @Column(nullable = false)
    private boolean featured;

    @Column(nullable = false)
    private int position;

    @Column(nullable = false)
    private boolean published;

    @ElementCollection
    @CollectionTable(name = "project_translation", joinColumns = @JoinColumn(name = "project_id"))
    @MapKeyColumn(name = "locale")
    private Map<String, Text> translations = new HashMap<>();

    @ManyToMany
    @JoinTable(name = "project_skill",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private Set<Skill> skills = new HashSet<>();

    /** Étude de cas : problème, solution, résultats. */
    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Text {
        @Column(nullable = false)
        private String title;

        @Column(length = 500)
        private String summary;

        @Column(columnDefinition = "text")
        private String problem;

        @Column(columnDefinition = "text")
        private String solution;

        @Column(columnDefinition = "text")
        private String results;
    }
}