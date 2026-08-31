package com.timcritt.tfg.infrastructure.web.dto;

import com.timcritt.tfg.domain.aggregate.classroom.ClassroomRole;

public record ClassroomMemberDto(
        Long membershipId,
        Long userId,
        ClassroomRole role,
        String firstName,
        String lastName
) {
}