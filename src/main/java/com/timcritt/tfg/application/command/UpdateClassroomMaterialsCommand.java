package com.timcritt.tfg.application.command;

import com.timcritt.tfg.domain.model.ClassroomRole;

import java.util.List;

/**
 * Application-layer command representing the desired final state of classroom material assignments.
 *
 * Note: This is NOT a web DTO.
 */
public record UpdateClassroomMaterialsCommand(
        Long classroomId,
        List<MaterialAssignment> materials
) {
    public record MaterialAssignment(
            Long materialId,
            String name,
            String description,
            ClassroomRole assignedToRole
    ) {}
}

