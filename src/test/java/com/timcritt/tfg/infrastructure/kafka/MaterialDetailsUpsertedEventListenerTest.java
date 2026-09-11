package com.timcritt.tfg.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timcritt.tfg.infrastructure.service.MaterialDetailsUpdateServiceAdapter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MaterialDetailsUpsertedEventListenerTest {

    @Test
    void delegatesUpdateWhenPayloadIsValid() {
        CapturingMaterialDetailsUpdateServiceAdapter adapter = new CapturingMaterialDetailsUpdateServiceAdapter();
        MaterialDetailsUpsertedEventListener listener = new MaterialDetailsUpsertedEventListener(new ObjectMapper().findAndRegisterModules(), adapter);

        listener.onMaterialDetailsUpserted("{\"materialId\":26,\"version\":4,\"materialTitle\":\"TOEFL Practice Test 4\",\"part1Title\":\"Part 1\",\"part2Title\":\"Part 2\",\"description\":\"Updated description\"}");

        assertEquals(26L, adapter.materialId);
        assertEquals(4L, adapter.version);
        assertEquals("TOEFL Practice Test 4", adapter.title);
        assertEquals("Part 1", adapter.part1Title);
        assertEquals("Part 2", adapter.part2Title);
        assertEquals("Updated description", adapter.description);
        assertEquals(1, adapter.updateCount);
    }

    @Test
    void supportsTitleAliasName() {
        CapturingMaterialDetailsUpdateServiceAdapter adapter = new CapturingMaterialDetailsUpdateServiceAdapter();
        MaterialDetailsUpsertedEventListener listener = new MaterialDetailsUpsertedEventListener(new ObjectMapper().findAndRegisterModules(), adapter);

        listener.onMaterialDetailsUpserted("{\"materialId\":26,\"version\":5,\"name\":\"TOEFL Practice Test 5\"}");

        assertEquals(26L, adapter.materialId);
        assertEquals(5L, adapter.version);
        assertEquals("TOEFL Practice Test 5", adapter.title);
        assertEquals(1, adapter.updateCount);
    }

    @Test
    void ignoresPayloadWithoutMaterialId() {
        CapturingMaterialDetailsUpdateServiceAdapter adapter = new CapturingMaterialDetailsUpdateServiceAdapter();
        MaterialDetailsUpsertedEventListener listener = new MaterialDetailsUpsertedEventListener(new ObjectMapper().findAndRegisterModules(), adapter);

        listener.onMaterialDetailsUpserted("{\"version\":4,\"materialTitle\":\"Title\"}");

        assertNull(adapter.updateCount);
    }

    @Test
    void ignoresPayloadWithMissingOrNegativeVersion() {
        CapturingMaterialDetailsUpdateServiceAdapter adapter = new CapturingMaterialDetailsUpdateServiceAdapter();
        MaterialDetailsUpsertedEventListener listener = new MaterialDetailsUpsertedEventListener(new ObjectMapper().findAndRegisterModules(), adapter);

        listener.onMaterialDetailsUpserted("{\"materialId\":26,\"materialTitle\":\"Title\"}");
        listener.onMaterialDetailsUpserted("{\"materialId\":26,\"version\":-1,\"materialTitle\":\"Title\"}");

        assertNull(adapter.updateCount);
    }

    @Test
    void ignoresPayloadWithoutAnyDetailsFields() {
        CapturingMaterialDetailsUpdateServiceAdapter adapter = new CapturingMaterialDetailsUpdateServiceAdapter();
        MaterialDetailsUpsertedEventListener listener = new MaterialDetailsUpsertedEventListener(new ObjectMapper().findAndRegisterModules(), adapter);

        listener.onMaterialDetailsUpserted("{\"materialId\":26,\"version\":4,\"materialTitle\":\" \",\"part1Title\":\"\t\",\"part2Title\":\"\",\"description\":\"  \"}");

        assertNull(adapter.updateCount);
    }

    @Test
    void ignoresMalformedPayload() {
        CapturingMaterialDetailsUpdateServiceAdapter adapter = new CapturingMaterialDetailsUpdateServiceAdapter();
        MaterialDetailsUpsertedEventListener listener = new MaterialDetailsUpsertedEventListener(new ObjectMapper().findAndRegisterModules(), adapter);

        listener.onMaterialDetailsUpserted("not-json");

        assertNull(adapter.updateCount);
    }

    @Test
    void delegatesUpdateWhenDebeziumEnvelopeContainsEvent() {
        CapturingMaterialDetailsUpdateServiceAdapter adapter = new CapturingMaterialDetailsUpdateServiceAdapter();
        MaterialDetailsUpsertedEventListener listener = new MaterialDetailsUpsertedEventListener(new ObjectMapper().findAndRegisterModules(), adapter);

        listener.onMaterialDetailsUpserted("{" +
                "\"schema\":{\"type\":\"struct\"}," +
                "\"payload\":{" +
                "\"event\":{" +
                "\"materialId\":10054," +
                "\"version\":0," +
                "\"materialTitle\":\"TOEFL Speaking Test 54\"," +
                "\"part1Title\":\"Part 1\"," +
                "\"part2Title\":\"Part 2\"," +
                "\"description\":\"Seeded\"," +
                "\"updatedAt\":\"2026-09-04T22:32:02.172647Z\"" +
                "}," +
                "\"requestId\":\"e13a3b38-5bd6-431d-a8dc-48e45debd009\"" +
                "}" +
                "}");

        assertEquals(10054L, adapter.materialId);
        assertEquals(0L, adapter.version);
        assertEquals("TOEFL Speaking Test 54", adapter.title);
        assertEquals("Part 1", adapter.part1Title);
        assertEquals("Part 2", adapter.part2Title);
        assertEquals("Seeded", adapter.description);
        assertEquals(1, adapter.updateCount);
    }

    @Test
    void delegatesUpdateWhenRootEnvelopeContainsEvent() {
        CapturingMaterialDetailsUpdateServiceAdapter adapter = new CapturingMaterialDetailsUpdateServiceAdapter();
        MaterialDetailsUpsertedEventListener listener = new MaterialDetailsUpsertedEventListener(new ObjectMapper().findAndRegisterModules(), adapter);

        listener.onMaterialDetailsUpserted("{" +
                "\"event\":{" +
                "\"materialId\":10001," +
                "\"version\":0," +
                "\"materialTitle\":\"TOEFL Speaking Test 1\"," +
                "\"part1Title\":\"Part 1\"," +
                "\"part2Title\":\"Part 2\"," +
                "\"description\":\"Seeded\"," +
                "\"updatedAt\":\"2026-09-04T22:32:02.172647Z\"" +
                "}," +
                "\"requestId\":\"ddbe8f09-cc40-456a-9359-48bf64e81639\"" +
                "}");

        assertEquals(10001L, adapter.materialId);
        assertEquals(0L, adapter.version);
        assertEquals("TOEFL Speaking Test 1", adapter.title);
        assertEquals("Part 1", adapter.part1Title);
        assertEquals("Part 2", adapter.part2Title);
        assertEquals("Seeded", adapter.description);
        assertEquals(1, adapter.updateCount);
    }

    private static final class CapturingMaterialDetailsUpdateServiceAdapter extends MaterialDetailsUpdateServiceAdapter {
        private Long materialId;
        private Long version;
        private String title;
        private String part1Title;
        private String part2Title;
        private String description;
        private Integer updateCount;

        private CapturingMaterialDetailsUpdateServiceAdapter() {
            super(null);
        }

        @Override
        public void updateDetails(Long materialId, Long version, String title, String part1Title, String part2Title, String description) {
            this.materialId = materialId;
            this.version = version;
            this.title = title;
            this.part1Title = part1Title;
            this.part2Title = part2Title;
            this.description = description;
            this.updateCount = 1;
        }
    }
}
