package com.timcritt.tfg.infrastructure.web.dto;

import com.timcritt.tfg.domain.model.ClassroomRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoleCheckDto {
    @NotNull
    private Long classroomId;
    @NotNull
    private Long userId;
    @NotNull
    private ClassroomRole role;
}

