package gerard.meli.backend_portfolio.publicapi;

import gerard.meli.backend_portfolio.common.Market;
import gerard.meli.backend_portfolio.experience.EmploymentType;
import gerard.meli.backend_portfolio.project.ProjectType;

import java.time.LocalDate;
import java.util.List;

/** Objets renvoyés aux visiteurs : déjà traduits, sans identifiants internes. */
public final class PublicDtos {

    private PublicDtos() {
    }

    public record PortfolioResponse(
            String locale,
            Market market,
            ProfileDto profile,
            List<SectionDto> sections,
            List<ExperienceDto> experiences,
            List<ProjectSummaryDto> projects,
            List<SkillCategoryDto> skills,
            List<EducationDto> education) {
    }

    public record ProfileDto(
            String fullName, String email, String phone, String location,
            String githubUrl, String linkedinUrl, String avatarUrl,
            String headline, String bio, String ctaLabel, String cvUrl) {
    }

    public record SectionDto(String code, String title, String subtitle) {
    }

    public record ExperienceDto(
            String company, String companyUrl, String location, EmploymentType employmentType,
            LocalDate startDate, LocalDate endDate, boolean current,
            String roleTitle, String summary, List<String> tasks) {
    }

    public record ProjectSummaryDto(
            String slug, ProjectType type, String title, String summary,
            String coverImageUrl, String repoUrl, String demoUrl, boolean featured,
            LocalDate startDate, LocalDate endDate, List<String> technologies) {
    }

    public record ProjectDetailDto(
            String slug, ProjectType type, String title, String summary,
            String problem, String solution, String results,
            String coverImageUrl, String repoUrl, String demoUrl,
            LocalDate startDate, LocalDate endDate, List<String> technologies) {
    }

    public record SkillCategoryDto(String name, List<SkillDto> skills) {
    }

    public record SkillDto(String name, Short level, String icon) {
    }

    public record EducationDto(
            String institution, String degree, String description,
            LocalDate startDate, LocalDate endDate) {
    }
}