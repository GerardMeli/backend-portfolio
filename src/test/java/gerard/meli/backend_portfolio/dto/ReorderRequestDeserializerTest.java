package gerard.meli.backend_portfolio.dto;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReorderRequestDeserializerTest {

    private final JsonMapper mapper = JsonMapper.builder().build();

    @Test
    void acceptsBareArrayOfIds() throws Exception {
        ReorderRequest request = mapper.readValue(
                "[\"81bd70b3-591a-4309-9553-f0a5987ef737\",\"2e8ab650-6b9c-4140-9657-453eff7d3e7c\"]",
                ReorderRequest.class
        );

        assertEquals(List.of(
                "81bd70b3-591a-4309-9553-f0a5987ef737",
                "2e8ab650-6b9c-4140-9657-453eff7d3e7c"
        ), request.ids());
    }

    @Test
    void continuesToAcceptObjectWithIds() throws Exception {
        ReorderRequest request = mapper.readValue(
                "{\"ids\":[\"81bd70b3-591a-4309-9553-f0a5987ef737\"]}",
                ReorderRequest.class
        );

        assertEquals(List.of("81bd70b3-591a-4309-9553-f0a5987ef737"), request.ids());
    }
}
