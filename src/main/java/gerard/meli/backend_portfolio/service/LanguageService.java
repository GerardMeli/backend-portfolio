package gerard.meli.backend_portfolio.service;

import gerard.meli.backend_portfolio.domain.*;
import gerard.meli.backend_portfolio.dto.*;
import gerard.meli.backend_portfolio.exception.*;
import gerard.meli.backend_portfolio.mapper.*;
import gerard.meli.backend_portfolio.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@Transactional
public class LanguageService {

    private final SpokenLanguageRepository repository;
    private final LanguageMapper mapper;

    public LanguageService(SpokenLanguageRepository repository, LanguageMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<LanguageDto> list() {
        return repository.findAllByOrderBySortOrderAsc().stream().map(mapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public LanguageDto get(Long id) {
        return mapper.toDto(find(id));
    }

    public LanguageDto create(LanguageDto dto) {
        SpokenLanguage entity = mapper.toEntity(dto);
        if (dto.sortOrder() == null) {
            entity.setSortOrder(repository.maxSortOrder() + 1);
        }
        return mapper.toDto(repository.save(entity));
    }

    public LanguageDto update(Long id, LanguageDto dto) {
        SpokenLanguage entity = find(id);
        mapper.update(dto, entity);
        return mapper.toDto(entity);
    }

    public void delete(Long id) {
        repository.delete(find(id));
    }

    /** Nouvel ordre : ids dans l'ordre souhaité. */
    public void reorder(ReorderRequest request) {
        ReorderSupport.apply(repository.findAll(), request.ids(), SpokenLanguage::getId, SpokenLanguage::setSortOrder);
    }

    private SpokenLanguage find(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Langue introuvable : " + id));
    }
}
