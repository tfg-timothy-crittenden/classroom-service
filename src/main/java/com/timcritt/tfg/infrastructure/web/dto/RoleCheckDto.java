package com.timcritt.tfg.infrastructure.web.dto;

import com.timcritt.tfg.domain.model.ClassroomRole;
import lombok.Data;

@Data
public class RoleCheckDto {
    private Long classroomId;
    private Long userId;
    private ClassroomRole role;
}

