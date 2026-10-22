package gerard.meli.backend_portfolio.experience;

import gerard.meli.backend_portfolio.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/** Une tâche / réalisation listée sous une expérience. */
@Entity
@Table(name = "experience_task")
@Getter
@Setter
@NoArgsConstructor
public class ExperienceTask extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "experience_id", nullable = false)
    private Experience experience;

    @Column(nullable = false)
    private int position;

    @ElementCollection
    @CollectionTable(name = "experience_task_translation", joinColumns = @JoinColumn(name = "task_id"))
    @MapKeyColumn(name = "locale")
    private Map<String, Text> translations = new HashMap<>();

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Text {
        @Column(nullable = false, columnDefinition = "text")
        private String content;
    }
}