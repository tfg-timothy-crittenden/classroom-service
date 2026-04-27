package com.timcritt.tfg.application.port.outbound;

import com.timcritt.tfg.domain.model.ClassroomRole;

public record MaterialReferenceAssignmentView(
        Long classroomId,
        Long materialId,
        ClassroomRole assignedToRole
) {
}

