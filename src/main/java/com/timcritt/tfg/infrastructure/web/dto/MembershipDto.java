package com.timcritt.tfg.infrastructure.web.dto;

import com.timcritt.tfg.domain.aggregate.classroom.ClassroomRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MembershipDto {
    @NotNull
    private Long userId;
    @NotNull
    private ClassroomRole role;
}
