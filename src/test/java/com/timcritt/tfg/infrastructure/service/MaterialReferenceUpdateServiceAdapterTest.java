package com.timcritt.tfg.infrastructure.service;

import com.timcritt.tfg.application.port.outbound.MaterialReferenceCommandPort;
import com.timcritt.tfg.domain.model.MaterialReference;
import com.timcritt.tfg.domain.model.ClassroomRole;
import com.timcritt.tfg.infrastructure.web.dto.UpdateClassroomMaterialsRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MaterialReferenceUpdateServiceAdapterTest {

    @Test
    void mapsPartTitlesFromRequestIntoCommandFlow() {
        CapturingMaterialReferenceCommandPort port = new CapturingMaterialReferenceCommandPort();
        MaterialReferenceUpdateServiceAdapter adapter = new MaterialReferenceUpdateServiceAdapter(port);

        UpdateClassroomMaterialsRequest request = new UpdateClassroomMaterialsRequest();
        UpdateClassroomMaterialsRequest.MaterialAssignmentDto assignment = new UpdateClassroomMaterialsRequest.MaterialAssignmentDto();
        assignment.setMaterialId(26L);
        assignment.setName("TOEFL Practice Test 2");
        assignment.setDescription("Practice test description");
        assignment.setPart1Title("Part 1 title");
        assignment.setPart2Title("Part 2 title");
        assignment.setAssignedToRole(ClassroomRole.STUDENT);
        request.setMaterials(List.of(assignment));

        adapter.updateClassroomMaterials(1L, request);

        assertEquals(1L, port.classroomId);
        assertNotNull(port.desired);
        assertEquals(1, port.desired.size());
        MaterialReference desired = port.desired.getFirst();
        assertEquals(26L, desired.getMaterialId());
        assertEquals("Part 1 title", desired.getPart1Title());
        assertEquals("Part 2 title", desired.getPart2Title());
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

