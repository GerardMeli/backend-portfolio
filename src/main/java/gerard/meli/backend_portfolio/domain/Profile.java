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
@Table(name = "profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "full_name", length = 150, nullable = false)
    private String fullName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "headline", columnDefinition = "jsonb")
    private Map<String, String> headline;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "summary", columnDefinition = "jsonb")
    private Map<String, String> summary;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "nationality", length = 100)
    private String nationality;

    @Column(name = "marital_status", length = 50)
    private String maritalStatus;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "work_authorization", columnDefinition = "jsonb")
    private Map<String, String> workAuthorization;

    @Column(name = "linkedin_url", length = 500)
    private String linkedinUrl;

    @Column(name = "github_url", length = 500)
    private String githubUrl;

    @Column(name = "website_url", length = 500)
    private String websiteUrl;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    @PreUpdate
    void touch() {
        updatedAt = Instant.now();
    }
}
