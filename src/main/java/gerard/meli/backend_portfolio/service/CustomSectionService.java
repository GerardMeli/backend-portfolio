package gerard.meli.backend_portfolio.service;

import gerard.meli.backend_portfolio.domain.*;
import gerard.meli.backend_portfolio.dto.*;
import gerard.meli.backend_portfolio.enums.SectionTypes;
import gerard.meli.backend_portfolio.exception.*;
import gerard.meli.backend_portfolio.mapper.*;
import gerard.meli.backend_portfolio.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@Transactional
public class CustomSectionService {

    private static final Set<String> RESERVED = Set.of(SectionTypes.SUMMARY, SectionTypes.EXPERIENCE,
            SectionTypes.EDUCATION, SectionTypes.PROJECT, SectionTypes.SKILL, SectionTypes.CERTIFICATION,
            SectionTypes.LANGUAGE);

    private final CustomSectionRepository repository;
    private final CustomItemRepository itemRepository;
    private final MarketRepository marketRepository;
    private final MarketSectionRepository marketSectionRepository;
    private final CustomSectionMapper mapper;
    private final CustomItemMapper itemMapper;
    private final MarketGuard guard;

    public CustomSectionService(CustomSectionRepository repository, CustomItemRepository itemRepository,
            MarketRepository marketRepository, MarketSectionRepository marketSectionRepository,
            CustomSectionMapper mapper, CustomItemMapper itemMapper, MarketGuard guard) {
        this.repository = repository;
        this.itemRepository = itemRepository;
        this.marketRepository = marketRepository;
        this.marketSectionRepository = marketSectionRepository;
        this.mapper = mapper;
        this.itemMapper = itemMapper;
        this.guard = guard;
    }

    // ---------- Sections ----------

    @Transactional(readOnly = true)
    public List<CustomSectionDto> list() {
        return repository.findAllByOrderBySortOrderAsc().stream().map(mapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public CustomSectionDto get(String id) {
        return mapper.toDto(repository.findByIdWithItems(id).orElseThrow(() -> notFound(id)));
    }

    /** Crée la section et l'ajoute en dernière position, visible, dans la config de chaque marché. */
    public CustomSectionDto create(CustomSectionDto dto) {
        String code = dto.code().trim().toUpperCase(Locale.ROOT);
        if (RESERVED.contains(code)) {
            throw new BadRequestException("Ce code est réservé à une section intégrée : " + code);
        }
        if (repository.existsByCode(code)) {
            throw new ConflictException("Une section avec ce code existe déjà : " + code);
        }
        guard.check(dto.markets());
        if (dto.items() != null) {
            dto.items().forEach(i -> guard.check(i.markets()));
        }
        CustomSection entity = mapper.toEntity(dto);
        if (dto.sortOrder() == null) {
            entity.setSortOrder(repository.maxSortOrder() + 1);
        }
        if (dto.items() != null) {
            for (int i = 0; i < dto.items().size(); i++) {
                if (dto.items().get(i).sortOrder() == null) {
                    entity.getItems().get(i).setSortOrder(i);
                }
            }
        }
        CustomSection saved = repository.save(entity);

        for (Market market : marketRepository.findAll()) {
            MarketSection config = new MarketSection();
            config.setId(new MarketSectionId(market.getCode(), saved.getCode()));
            config.setSortOrder(marketSectionRepository.maxSortOrder(market.getCode()) + 1);
            config.setVisible(true);
            marketSectionRepository.save(config);
        }
        return mapper.toDto(saved);
    }

    /** Le code n'est pas modifiable. Les éléments se gèrent via leurs propres méthodes. */
    public CustomSectionDto update(String id, CustomSectionDto dto) {
        CustomSection section = find(id);
        guard.check(dto.markets());
        mapper.update(dto, section);
        return mapper.toDto(section);
    }

    /** Supprime la section, ses éléments et sa présence dans la config de tous les marchés. */
    public void delete(String id) {
        String code = find(id).getCode();
        marketSectionRepository.deleteBySectionType(code);
        repository.deleteById(id);
    }

    // ---------- Éléments ----------

    @Transactional(readOnly = true)
    public List<CustomItemDto> listItems(String sectionId) {
        requireSection(sectionId);
        return itemRepository.findBySectionIdOrderBySortOrderAsc(sectionId).stream().map(itemMapper::toDto).toList();
    }

    public CustomItemDto addItem(String sectionId, CustomItemDto dto) {
        CustomSection section = find(sectionId);
        guard.check(dto.markets());
        CustomItem item = itemMapper.toEntity(dto);
        if (dto.sortOrder() == null) {
            item.setSortOrder(itemRepository.maxSortOrder(sectionId) + 1);
        }
        section.addItem(item);
        return itemMapper.toDto(itemRepository.save(item));
    }

    public CustomItemDto updateItem(String sectionId, String itemId, CustomItemDto dto) {
        CustomItem item = findItem(sectionId, itemId);
        guard.check(dto.markets());
        itemMapper.update(dto, item);
        return itemMapper.toDto(item);
    }

    public void deleteItem(String sectionId, String itemId) {
        CustomItem item = findItem(sectionId, itemId);
        item.getSection().getItems().remove(item);
        itemRepository.delete(item);
    }

    public void reorderItems(String sectionId, ReorderRequest request) {
        requireSection(sectionId);
        ReorderSupport.apply(itemRepository.findBySectionIdOrderBySortOrderAsc(sectionId), request.ids(),
                CustomItem::getId, CustomItem::setSortOrder);
    }

    // ---------- interne ----------

    private CustomSection find(String id) {
        return repository.findById(id).orElseThrow(() -> notFound(id));
    }

    private void requireSection(String id) {
        if (!repository.existsById(id)) throw notFound(id);
    }

    private CustomItem findItem(String sectionId, String itemId) {
        return itemRepository.findById(itemId)
                .filter(i -> i.getSection().getId().equals(sectionId))
                .orElseThrow(() -> new NotFoundException("Élément introuvable : " + itemId));
    }

    private NotFoundException notFound(String id) {
        return new NotFoundException("Section personnalisée introuvable : " + id);
    }
}
