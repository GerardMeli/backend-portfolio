package gerard.meli.backend_portfolio.publicapi;

import gerard.meli.backend_portfolio.common.LocaleSupport;
import gerard.meli.backend_portfolio.common.Market;
import gerard.meli.backend_portfolio.education.Education;
import gerard.meli.backend_portfolio.education.EducationRepository;
import gerard.meli.backend_portfolio.experience.Experience;
import gerard.meli.backend_portfolio.experience.ExperienceRepository;
import gerard.meli.backend_portfolio.experience.ExperienceTask;
import gerard.meli.backend_portfolio.profile.Profile;
import gerard.meli.backend_portfolio.profile.ProfileRepository;
import gerard.meli.backend_portfolio.profile.ProfileVariant;
import gerard.meli.backend_portfolio.profile.ProfileVariantRepository;
import gerard.meli.backend_portfolio.project.Project;
import gerard.meli.backend_portfolio.project.ProjectRepository;
import gerard.meli.backend_portfolio.publicapi.PublicDtos.*;
import gerard.meli.backend_portfolio.section.SectionRepository;
import gerard.meli.backend_portfolio.skill.Skill;
import gerard.meli.backend_portfolio.skill.SkillCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Lecture seule. @Transactional car open-in-view est désactivé :
 * le mapping vers les DTO doit se faire pendant que la session Hibernate est ouverte.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicPortfolioService {

    private final ProfileRepository profiles;
    private final ProfileVariantRepository variants;
    private final SectionRepository sections;
    private final ExperienceRepository experiences;
    private final ProjectRepository projects;
    private final SkillCategoryRepository skillCategories;
    private final EducationRepository educations;
    private final LocaleSupport locales;

    public PortfolioResponse getPortfolio(String locale, Market market) {
        ProfileVariant variant = market == null
                ? null
                : variants.findByMarketAndLocaleAndActiveTrue(market, locale).orElse(null);

        ProfileDto profile = profiles.findFirstBy()
                .map(p -> toProfile(p, variant, locale))
                .orElse(null);

        List<SectionDto> sectionDtos = sections.findByVisibleTrueOrderByPositionAsc().stream()
                .map(s -> locales.pick(s.getTranslations(), locale)
                        .map(t -> new SectionDto(s.getCode(), t.getTitle(), t.getSubtitle())))
                .flatMap(Optional::stream)
                .toList();

        List<ExperienceDto> experienceDtos = experiences.findByPublishedTrueOrderByPositionAsc().stream()
                .map(e -> toExperience(e, locale))
                .flatMap(Optional::stream)
                .toList();

        List<ProjectSummaryDto> projectDtos = projects.findByPublishedTrueOrderByPositionAsc().stream()
                .map(p -> toProjectSummary(p, locale))
                .flatMap(Optional::stream)
                .toList();

        List<SkillCategoryDto> skillDtos = skillCategories.findAllByOrderByPositionAsc().stream()
                .map(c -> locales.pick(c.getTranslations(), locale)
                        .map(t -> new SkillCategoryDto(t.getName(),
                                c.getSkills().stream()
                                        .map(s -> new SkillDto(s.getName(), s.getLevel(), s.getIcon()))
                                        .toList())))
                .flatMap(Optional::stream)
                .toList();

        List<EducationDto> educationDtos = educations.findByPublishedTrueOrderByPositionAsc().stream()
                .map(e -> toEducation(e, locale))
                .flatMap(Optional::stream)
                .toList();

        return new PortfolioResponse(locale, market, profile, sectionDtos, experienceDtos,
                projectDtos, skillDtos, educationDtos);
    }

    public Optional<ProjectDetailDto> getProject(String slug, String locale) {
        return projects.findBySlug(slug)
                .filter(Project::isPublished) // un brouillon ne doit jamais être visible
                .flatMap(p -> locales.pick(p.getTranslations(), locale)
                        .map(t -> new ProjectDetailDto(
                                p.getSlug(), p.getType(), t.getTitle(), t.getSummary(),
                                t.getProblem(), t.getSolution(), t.getResults(),
                                p.getCoverImageUrl(), p.getRepoUrl(), p.getDemoUrl(),
                                p.getStartDate(), p.getEndDate(), technologies(p))));
    }

    // ------------------------------------------------------------------ mapping

    /** Les champs de la variante (marché + langue) remplacent ceux du profil quand ils sont renseignés. */
    private ProfileDto toProfile(Profile p, ProfileVariant v, String locale) {
        Profile.Text t = locales.pick(p.getTranslations(), locale).orElse(null);
        String headline = firstNonBlank(v == null ? null : v.getHeadline(), t == null ? null : t.getHeadline());
        String bio = firstNonBlank(v == null ? null : v.getSummary(), t == null ? null : t.getBio());
        return new ProfileDto(
                p.getFullName(), p.getEmail(), p.getPhone(), p.getLocation(),
                p.getGithubUrl(), p.getLinkedinUrl(), p.getAvatarUrl(),
                headline, bio,
                v == null ? null : v.getCtaLabel(),
                v == null ? null : v.getCvFileUrl());
    }

    private Optional<ExperienceDto> toExperience(Experience e, String locale) {
        return locales.pick(e.getTranslations(), locale).map(t -> {
            List<String> tasks = e.getTasks().stream()
                    .map(task -> locales.pick(task.getTranslations(), locale))
                    .flatMap(Optional::stream)
                    .map(ExperienceTask.Text::getContent)
                    .toList();
            return new ExperienceDto(
                    e.getCompany(), e.getCompanyUrl(), e.getLocation(), e.getEmploymentType(),
                    e.getStartDate(), e.getEndDate(), e.getEndDate() == null,
                    t.getRoleTitle(), t.getSummary(), tasks);
        });
    }

    private Optional<ProjectSummaryDto> toProjectSummary(Project p, String locale) {
        return locales.pick(p.getTranslations(), locale).map(t -> new ProjectSummaryDto(
                p.getSlug(), p.getType(), t.getTitle(), t.getSummary(),
                p.getCoverImageUrl(), p.getRepoUrl(), p.getDemoUrl(), p.isFeatured(),
                p.getStartDate(), p.getEndDate(), technologies(p)));
    }

    private Optional<EducationDto> toEducation(Education e, String locale) {
        return locales.pick(e.getTranslations(), locale).map(t -> new EducationDto(
                e.getInstitution(), t.getDegree(), t.getDescription(), e.getStartDate(), e.getEndDate()));
    }

    private List<String> technologies(Project p) {
        return p.getSkills().stream()
                .map(Skill::getName)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    private String firstNonBlank(String first, String second) {
        return first != null && !first.isBlank() ? first : second;
    }
}