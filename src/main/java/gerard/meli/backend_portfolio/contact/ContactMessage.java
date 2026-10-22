package gerard.meli.backend_portfolio.contact;

import gerard.meli.backend_portfolio.common.BaseEntity;
import gerard.meli.backend_portfolio.common.Market;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "contact_message")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class ContactMessage extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    private String subject;

    @Column(nullable = false, columnDefinition = "text")
    private String message;

    private String locale;

    @Enumerated(EnumType.STRING)
    private Market market;

    @Column(name = "is_read", nullable = false)
    private boolean read;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}