package com.timcritt.tfg.application.service;

import com.timcritt.tfg.application.command.UpdateClassroomMaterialsCommand;
import com.timcritt.tfg.application.port.outbound.MaterialReferenceCommandPort;
import com.timcritt.tfg.domain.model.MaterialReference;

import java.util.List;

/**
 * Application service (no Spring) that updates which materials are assigned to a classroom.
 */
public class MaterialReferenceUpdateService {

    private final MaterialReferenceCommandPort commandPort;

    public MaterialReferenceUpdateService(MaterialReferenceCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    public List<MaterialReference> updateClassroomMaterials(UpdateClassroomMaterialsCommand command) {
        if (command == null || command.classroomId() == null) {
            throw new IllegalArgumentException("classroomId is required");
        }

        List<MaterialReference> desired = command.materials() == null
                ? List.of()
                : command.materials().stream()
                .map(m -> new MaterialReference(
                        null,
                        m.materialId(),
                        m.name(),
                        m.description(),
                        m.part1Title(),
                        m.part2Title(),
                        m.assignedToRole()
                ))
                .toList();

        return commandPort.upsertForClassroom(command.classroomId(), desired);
    }
}
