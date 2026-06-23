package com.timcritt.tfg.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JoinClassroomRequest {
    @NotBlank
    private String joinCode;
}
