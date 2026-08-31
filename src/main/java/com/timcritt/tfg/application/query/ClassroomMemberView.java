package com.timcritt.tfg.application.query;

import com.timcritt.tfg.domain.aggregate.classroom.ClassroomRole;

public record ClassroomMemberView(
        Long membershipId,
        Long userId,
        ClassroomRole role,
        String firstName,
        String lastName
) {
}