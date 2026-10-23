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
@Table(name = "spoken_language")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpokenLanguage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "name", columnDefinition = "jsonb", nullable = false)
    private Map<String, String> name;

    @Column(name = "level", length = 20)
    private String level;

    @Column(name = "position", nullable = false)
    private Integer sortOrder = 0;

    @Column(name = "visible", nullable = false)
    private boolean visible = true;
}
