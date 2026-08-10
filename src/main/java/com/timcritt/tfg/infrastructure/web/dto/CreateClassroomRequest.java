package com.timcritt.tfg.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CreateClassroomRequest {
    @NotEmpty
    private String name;
    @Schema(nullable = true, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    private String description;
}
