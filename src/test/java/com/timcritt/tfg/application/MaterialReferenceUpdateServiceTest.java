package com.timcritt.tfg.application;

import com.timcritt.tfg.application.command.UpdateClassroomMaterialsCommand;
import com.timcritt.tfg.application.port.outbound.MaterialReferenceCommandPort;
import com.timcritt.tfg.application.service.MaterialReferenceUpdateService;
import com.timcritt.tfg.domain.model.ClassroomRole;
import com.timcritt.tfg.domain.model.MaterialReference;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MaterialReferenceUpdateServiceTest {

    @Test
    void propagatesPartTitlesIntoDomainMaterialReferences() {
        CapturingMaterialReferenceCommandPort port = new CapturingMaterialReferenceCommandPort();
        MaterialReferenceUpdateService service = new MaterialReferenceUpdateService(port);

        service.updateClassroomMaterials(new UpdateClassroomMaterialsCommand(
                7L,
                List.of(new UpdateClassroomMaterialsCommand.MaterialAssignment(
                        26L,
                        "TOEFL Practice Test 2",
                        "Practice test description",
                        "Part 1 title",
                        "Part 2 title",
                        ClassroomRole.STUDENT
                ))
        ));

        assertEquals(7L, port.classroomId);
        assertNotNull(port.desired);
        assertEquals(1, port.desired.size());
        MaterialReference desired = port.desired.getFirst();
        assertEquals(26L, desired.getMaterialId());
        assertEquals("Part 1 title", desired.getPart1Title());
        assertEquals("Part 2 title", desired.getPart2Title());
        assertEquals(ClassroomRole.STUDENT, desired.getAssignedToRole());
    }

    private static final class CapturingMaterialReferenceCommandPort implements MaterialReferenceCommandPort {
        private Long classroomId;
        private List<MaterialReference> desired;

        @Override
        public List<MaterialReference> upsertForClassroom(Long classroomId, List<MaterialReference> desired) {
            this.classroomId = classroomId;
            this.desired = desired;
            return desired;
        }

        @Override
        public int deleteByMaterialId(Long materialId) {
            return 0;
        }

        @Override
        public int updateTitlesByMaterialId(Long materialId, String title, String part1Title, String part2Title) {
            return 0;
        }
    }
}


