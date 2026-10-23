package gerard.meli.backend_portfolio.domain;

import gerard.meli.backend_portfolio.enums.PageFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.*;
import java.util.*;

@Entity
@Table(name = "market")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Market {

    @Id
    @Column(name = "code", length = 20)
    private String code;

    @Column(name = "label", length = 100, nullable = false)
    private String label;

    @Column(name = "default_locale", length = 5, nullable = false)
    private String defaultLocale;

    @Enumerated(EnumType.STRING)
    @Column(name = "page_format", length = 10, nullable = false)
    private PageFormat pageFormat;

    @Column(name = "date_format", length = 20, nullable = false)
    private String dateFormat;

    @Column(name = "show_photo", nullable = false)
    private boolean showPhoto = false;

    @Column(name = "show_personal_details", nullable = false)
    private boolean showPersonalDetails = false;

    @Column(name = "max_pages", nullable = false)
    private Short maxPages = (short) 2;

    @Column(name = "notes")
    private String notes;
}
