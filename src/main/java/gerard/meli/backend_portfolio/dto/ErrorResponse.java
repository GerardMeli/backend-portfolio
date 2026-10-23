package gerard.meli.backend_portfolio.dto;

import java.time.*;
import java.util.*;

public record ErrorResponse(
        int status,
        String message,
        List<String> details,
        Instant timestamp
) {
}
