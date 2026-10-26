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
public class SkillService {

    private final SkillCategoryRepository repository;
    private final SkillRepository childRepository;
    private final SkillCategoryMapper mapper;
    private final SkillMapper childMapper;
    private final MarketGuard guard;

    public SkillService(SkillCategoryRepository repository, SkillRepository childRepository,
            SkillCategoryMapper mapper, SkillMapper childMapper, MarketGuard guard) {
        this.repository = repository;
        this.childRepository = childRepository;
        this.mapper = mapper;
        this.childMapper = childMapper;
        this.guard = guard;
    }

    // ---------- SkillCategory ----------

    @Transactional(readOnly = true)
    public List<SkillCategoryDto> list() {
        return repository.findAllByOrderBySortOrderAsc().stream().map(mapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public SkillCategoryDto get(String id) {
        return mapper.toDto(repository.findByIdWithSkills(id).orElseThrow(() -> notFound(id)));
    }

    public SkillCategoryDto create(SkillCategoryDto dto) {
        if (dto.skills() != null) {
            dto.skills().forEach(c -> guard.check(c.markets()));
        }
        SkillCategory entity = mapper.toEntity(dto);
        if (dto.sortOrder() == null) {
            entity.setSortOrder(repository.maxSortOrder() + 1);
        }
        if (dto.skills() != null) {
            for (int i = 0; i < dto.skills().size(); i++) {
                if (dto.skills().get(i).sortOrder() == null) {
                    entity.getSkills().get(i).setSortOrder(i);
                }
            }
        }
        return mapper.toDto(repository.save(entity));
    }

    public SkillCategoryDto update(String id, SkillCategoryDto dto) {
        SkillCategory entity = find(id);
        mapper.update(dto, entity);
        return mapper.toDto(entity);
    }

    public void delete(String id) {
        repository.delete(find(id));
    }

    public void reorder(ReorderRequest request) {
        ReorderSupport.apply(repository.findAll(), request.ids(), SkillCategory::getId, SkillCategory::setSortOrder);
    }

    // ---------- Compétence ----------

    @Transactional(readOnly = true)
    public List<SkillDto> listSkills(String parentId) {
        requireParent(parentId);
        return childRepository.findByCategoryIdOrderBySortOrderAsc(parentId).stream().map(childMapper::toDto).toList();
    }

    public SkillDto addSkill(String parentId, SkillDto dto) {
        SkillCategory parent = find(parentId);
        guard.check(dto.markets());
        Skill child = childMapper.toEntity(dto);
        if (dto.sortOrder() == null) {
            child.setSortOrder(childRepository.maxSortOrder(parentId) + 1);
        }
        parent.addSkill(child);
        return childMapper.toDto(childRepository.save(child));
    }

    public SkillDto updateSkill(String parentId, String childId, SkillDto dto) {
        Skill child = findChild(parentId, childId);
        guard.check(dto.markets());
        childMapper.update(dto, child);
        return childMapper.toDto(child);
    }

    public void deleteSkill(String parentId, String childId) {
        Skill child = findChild(parentId, childId);
        child.getCategory().getSkills().remove(child);
        childRepository.delete(child);
    }

    public void reorderSkills(String parentId, ReorderRequest request) {
        requireParent(parentId);
        ReorderSupport.apply(childRepository.findByCategoryIdOrderBySortOrderAsc(parentId), request.ids(), Skill::getId, Skill::setSortOrder);
    }

    // ---------- interne ----------

    private SkillCategory find(String id) {
        return repository.findById(id).orElseThrow(() -> notFound(id));
    }

    private void requireParent(String id) {
        if (!repository.existsById(id)) throw notFound(id);
    }

    private Skill findChild(String parentId, String childId) {
        return childRepository.findById(childId)
                .filter(c -> c.getCategory().getId().equals(parentId))
                .orElseThrow(() -> new NotFoundException("Compétence introuvable : " + childId));
    }

    private NotFoundException notFound(String id) {
        return new NotFoundException("Catégorie de compétences introuvable : " + id);
    }
}
