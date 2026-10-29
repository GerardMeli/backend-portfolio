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
public class ExperienceService {

    private final ExperienceRepository repository;
    private final ExperienceTaskRepository childRepository;
    private final ExperienceMapper mapper;
    private final ExperienceTaskMapper childMapper;
    private final MarketGuard guard;

    // ---------- Experience ----------

    @Transactional(readOnly = true)
    public List<ExperienceDto> list() {
        return repository.findAllByOrderBySortOrderAscStartDateDesc().stream().map(mapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public ExperienceDto get(String id) {
        return mapper.toDto(repository.findByIdWithTasks(id).orElseThrow(() -> notFound(id)));
    }

    public ExperienceDto create(ExperienceDto dto) {
        guard.check(dto.markets());
        if (dto.tasks() != null) {
            dto.tasks().forEach(c -> guard.check(c.markets()));
        }
        Experience entity = mapper.toEntity(dto);
        if (dto.sortOrder() == null) {
            entity.setSortOrder(repository.maxSortOrder() + 1);
        }
        if (dto.tasks() != null) {
            for (int i = 0; i < dto.tasks().size(); i++) {
                if (dto.tasks().get(i).sortOrder() == null) {
                    entity.getTasks().get(i).setSortOrder(i);
                }
            }
        }
        return mapper.toDto(repository.save(entity));
    }

    public ExperienceDto update(String id, ExperienceDto dto) {
        Experience entity = find(id);
        guard.check(dto.markets());
        mapper.update(dto, entity);
        return mapper.toDto(entity);
    }

    public void delete(String id) {
        repository.delete(find(id));
    }

    public void reorder(ReorderRequest request) {
        ReorderSupport.apply(repository.findAll(), request.ids(), Experience::getId, Experience::setSortOrder);
    }

    // ---------- Tâche ----------

    @Transactional(readOnly = true)
    public List<ExperienceTaskDto> listTasks(String parentId) {
        requireParent(parentId);
        return childRepository.findByExperienceIdOrderBySortOrderAsc(parentId).stream().map(childMapper::toDto).toList();
    }

    public ExperienceTaskDto addTask(String parentId, ExperienceTaskDto dto) {
        Experience parent = find(parentId);
        guard.check(dto.markets());
        ExperienceTask child = childMapper.toEntity(dto);
        if (dto.sortOrder() == null) {
            child.setSortOrder(childRepository.maxSortOrder(parentId) + 1);
        }
        parent.addTask(child);
        return childMapper.toDto(childRepository.save(child));
    }

    public ExperienceTaskDto updateTask(String parentId, String childId, ExperienceTaskDto dto) {
        ExperienceTask child = findChild(parentId, childId);
        guard.check(dto.markets());
        childMapper.update(dto, child);
        return childMapper.toDto(child);
    }

    public void deleteTask(String parentId, String childId) {
        ExperienceTask child = findChild(parentId, childId);
        child.getExperience().getTasks().remove(child);
        childRepository.delete(child);
    }

    public void reorderTasks(String parentId, ReorderRequest request) {
        requireParent(parentId);
        ReorderSupport.apply(childRepository.findByExperienceIdOrderBySortOrderAsc(parentId), request.ids(), ExperienceTask::getId, ExperienceTask::setSortOrder);
    }

    // ---------- interne ----------

    private Experience find(String id) {
        return repository.findById(id).orElseThrow(() -> notFound(id));
    }

    private void requireParent(String id) {
        if (!repository.existsById(id)) throw notFound(id);
    }

    private ExperienceTask findChild(String parentId, String childId) {
        return childRepository.findById(childId)
                .filter(c -> c.getExperience().getId().equals(parentId))
                .orElseThrow(() -> new NotFoundException("Tâche introuvable : " + childId));
    }

    private NotFoundException notFound(String id) {
        return new NotFoundException("Expérience introuvable : " + id);
    }
}
