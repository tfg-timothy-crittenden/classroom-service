package com.timcritt.tfg.infrastructure.web.dtoMapper;

import com.timcritt.tfg.application.command.UpdateClassroomMaterialsCommand;
import com.timcritt.tfg.infrastructure.web.dto.UpdateClassroomMaterialsRequest;

import java.util.List;

public final class UpdateClassroomMaterialsCommandMapper {

    private UpdateClassroomMaterialsCommandMapper() {
    }

    public static UpdateClassroomMaterialsCommand toCommand(Long classroomId, UpdateClassroomMaterialsRequest request) {
        List<UpdateClassroomMaterialsCommand.MaterialAssignment> desired = request == null || request.getMaterials() == null
                ? List.of()
                : request.getMaterials().stream()
                .map(m -> new UpdateClassroomMaterialsCommand.MaterialAssignment(
                        m.getMaterialId(),
                        m.getAssignedToRole()
                ))
                .toList();

        return new UpdateClassroomMaterialsCommand(classroomId, desired);
    }
}

