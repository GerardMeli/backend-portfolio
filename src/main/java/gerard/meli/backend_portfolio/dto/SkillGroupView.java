package gerard.meli.backend_portfolio.dto;

import java.util.*;

public record SkillGroupView(
        String name,
        List<SkillView> skills
) {
}
