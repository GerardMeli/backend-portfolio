package gerard.meli.backend_portfolio.skill;

import gerard.meli.backend_portfolio.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "skill_category")
@Getter
@Setter
@NoArgsConstructor
public class SkillCategory extends BaseEntity {

    @Column(nullable = false)
    private int position;

    @ElementCollection
    @CollectionTable(name = "skill_category_translation", joinColumns = @JoinColumn(name = "skill_category_id"))
    @MapKeyColumn(name = "locale")
    private Map<String, Text> translations = new HashMap<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<Skill> skills = new ArrayList<>();

    public void addSkill(Skill skill) {
        skill.setCategory(this);
        skills.add(skill);
    }

    public void removeSkill(Skill skill) {
        skills.remove(skill);
        skill.setCategory(null);
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Text {
        @Column(nullable = false)
        private String name;
    }
}