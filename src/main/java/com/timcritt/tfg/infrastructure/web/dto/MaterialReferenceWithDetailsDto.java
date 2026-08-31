package com.timcritt.tfg.infrastructure.web.dto;

import com.timcritt.tfg.domain.aggregate.classroom.ClassroomRole;
import lombok.Data;

@Data
public class MaterialReferenceWithDetailsDto {
    private Long materialId;
    private ClassroomRole assignedToRole;
    private String name;
    private String description;
    private String part1Title;
    private String part2Title;
}

