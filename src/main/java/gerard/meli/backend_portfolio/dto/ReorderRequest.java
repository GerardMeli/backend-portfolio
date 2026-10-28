package gerard.meli.backend_portfolio.dto;

import jakarta.validation.constraints.*;
import gerard.meli.backend_portfolio.dto.jackson.ReorderRequestDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.util.*;

/** Ids dans le nouvel ordre souhaité ; la position devient l'index dans la liste. */
@JsonDeserialize(using = ReorderRequestDeserializer.class)
public record ReorderRequest(
        @NotEmpty List<String> ids
) {
}
