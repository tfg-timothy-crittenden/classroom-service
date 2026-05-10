package com.timcritt.tfg.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timcritt.tfg.infrastructure.service.MaterialTitleUpdateServiceAdapter;
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
            String expectedTitle,
            String expectedPart1Title,
            String expectedPart2Title
    ) {
        CapturingMaterialTitleUpdateServiceAdapter adapter = new CapturingMaterialTitleUpdateServiceAdapter();
        MaterialTitleUpdatedEventListener listener = new MaterialTitleUpdatedEventListener(new ObjectMapper().findAndRegisterModules(), adapter);

        listener.onMaterialTitleUpdated(payload);

        assertEquals(expectedMaterialId, adapter.materialId, caseName + " materialId");
        assertEquals(expectedTitle, adapter.title, caseName + " title");
        assertEquals(expectedPart1Title, adapter.part1Title, caseName + " part1Title");
        assertEquals(expectedPart2Title, adapter.part2Title, caseName + " part2Title");
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
                Arguments.of("part1 only", payload(null, "Part 1 - TOEFL Practice Test 2", null), 26L, null, "Part 1 - TOEFL Practice Test 2", null),
                Arguments.of("part2 only", payload(null, null, "Part 2 - TOEFL Practice Test 2"), 26L, null, null, "Part 2 - TOEFL Practice Test 2"),
                Arguments.of("both parts only", payload(null, "Part 1 - TOEFL Practice Test 2", "Part 2 - TOEFL Practice Test 2"), 26L, null, "Part 1 - TOEFL Practice Test 2", "Part 2 - TOEFL Practice Test 2"),
                Arguments.of("title plus part1", payload("TOEFL Practice Test 2", "Part 1 - TOEFL Practice Test 2", null), 26L, "TOEFL Practice Test 2", "Part 1 - TOEFL Practice Test 2", null),
                Arguments.of("title plus part2", payload("TOEFL Practice Test 2", null, "Part 2 - TOEFL Practice Test 2"), 26L, "TOEFL Practice Test 2", null, "Part 2 - TOEFL Practice Test 2"),
                Arguments.of("title plus both parts", payload("TOEFL Practice Test 2", "Part 1 - TOEFL Practice Test 2", "Part 2 - TOEFL Practice Test 2"), 26L, "TOEFL Practice Test 2", "Part 1 - TOEFL Practice Test 2", "Part 2 - TOEFL Practice Test 2")
        );
    }

    static Stream<Arguments> emptyTitleCases() throws Exception {
        return Stream.of(
                Arguments.of("only materialId", payload(null, null, null)),
                Arguments.of("blank title fields", payload("   ", "  ", "\t"))
        );
    }

    private static String payload(String materialTitle, String part1Title, String part2Title) throws Exception {
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("materialId", 26L);
        if (materialTitle != null) {
            event.put("materialTitle", materialTitle);
        }
        if (part1Title != null) {
            event.put("part1Title", part1Title);
        }
        if (part2Title != null) {
            event.put("part2Title", part2Title);
        }
        return new ObjectMapper().writeValueAsString(event);
    }

    private static final class CapturingMaterialTitleUpdateServiceAdapter extends MaterialTitleUpdateServiceAdapter {
        private Long materialId;
        private String title;
        private String part1Title;
        private String part2Title;
        private Integer updateCount;

        private CapturingMaterialTitleUpdateServiceAdapter() {
            super(null);
        }

        @Override
        public int updateTitle(Long materialId, String title, String part1Title, String part2Title) {
            this.materialId = materialId;
            this.title = title;
            this.part1Title = part1Title;
            this.part2Title = part2Title;
            this.updateCount = 1;
            return 1;
        }
    }
}

