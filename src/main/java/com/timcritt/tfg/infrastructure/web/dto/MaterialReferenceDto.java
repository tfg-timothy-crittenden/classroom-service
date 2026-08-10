package com.timcritt.tfg.infrastructure.web.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MaterialReferenceDto {
    @NotNull
    private Long materialId;
    @NotEmpty
    private String name;
    //Optional
    private String description;
    @NotEmpty
    private String part1Title;
    @NotEmpty
    private String part2Title;
}

