package gerard.meli.backend_portfolio.dto;

import gerard.meli.backend_portfolio.enums.SkillKind;
import java.util.*;

public record SkillGroupView(
        String name,
        SkillKind kind,
        List<SkillView> skills
) {
}
