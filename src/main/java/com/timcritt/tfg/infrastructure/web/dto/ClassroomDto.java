package com.timcritt.tfg.infrastructure.web.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class ClassroomDto {
    @NotNull
    private Long id;
    @NotEmpty
    private String name;
    //Optional
    private String description;
    @NotNull
    private Instant createdAt;
    @NotNull
    private Instant updatedAt;
    @NotNull
    private List<MemberDto> members;
    @NotNull
    private List<MaterialReferenceDto> materials;
    @NotNull
    private int studentCount;

}
