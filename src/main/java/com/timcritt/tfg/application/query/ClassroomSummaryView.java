package com.timcritt.tfg.application.query;

import java.time.Instant;
import java.util.List;

public record ClassroomSummaryView(
        Long id,
        String name,
        String description,
        Instant createdAt,
        Instant updatedAt,
        int studentCount,
        int materialCount,
        List<ClassroomMemberView> teachers
) {
}
