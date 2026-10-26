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
@Table(name = "certification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "name", columnDefinition = "jsonb", nullable = false)
    private Map<String, String> name;

    @Column(name = "issuer", length = 200)
    private String issuer;

    @Column(name = "issued_on")
    private LocalDate issuedOn;

    @Column(name = "expires_on")
    private LocalDate expiresOn;

    @Column(name = "credential_url", length = 500)
    private String credentialUrl;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "markets", columnDefinition = "varchar(20)[]")
    private List<String> markets;

    @Column(name = "position", nullable = false)
    private Integer sortOrder = 0;

    @Column(name = "visible", nullable = false)
    private boolean visible = true;

}
