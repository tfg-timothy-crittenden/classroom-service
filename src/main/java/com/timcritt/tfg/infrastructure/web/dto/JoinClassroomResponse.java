package com.timcritt.tfg.infrastructure.web.dto;

import com.timcritt.tfg.domain.model.ClassroomRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JoinClassroomResponse {
    @NotNull
    private Long classroomId;

    @NotBlank
    private String classroomName;

    @NotNull
    private ClassroomRole role;

    @NotBlank
    private String message;
}
