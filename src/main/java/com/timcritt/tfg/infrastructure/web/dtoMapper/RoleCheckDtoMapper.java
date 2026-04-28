package com.timcritt.tfg.infrastructure.web.dtoMapper;

import com.timcritt.tfg.domain.model.ClassroomRole;
import com.timcritt.tfg.infrastructure.web.dto.RoleCheckDto;

public final class RoleCheckDtoMapper {
    private RoleCheckDtoMapper() {
    }

    public static RoleCheckDto toDto(Long classroomId, Long userId, ClassroomRole role) {
        RoleCheckDto dto = new RoleCheckDto();
        dto.setClassroomId(classroomId);
        dto.setUserId(userId);
        dto.setRole(role);
        return dto;
    }
}
