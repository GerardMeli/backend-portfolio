package gerard.meli.backend_portfolio.adminapi;

import gerard.meli.backend_portfolio.adminapi.AdminDtos.*;
import gerard.meli.backend_portfolio.skill.Skill;
import gerard.meli.backend_portfolio.skill.SkillCategory;
import gerard.meli.backend_portfolio.skill.SkillCategoryRepository;
import gerard.meli.backend_portfolio.skill.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminSkillService {

    private final SkillCategoryRepository categories;
    private final SkillRepository skills;
    private final AdminSupport support;

    // ------------------------------------------------------------------ catégories

    @Transactional(readOnly = true)
    public List<CategoryResponse> listCategories() {
        return categories.findAllByOrderByPositionAsc().stream().map(this::toCategory).toList();
    }

    public CategoryResponse createCategory(CategoryRequest r) {
        SkillCategory c = new SkillCategory();
        c.setPosition(categories.nextPosition());
        applyCategory(c, r);
        return toCategory(categories.save(c));
    }

    public CategoryResponse updateCategory(UUID id, CategoryRequest r) {
        SkillCategory c = category(id);
        applyCategory(c, r);
        return toCategory(c);
    }

    /** Supprime aussi les compétences de la catégorie (et leurs liens avec les projets). */
    public void deleteCategory(UUID id) {
        categories.delete(category(id));
    }

    public List<CategoryResponse> reorderCategories(List<UUID> ids) {
        support.applyOrder(ids, categories.findAll(), SkillCategory::setPosition);
        return listCategories();
    }

    // ------------------------------------------------------------------ compétences

    public SkillResponse addSkill(UUID categoryId, SkillRequest r) {
        SkillCategory c = category(categoryId);
        Skill s = new Skill();
        applySkill(s, r);
        s.setPosition(c.getSkills().stream().mapToInt(Skill::getPosition).max().orElse(-1) + 1);
        c.addSkill(s);
        categories.saveAndFlush(c); // flush : l'id du nouveau Skill est attribué
        return toSkill(s);
    }

    public SkillResponse updateSkill(UUID id, SkillRequest r) {
        Skill s = skills.findById(id).orElseThrow(() -> support.notFound("Skill"));
        applySkill(s, r);
        return toSkill(s);
    }

    public void deleteSkill(UUID id) {
        Skill s = skills.findById(id).orElseThrow(() -> support.notFound("Skill"));
        s.getCategory().getSkills().remove(s); // orphanRemoval supprime la ligne
    }

    public CategoryResponse reorderSkills(UUID categoryId, List<UUID> ids) {
        SkillCategory c = category(categoryId);
        support.applyOrder(ids, c.getSkills(), Skill::setPosition);
        return toCategory(c);
    }

    // ------------------------------------------------------------------ interne

    private SkillCategory category(UUID id) {
        return categories.findWithDetailsById(id).orElseThrow(() -> support.notFound("Skill category"));
    }

    private void applyCategory(SkillCategory c, CategoryRequest r) {
        support.syncTranslations(c.getTranslations(), r.translations(), t -> {
            SkillCategory.Text text = new SkillCategory.Text();
            text.setName(t.name());
            return text;
        });
    }

    private void applySkill(Skill s, SkillRequest r) {
        s.setName(r.name().trim());
        s.setLevel(r.level());
        s.setIcon(r.icon());
    }

    private CategoryResponse toCategory(SkillCategory c) {
        return new CategoryResponse(c.getId(), c.getPosition(),
                AdminSupport.mapValues(c.getTranslations(), t -> new CategoryText(t.getName())),
                c.getSkills().stream()
                        .sorted(Comparator.comparingInt(Skill::getPosition))
                        .map(this::toSkill)
                        .toList());
    }

    private SkillResponse toSkill(Skill s) {
        return new SkillResponse(s.getId(), s.getName(), s.getLevel(), s.getIcon(), s.getPosition());
    }
}