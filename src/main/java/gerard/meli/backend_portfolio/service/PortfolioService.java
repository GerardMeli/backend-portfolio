package gerard.meli.backend_portfolio.service;

import gerard.meli.backend_portfolio.domain.*;
import gerard.meli.backend_portfolio.dto.*;
import gerard.meli.backend_portfolio.enums.SectionTypes;
import gerard.meli.backend_portfolio.exception.*;
import gerard.meli.backend_portfolio.mapper.*;
import gerard.meli.backend_portfolio.repository.*;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

/** Assemble le CV public selon le marché choisi par le visiteur. */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PortfolioService {

    @Value("${app.markets.default:AFRICA}")
    private String defaultMarket;
    private final MarketRepository marketRepository;
    private final MarketSectionRepository marketSectionRepository;
    private final ProfileRepository profileRepository;
    private final ExperienceRepository experienceRepository;
    private final EducationRepository educationRepository;
    private final ProjectRepository projectRepository;
    private final SkillCategoryRepository skillCategoryRepository;
    private final CertificationRepository certificationRepository;
    private final SpokenLanguageRepository languageRepository;
    private final CustomSectionRepository customSectionRepository;
    private final ProfilePhotoService photoService;

    private final MarketMapper marketMapper;
    private final ProfileMapper profileMapper;
    private final ExperienceMapper experienceMapper;
    private final EducationMapper educationMapper;
    private final ProjectMapper projectMapper;
    private final SkillCategoryMapper skillCategoryMapper;
    private final CertificationMapper certificationMapper;
    private final LanguageMapper languageMapper;
    private final CustomSectionMapper customSectionMapper;

    /** Liste des marchés pour le sélecteur du visiteur. */
    public List<MarketInfoView> listMarkets() {
        return marketRepository.findAllByOrderByCodeAsc().stream().map(marketMapper::toInfoView).toList();
    }

    /**
     * @param marketCode marché choisi (AFRICA, CANADA, EU...) ; vide = marché par défaut
     * @param lang       langue demandée (fr, en...) ; vide = langue par défaut du marché
     */
    public PortfolioView assemble(String marketCode, String lang) {
        Market market = findMarket(marketCode);
        String language = resolveLang(lang, market);
        ViewContext ctx = new ViewContext(market.getCode(), language, market.getDefaultLocale());

        Profile profile = profileRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new NotFoundException("Le profil n'est pas encore configuré."));
        String photoUrl = photoService.publicUrl().orElse(profile.getPhotoUrl());
        ProfileView profileView = profileMapper.toView(profile, market, ctx, photoUrl);

        Map<String, CustomSection> customByCode = new HashMap<>();
        customSectionRepository.findAllVisibleWithItems().forEach(s -> customByCode.put(s.getCode(), s));

        List<SectionView> sections = new ArrayList<>();
        for (MarketSection config : marketSectionRepository.findByMarket(market.getCode())) {
            if (!config.isVisible()) continue;
            buildSection(config, profile, ctx, language, customByCode).ifPresent(sections::add);
        }
        return new PortfolioView(marketMapper.toInfoView(market), language, profileView, sections);
    }

    private Optional<SectionView> buildSection(MarketSection config, Profile profile, ViewContext ctx,
            String language, Map<String, CustomSection> customByCode) {
        String type = config.getId().getSectionType();
        List<?> items = switch (type) {
            case SectionTypes.SUMMARY -> summary(profile, ctx);
            case SectionTypes.EXPERIENCE -> experienceRepository.findAllVisibleWithTasks().stream()
                    .filter(e -> ctx.allows(e.getMarkets()))
                    .map(e -> experienceMapper.toView(e, ctx)).toList();
            case SectionTypes.EDUCATION -> educationRepository.findByVisibleTrueOrderBySortOrderAsc().stream()
                    .filter(e -> ctx.allows(e.getMarkets()))
                    .map(e -> educationMapper.toView(e, ctx)).toList();
            case SectionTypes.PROJECT -> projectRepository.findAllVisibleWithHighlights().stream()
                    .filter(p -> ctx.allows(p.getMarkets()))
                    .map(p -> projectMapper.toView(p, ctx)).toList();
            case SectionTypes.SKILL -> skillGroups(ctx);
            case SectionTypes.CERTIFICATION -> certificationRepository.findByVisibleTrueOrderBySortOrderAsc().stream()
                    .filter(c -> ctx.allows(c.getMarkets()))
                    .map(c -> certificationMapper.toView(c, ctx)).toList();
            case SectionTypes.LANGUAGE -> languageRepository.findByVisibleTrueOrderBySortOrderAsc().stream()
                    .map(l -> languageMapper.toView(l, ctx)).toList();
            default -> customItems(customByCode.get(type), ctx);
        };

        items = limit(items, config.getMaxItems());
        if (items.isEmpty()) return Optional.empty();

        CustomSection custom = customByCode.get(type);
        String title = custom != null ? ctx.text(custom.getTitle()) : SectionTitles.of(type, language);
        return Optional.of(new SectionView(type, title, new ArrayList<Object>(items)));
    }

    private List<SkillGroupView> skillGroups(ViewContext ctx) {
        return skillCategoryRepository.findAllVisibleWithSkills().stream()
                .map(category -> skillCategoryMapper.toView(category, ctx))
                .filter(group -> !group.skills().isEmpty()).toList();
    }

    private List<String> summary(Profile profile, ViewContext ctx) {
        String text = ctx.text(profile.getSummary());
        return text == null ? List.of() : List.of(text);
    }

    private List<CustomItemView> customItems(CustomSection section, ViewContext ctx) {
        if (section == null || !ctx.allows(section.getMarkets())) return List.of();
        return customSectionMapper.toItemViews(section, ctx);
    }

    private static List<?> limit(List<?> items, Integer max) {
        return max == null || max >= items.size() ? items : items.subList(0, max);
    }

    private Market findMarket(String code) {
        String wanted = (code == null || code.isBlank() ? defaultMarket : code).trim().toUpperCase(Locale.ROOT);
        return marketRepository.findById(wanted)
                .orElseThrow(() -> new NotFoundException("Marché inconnu : " + wanted));
    }

    /** "fr-CA" ou "fr_CA" donne "fr" ; vide donne la langue par défaut du marché. */
    private static String resolveLang(String lang, Market market) {
        if (lang == null || lang.isBlank()) return market.getDefaultLocale();
        String cleaned = lang.trim().toLowerCase(Locale.ROOT).replace('_', '-');
        int dash = cleaned.indexOf('-');
        return dash > 0 ? cleaned.substring(0, dash) : cleaned;
    }
}
