package com.timcritt.tfg.infrastructure.web.dto;

import lombok.Data;

@Data
public class MaterialReferenceDto {
    private Long materialId;
    private String name;
    private String description;
    private String part1Title;
    private String part2Title;
}

