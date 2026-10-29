package gerard.meli.backend_portfolio.service;

import gerard.meli.backend_portfolio.domain.*;
import gerard.meli.backend_portfolio.dto.*;
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
public class CertificationService {

    private final CertificationRepository repository;
    private final CertificationMapper mapper;
    private final MarketGuard guard;

    @Transactional(readOnly = true)
    public List<CertificationDto> list() {
        return repository.findAllByOrderBySortOrderAsc().stream().map(mapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public CertificationDto get(String id) {
        return mapper.toDto(find(id));
    }

    public CertificationDto create(CertificationDto dto) {
        guard.check(dto.markets());
        Certification entity = mapper.toEntity(dto);
        if (dto.sortOrder() == null) {
            entity.setSortOrder(repository.maxSortOrder() + 1);
        }
        return mapper.toDto(repository.save(entity));
    }

    public CertificationDto update(String id, CertificationDto dto) {
        Certification entity = find(id);
        guard.check(dto.markets());
        mapper.update(dto, entity);
        return mapper.toDto(entity);
    }

    public void delete(String id) {
        repository.delete(find(id));
    }

    /** Nouvel ordre : ids dans l'ordre souhaité. */
    public void reorder(ReorderRequest request) {
        ReorderSupport.apply(repository.findAll(), request.ids(), Certification::getId, Certification::setSortOrder);
    }

    private Certification find(String id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Certification introuvable : " + id));
    }
}
