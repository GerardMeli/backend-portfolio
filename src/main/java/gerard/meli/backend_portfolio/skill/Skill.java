package gerard.meli.backend_portfolio.skill;

import gerard.meli.backend_portfolio.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "skill")
@Getter
@Setter
@NoArgsConstructor
public class Skill extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private SkillCategory category;

    @Column(nullable = false)
    private String name;

    /** Niveau de 1 à 5, facultatif. */
    private Short level;

    private String icon;

    @Column(nullable = false)
    private int position;
}