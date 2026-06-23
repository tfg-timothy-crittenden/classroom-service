package com.timcritt.tfg.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.Instant;
import java.util.List;

@Data
public class ClassroomSummaryDto {
    @NotNull
    private Long id;
    @NotEmpty
    private String name;
    //For
    @Schema(nullable = true) //Needed to conform to openAPI specs and therefore for Zod to generate the schema
    private String description;
    @NotNull
    private Instant createdAt;
    @NotNull
    private Instant updatedAt;
    @NotNull
    private int studentCount;
    @NotNull
    private int materialCount;
    @NotNull
    private List<TeacherSummaryDto> teachers;
}

