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
public class ProjectService {

    private final ProjectRepository repository;
    private final ProjectHighlightRepository childRepository;
    private final ProjectMapper mapper;
    private final ProjectHighlightMapper childMapper;
    private final MarketGuard guard;

    // ---------- Project ----------

    @Transactional(readOnly = true)
    public List<ProjectDto> list() {
        return repository.findAllByOrderBySortOrderAscStartDateDesc().stream().map(mapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public ProjectDto get(String id) {
        return mapper.toDto(repository.findByIdWithHighlights(id).orElseThrow(() -> notFound(id)));
    }

    public ProjectDto create(ProjectDto dto) {
        guard.check(dto.markets());
        Project entity = mapper.toEntity(dto);
        if (dto.sortOrder() == null) {
            entity.setSortOrder(repository.maxSortOrder() + 1);
        }
        if (dto.highlights() != null) {
            for (int i = 0; i < dto.highlights().size(); i++) {
                if (dto.highlights().get(i).sortOrder() == null) {
                    entity.getHighlights().get(i).setSortOrder(i);
                }
            }
        }
        return mapper.toDto(repository.save(entity));
    }

    public ProjectDto update(String id, ProjectDto dto) {
        Project entity = find(id);
        guard.check(dto.markets());
        mapper.update(dto, entity);
        return mapper.toDto(entity);
    }

    public void delete(String id) {
        repository.delete(find(id));
    }

    public void reorder(ReorderRequest request) {
        ReorderSupport.apply(repository.findAll(), request.ids(), Project::getId, Project::setSortOrder);
    }

    // ---------- Point clé ----------

    @Transactional(readOnly = true)
    public List<ProjectHighlightDto> listHighlights(String parentId) {
        requireParent(parentId);
        return childRepository.findByProjectIdOrderBySortOrderAsc(parentId).stream().map(childMapper::toDto).toList();
    }

    public ProjectHighlightDto addHighlight(String parentId, ProjectHighlightDto dto) {
        Project parent = find(parentId);
        ProjectHighlight child = childMapper.toEntity(dto);
        if (dto.sortOrder() == null) {
            child.setSortOrder(childRepository.maxSortOrder(parentId) + 1);
        }
        parent.addHighlight(child);
        return childMapper.toDto(childRepository.save(child));
    }

    public ProjectHighlightDto updateHighlight(String parentId, String childId, ProjectHighlightDto dto) {
        ProjectHighlight child = findChild(parentId, childId);
        childMapper.update(dto, child);
        return childMapper.toDto(child);
    }

    public void deleteHighlight(String parentId, String childId) {
        ProjectHighlight child = findChild(parentId, childId);
        child.getProject().getHighlights().remove(child);
        childRepository.delete(child);
    }

    public void reorderHighlights(String parentId, ReorderRequest request) {
        requireParent(parentId);
        ReorderSupport.apply(childRepository.findByProjectIdOrderBySortOrderAsc(parentId), request.ids(), ProjectHighlight::getId, ProjectHighlight::setSortOrder);
    }

    // ---------- interne ----------

    private Project find(String id) {
        return repository.findById(id).orElseThrow(() -> notFound(id));
    }

    private void requireParent(String id) {
        if (!repository.existsById(id)) throw notFound(id);
    }

    private ProjectHighlight findChild(String parentId, String childId) {
        return childRepository.findById(childId)
                .filter(c -> c.getProject().getId().equals(parentId))
                .orElseThrow(() -> new NotFoundException("Point clé introuvable : " + childId));
    }

    private NotFoundException notFound(String id) {
        return new NotFoundException("Projet introuvable : " + id);
    }
}
