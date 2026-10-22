package gerard.meli.backend_portfolio.profile;

import gerard.meli.backend_portfolio.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/** Une seule ligne attendue dans cette table : le profil du propriétaire du portfolio. */
@Entity
@Table(name = "profile")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class Profile extends BaseEntity {

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String email;

    private String phone;
    private String location;
    private String githubUrl;
    private String linkedinUrl;
    private String avatarUrl;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ElementCollection
    @CollectionTable(name = "profile_translation", joinColumns = @JoinColumn(name = "profile_id"))
    @MapKeyColumn(name = "locale")
    private Map<String, Text> translations = new HashMap<>();

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Text {
        @Column(nullable = false)
        private String headline;

        @Column(columnDefinition = "text")
        private String bio;
    }
}