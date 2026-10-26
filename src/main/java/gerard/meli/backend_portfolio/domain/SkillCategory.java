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
@Table(name = "skill_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SkillCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "name", columnDefinition = "jsonb", nullable = false)
    private Map<String, String> name;

    @Column(name = "position", nullable = false)
    private Integer sortOrder = 0;

    @Column(name = "visible", nullable = false)
    private boolean visible = true;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<Skill> skills = new ArrayList<>();

    public void addSkill(Skill item) {
        skills.add(item);
        item.setCategory(this);
    }

    public void removeSkill(Skill item) {
        skills.remove(item);
        item.setCategory(null);
    }
}
