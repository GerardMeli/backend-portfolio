package gerard.meli.backend_portfolio.adminapi;

import gerard.meli.backend_portfolio.common.Market;
import gerard.meli.backend_portfolio.experience.EmploymentType;
import gerard.meli.backend_portfolio.project.ProjectType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Objets échangés avec l'admin Angular. Contrairement à l'API publique, on renvoie TOUTES
 * les traductions (Map locale -> texte) pour pouvoir les éditer.
 * Les URL doivent commencer par http:// ou https:// (évite les liens "javascript:").
 */
public final class AdminDtos {

    static final String URL_PATTERN = "^https?://\\S+$";
    static final String SLUG_PATTERN = "^[a-z0-9]+(?:-[a-z0-9]+)*$";

    private AdminDtos() {
    }

    // ------------------------------------------------------------------ commun
    public record ReorderRequest(@NotEmpty List<UUID> ids) {
    }

    public record PublishRequest(boolean published) {
    }

    public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {
    }

    // ------------------------------------------------------------------ profil
    public record ProfileText(
            @NotBlank @Size(max = 255) String headline,
            @Size(max = 5000) String bio) {
    }

    public record ProfileRequest(
            @NotBlank @Size(max = 150) String fullName,
            @NotBlank @Email @Size(max = 255) String email,
            @Size(max = 40) String phone,
            @Size(max = 150) String location,
            @Size(max = 500) @Pattern(regexp = URL_PATTERN) String githubUrl,
            @Size(max = 500) @Pattern(regexp = URL_PATTERN) String linkedinUrl,
            @Size(max = 500) @Pattern(regexp = URL_PATTERN) String avatarUrl,
            @NotEmpty Map<String, @Valid ProfileText> translations) {
    }

    public record ProfileResponse(
            UUID id, String fullName, String email, String phone, String location,
            String githubUrl, String linkedinUrl, String avatarUrl,
            Map<String, ProfileText> translations) {
    }

    public record VariantRequest(
            @NotNull Market market,
            @NotBlank @Size(max = 5) String locale,
            @Size(max = 255) String headline,
            @Size(max = 5000) String summary,
            @Size(max = 100) String ctaLabel,
            @Size(max = 500) @Pattern(regexp = URL_PATTERN) String cvFileUrl,
            Boolean active) {
    }

    public record VariantResponse(
            UUID id, Market market, String locale, String headline, String summary,
            String ctaLabel, String cvFileUrl, boolean active) {
    }

    // ------------------------------------------------------------------ sections
    public record SectionText(
            @NotBlank @Size(max = 150) String title,
            @Size(max = 255) String subtitle) {
    }

    public record SectionRequest(
            Boolean visible,
            @NotEmpty Map<String, @Valid SectionText> translations) {
    }

    public record SectionResponse(
            UUID id, String code, int position, boolean visible, Map<String, SectionText> translations) {
    }

    // ------------------------------------------------------------------ compétences
    public record CategoryText(@NotBlank @Size(max = 100) String name) {
    }

    public record CategoryRequest(@NotEmpty Map<String, @Valid CategoryText> translations) {
    }

    public record SkillRequest(
            @NotBlank @Size(max = 100) String name,
            @Min(1) @Max(5) Short level,
            @Size(max = 100) String icon) {
    }

    public record SkillResponse(UUID id, String name, Short level, String icon, int position) {
    }

    public record CategoryResponse(
            UUID id, int position, Map<String, CategoryText> translations, List<SkillResponse> skills) {
    }

    // ------------------------------------------------------------------ expériences
    public record ExperienceText(
            @NotBlank @Size(max = 150) String roleTitle,
            @Size(max = 5000) String summary) {
    }

    public record ExperienceRequest(
            @NotBlank @Size(max = 150) String company,
            @Size(max = 500) @Pattern(regexp = URL_PATTERN) String companyUrl,
            @Size(max = 150) String location,
            @NotNull EmploymentType employmentType,
            @NotNull LocalDate startDate,
            LocalDate endDate,
            Boolean published,
            @NotEmpty Map<String, @Valid ExperienceText> translations) {
    }

    public record TaskText(@NotBlank @Size(max = 2000) String content) {
    }

    public record TaskRequest(@NotEmpty Map<String, @Valid TaskText> translations) {
    }

    public record TaskResponse(UUID id, int position, Map<String, TaskText> translations) {
    }

    public record ExperienceResponse(
            UUID id, String company, String companyUrl, String location, EmploymentType employmentType,
            LocalDate startDate, LocalDate endDate, int position, boolean published,
            Map<String, ExperienceText> translations, List<TaskResponse> tasks) {
    }

    // ------------------------------------------------------------------ formations
    public record EducationText(
            @NotBlank @Size(max = 200) String degree,
            @Size(max = 5000) String description) {
    }

    public record EducationRequest(
            @NotBlank @Size(max = 200) String institution,
            LocalDate startDate,
            LocalDate endDate,
            Boolean published,
            @NotEmpty Map<String, @Valid EducationText> translations) {
    }

    public record EducationResponse(
            UUID id, String institution, LocalDate startDate, LocalDate endDate,
            int position, boolean published, Map<String, EducationText> translations) {
    }

    // ------------------------------------------------------------------ projets
    public record ProjectText(
            @NotBlank @Size(max = 200) String title,
            @Size(max = 500) String summary,
            @Size(max = 10000) String problem,
            @Size(max = 10000) String solution,
            @Size(max = 10000) String results) {
    }

    public record ProjectRequest(
            @NotBlank @Size(max = 120) @Pattern(regexp = SLUG_PATTERN) String slug,
            @NotNull ProjectType type,
            @Size(max = 500) @Pattern(regexp = URL_PATTERN) String repoUrl,
            @Size(max = 500) @Pattern(regexp = URL_PATTERN) String demoUrl,
            @Size(max = 500) @Pattern(regexp = URL_PATTERN) String coverImageUrl,
            LocalDate startDate,
            LocalDate endDate,
            Boolean featured,
            Boolean published,
            Set<UUID> skillIds,
            @NotEmpty Map<String, @Valid ProjectText> translations) {
    }

    public record SkillRef(UUID id, String name) {
    }

    public record ProjectResponse(
            UUID id, String slug, ProjectType type, String repoUrl, String demoUrl, String coverImageUrl,
            LocalDate startDate, LocalDate endDate, boolean featured, int position, boolean published,
            List<SkillRef> skills, Map<String, ProjectText> translations) {
    }

    // ------------------------------------------------------------------ messages de contact
    public record MessageResponse(
            UUID id, String name, String email, String subject, String message,
            String locale, Market market, boolean read, Instant createdAt) {
    }

    public record ReadRequest(boolean read) {
    }

    public record UnreadCount(long unread) {
    }
}