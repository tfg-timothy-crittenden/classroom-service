package com.timcritt.tfg.infrastructure.web.dto;

import com.timcritt.tfg.domain.aggregate.classroom.ClassroomRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MaterialReferenceWithDetailsDto {
    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Long materialId;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private ClassroomRole assignedToRole;

    @Schema(nullable = true, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    private String name;

    @Schema(nullable = true, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    private String description;

    @Schema(nullable = true, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    private String part1Title;

    @Schema(nullable = true, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    private String part2Title;
}

