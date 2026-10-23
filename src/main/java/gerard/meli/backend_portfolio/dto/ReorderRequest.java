package gerard.meli.backend_portfolio.dto;

import jakarta.validation.constraints.*;
import java.util.*;

/** Ids dans le nouvel ordre souhaité ; la position devient l'index dans la liste. */
public record ReorderRequest(
        @NotEmpty List<Long> ids
) {
}
