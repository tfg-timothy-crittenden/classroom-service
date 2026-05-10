package com.timcritt.tfg.application;

import com.timcritt.tfg.application.port.outbound.MaterialReferenceCommandPort;
import com.timcritt.tfg.application.service.MaterialTitleUpdateService;
import com.timcritt.tfg.domain.model.MaterialReference;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MaterialTitleUpdateServiceTest {

    @Test
    void forwardsPartialTitleUpdatesToCommandPort() {
        CapturingMaterialReferenceCommandPort port = new CapturingMaterialReferenceCommandPort();
        MaterialTitleUpdateService service = new MaterialTitleUpdateService(port);

        int updated = service.updateTitle(
                26L,
                null,
                "Part 1 - TOEFL Practice Test 2",
                null
        );

        assertEquals(1, updated);
        assertEquals(26L, port.materialId);
        assertNull(port.title);
        assertEquals("Part 1 - TOEFL Practice Test 2", port.part1Title);
        assertNull(port.part2Title);
        assertNotNull(port.returnValue);
    }

    @Test
    void rejectsEmptyTitleUpdates() {
        CapturingMaterialReferenceCommandPort port = new CapturingMaterialReferenceCommandPort();
        MaterialTitleUpdateService service = new MaterialTitleUpdateService(port);

        assertThrows(IllegalArgumentException.class, () -> service.updateTitle(26L, " ", null, null));
    }

    private static final class CapturingMaterialReferenceCommandPort implements MaterialReferenceCommandPort {
        private Long materialId;
        private String title;
        private String part1Title;
        private String part2Title;
        private List<MaterialReference> returnValue;

        @Override
        public List<MaterialReference> upsertForClassroom(Long classroomId, List<MaterialReference> desired) {
            return List.of();
        }

        @Override
        public int deleteByMaterialId(Long materialId) {
            return 0;
        }

        @Override
        public int updateTitlesByMaterialId(Long materialId, String title, String part1Title, String part2Title) {
            this.materialId = materialId;
            this.title = title;
            this.part1Title = part1Title;
            this.part2Title = part2Title;
            this.returnValue = List.of();
            return 1;
        }
    }
}

