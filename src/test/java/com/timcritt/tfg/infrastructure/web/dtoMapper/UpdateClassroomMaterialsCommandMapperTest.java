package com.timcritt.tfg.infrastructure.web.dtoMapper;

import com.timcritt.tfg.application.command.UpdateClassroomMaterialsCommand;
import com.timcritt.tfg.domain.aggregate.classroom.ClassroomRole;
import com.timcritt.tfg.infrastructure.web.dto.UpdateClassroomMaterialsRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UpdateClassroomMaterialsCommandMapperTest {

    @Test
    void mapsNullRequestToEmptyMaterialsList() {
        UpdateClassroomMaterialsCommand command = UpdateClassroomMaterialsCommandMapper.toCommand(7L, null);

        assertEquals(7L, command.classroomId());
        assertNotNull(command.materials());
        assertEquals(0, command.materials().size());
    }

    @Test
    void mapsRequestMaterialsToCommandAssignments() {
        UpdateClassroomMaterialsRequest request = new UpdateClassroomMaterialsRequest();
        UpdateClassroomMaterialsRequest.MaterialAssignmentDto dto = new UpdateClassroomMaterialsRequest.MaterialAssignmentDto();
        dto.setMaterialId(101L);
        dto.setAssignedToRole(ClassroomRole.STUDENT);
        request.setMaterials(List.of(dto));

        UpdateClassroomMaterialsCommand command = UpdateClassroomMaterialsCommandMapper.toCommand(9L, request);

        assertEquals(9L, command.classroomId());
        assertEquals(1, command.materials().size());
        assertEquals(101L, command.materials().getFirst().materialId());
        assertEquals(ClassroomRole.STUDENT, command.materials().getFirst().assignedToRole());
    }
}

