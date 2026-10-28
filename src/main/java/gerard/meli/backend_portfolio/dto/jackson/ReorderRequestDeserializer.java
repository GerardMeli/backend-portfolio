package gerard.meli.backend_portfolio.dto.jackson;

import gerard.meli.backend_portfolio.dto.ReorderRequest;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

import java.util.ArrayList;
import java.util.List;

public final class ReorderRequestDeserializer extends ValueDeserializer<ReorderRequest> {

    @Override
    public ReorderRequest deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
        JsonNode body = parser.readValueAsTree();
        JsonNode ids = body.isArray() ? body : body.isObject() ? body.get("ids") : null;

        if (ids == null || !ids.isArray()) {
            throw DatabindException.from(parser, "Expected an array of ids or an object containing an ids array");
        }

        List<String> values = new ArrayList<>(ids.size());
        for (JsonNode id : ids) {
            if (!id.isTextual()) {
                throw DatabindException.from(parser, "Each id must be a string");
            }
            values.add(id.textValue());
        }
        return new ReorderRequest(values);
    }
}
