package gerard.meli.backend_portfolio.service;

import gerard.meli.backend_portfolio.domain.*;
import gerard.meli.backend_portfolio.dto.*;
import gerard.meli.backend_portfolio.enums.SectionTypes;
import gerard.meli.backend_portfolio.exception.*;
import gerard.meli.backend_portfolio.mapper.*;
import gerard.meli.backend_portfolio.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class MarketService {

    /** Ordre par défaut des sections intégrées pour un nouveau marché. */
    private static final List<String> BUILT_IN = List.of(SectionTypes.SUMMARY, SectionTypes.EXPERIENCE,
            SectionTypes.EDUCATION, SectionTypes.SKILL, SectionTypes.PROJECT, SectionTypes.CERTIFICATION,
            SectionTypes.LANGUAGE);

    private final MarketRepository repository;
    private final MarketSectionRepository sectionRepository;
    private final CustomSectionRepository customSectionRepository;
    private final MarketMapper mapper;
    private final MarketSectionMapper sectionMapper;

    @Transactional(readOnly = true)
    public List<MarketDto> list() {
        return repository.findAllByOrderByCodeAsc().stream().map(mapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public MarketDto get(String code) {
        return mapper.toDto(find(code));
    }

    /** Crée un marché et initialise sa config avec toutes les sections (intégrées puis personnalisées). */
    public MarketDto create(MarketDto dto) {
        String code = dto.code().trim().toUpperCase(Locale.ROOT);
        if (repository.existsById(code)) {
            throw new ConflictException("Ce marché existe déjà : " + code);
        }
        Market saved = repository.save(mapper.toEntity(dto));

        List<String> types = new ArrayList<>(BUILT_IN);
        customSectionRepository.findAllByOrderBySortOrderAsc().forEach(s -> types.add(s.getCode()));
        int position = 1;
        for (String type : types) {
            MarketSection config = new MarketSection();
            config.setId(new MarketSectionId(saved.getCode(), type));
            config.setSortOrder(position++);
            config.setVisible(true);
            sectionRepository.save(config);
        }
        return mapper.toDto(saved);
    }

    /** Le code n'est pas modifiable. */
    public MarketDto update(String code, MarketDto dto) {
        Market market = find(code);
        mapper.update(dto, market);
        return mapper.toDto(market);
    }

    public void delete(String code) {
        Market market = find(code);
        if (repository.count() <= 1) {
            throw new BadRequestException("Impossible de supprimer le dernier marché.");
        }
        repository.delete(market);
    }

    @Transactional(readOnly = true)
    public List<MarketSectionDto> listSections(String code) {
        String market = find(code).getCode();
        return sectionRepository.findByMarket(market).stream().map(sectionMapper::toDto).toList();
    }

    /**
     * Met à jour (ou ajoute) les sections fournies : ordre, visibilité, plafond d'éléments.
     * Les sections absentes de la liste ne sont pas modifiées.
     */
    public List<MarketSectionDto> replaceSections(String code, List<MarketSectionDto> dtos) {
        String market = find(code).getCode();

        Set<String> valid = new HashSet<>(BUILT_IN);
        customSectionRepository.findAll().forEach(s -> valid.add(s.getCode()));
        Set<String> seen = new HashSet<>();
        for (MarketSectionDto dto : dtos) {
            String type = dto.sectionType().trim().toUpperCase(Locale.ROOT);
            if (!valid.contains(type)) {
                throw new BadRequestException("Section inconnue : " + type);
            }
            if (!seen.add(type)) {
                throw new BadRequestException("Section en double : " + type);
            }
        }

        for (MarketSectionDto dto : dtos) {
            MarketSectionId id = new MarketSectionId(market, dto.sectionType().trim().toUpperCase(Locale.ROOT));
            sectionRepository.findById(id).ifPresentOrElse(
                    existing -> sectionMapper.update(dto, existing),
                    () -> sectionRepository.save(sectionMapper.toEntity(market, dto)));
        }
        return listSections(market);
    }

    private Market find(String code) {
        return repository.findById(code.trim().toUpperCase(Locale.ROOT))
                .orElseThrow(() -> new NotFoundException("Marché introuvable : " + code));
    }
}
