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
public class EducationService {

    private final EducationRepository repository;
    private final EducationMapper mapper;
    private final MarketGuard guard;

    public EducationService(EducationRepository repository, EducationMapper mapper, MarketGuard guard) {
        this.repository = repository;
        this.mapper = mapper;
        this.guard = guard;
    }

    @Transactional(readOnly = true)
    public List<EducationDto> list() {
        return repository.findAllByOrderBySortOrderAsc().stream().map(mapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public EducationDto get(String id) {
        return mapper.toDto(find(id));
    }

    public EducationDto create(EducationDto dto) {
        guard.check(dto.markets());
        Education entity = mapper.toEntity(dto);
        if (dto.sortOrder() == null) {
            entity.setSortOrder(repository.maxSortOrder() + 1);
        }
        return mapper.toDto(repository.save(entity));
    }

    public EducationDto update(String id, EducationDto dto) {
        Education entity = find(id);
        guard.check(dto.markets());
        mapper.update(dto, entity);
        return mapper.toDto(entity);
    }

    public void delete(String id) {
        repository.delete(find(id));
    }

    /** Nouvel ordre : ids dans l'ordre souhaité. */
    public void reorder(ReorderRequest request) {
        ReorderSupport.apply(repository.findAll(), request.ids(), Education::getId, Education::setSortOrder);
    }

    private Education find(String id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Formation introuvable : " + id));
    }
}
