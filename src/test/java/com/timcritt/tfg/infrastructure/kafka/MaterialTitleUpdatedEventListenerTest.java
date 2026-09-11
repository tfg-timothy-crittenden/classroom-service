package com.timcritt.tfg.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timcritt.tfg.infrastructure.service.MaterialDetailsUpdateServiceAdapter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MaterialTitleUpdatedEventListenerTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("titleUpdateCases")
    void forwardsAnyCombinationOfTitleFields(
            String caseName,
            String payload,
            Long expectedMaterialId,
            Long expectedVersion,
            String expectedTitle,
            String expectedPart1Title,
            String expectedPart2Title,
            String expectedDescription
    ) {
        CapturingMaterialTitleUpdateServiceAdapter adapter = new CapturingMaterialTitleUpdateServiceAdapter();
        MaterialTitleUpdatedEventListener listener = new MaterialTitleUpdatedEventListener(new ObjectMapper().findAndRegisterModules(), adapter);

        listener.onMaterialTitleUpdated(payload);

        assertEquals(expectedMaterialId, adapter.materialId, caseName + " materialId");
        assertEquals(expectedVersion, adapter.version, caseName + " version");
        assertEquals(expectedTitle, adapter.title, caseName + " title");
        assertEquals(expectedPart1Title, adapter.part1Title, caseName + " part1Title");
        assertEquals(expectedPart2Title, adapter.part2Title, caseName + " part2Title");
        assertEquals(expectedDescription, adapter.description, caseName + " description");
        assertEquals(1, adapter.updateCount, caseName + " updateCount");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("emptyTitleCases")
    void ignoresEventsWithoutAnyTitleFields(String caseName, String payload) {
        CapturingMaterialTitleUpdateServiceAdapter adapter = new CapturingMaterialTitleUpdateServiceAdapter();
        MaterialTitleUpdatedEventListener listener = new MaterialTitleUpdatedEventListener(new ObjectMapper().findAndRegisterModules(), adapter);

        listener.onMaterialTitleUpdated(payload);

        assertNull(adapter.updateCount, caseName + " should not invoke update");
    }

    static Stream<Arguments> titleUpdateCases() throws Exception {
        return Stream.of(
                Arguments.of("part1 only", payload(2L, null, "Part 1 - TOEFL Practice Test 2", null, null), 26L, 2L, null, "Part 1 - TOEFL Practice Test 2", null, null),
                Arguments.of("part2 only", payload(2L, null, null, "Part 2 - TOEFL Practice Test 2", null), 26L, 2L, null, null, "Part 2 - TOEFL Practice Test 2", null),
                Arguments.of("both parts only", payload(2L, null, "Part 1 - TOEFL Practice Test 2", "Part 2 - TOEFL Practice Test 2", null), 26L, 2L, null, "Part 1 - TOEFL Practice Test 2", "Part 2 - TOEFL Practice Test 2", null),
                Arguments.of("description only", payload(2L, null, null, null, "Updated description"), 26L, 2L, null, null, null, "Updated description"),
                Arguments.of("title plus part1", payload(2L, "TOEFL Practice Test 2", "Part 1 - TOEFL Practice Test 2", null, "Updated description"), 26L, 2L, "TOEFL Practice Test 2", "Part 1 - TOEFL Practice Test 2", null, "Updated description"),
                Arguments.of("title plus part2", payload(2L, "TOEFL Practice Test 2", null, "Part 2 - TOEFL Practice Test 2", "Updated description"), 26L, 2L, "TOEFL Practice Test 2", null, "Part 2 - TOEFL Practice Test 2", "Updated description"),
                Arguments.of("title plus both parts", payload(2L, "TOEFL Practice Test 2", "Part 1 - TOEFL Practice Test 2", "Part 2 - TOEFL Practice Test 2", "Updated description"), 26L, 2L, "TOEFL Practice Test 2", "Part 1 - TOEFL Practice Test 2", "Part 2 - TOEFL Practice Test 2", "Updated description")
        );
    }

    static Stream<Arguments> emptyTitleCases() throws Exception {
        return Stream.of(
                Arguments.of("only materialId", payload(2L, null, null, null, null)),
                Arguments.of("blank title fields", payload(2L, "   ", "  ", "\t", "  "))
        );
    }

    private static String payload(Long version, String materialTitle, String part1Title, String part2Title, String description) throws Exception {
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("materialId", 26L);
        if (version != null) {
            event.put("version", version);
        }
        if (materialTitle != null) {
            event.put("materialTitle", materialTitle);
        }
        if (part1Title != null) {
            event.put("part1Title", part1Title);
        }
        if (part2Title != null) {
            event.put("part2Title", part2Title);
        }
        if (description != null) {
            event.put("description", description);
        }
        return new ObjectMapper().writeValueAsString(event);
    }

    private static final class CapturingMaterialTitleUpdateServiceAdapter extends MaterialDetailsUpdateServiceAdapter {
        private Long materialId;
        private Long version;
        private String title;
        private String part1Title;
        private String part2Title;
        private String description;
        private Integer updateCount;

        private CapturingMaterialTitleUpdateServiceAdapter() {
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

