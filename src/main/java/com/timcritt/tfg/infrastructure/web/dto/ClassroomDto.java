package com.timcritt.tfg.infrastructure.web.dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class ClassroomDto {
    private Long id;
    private String name;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
    private List<MemberDto> members;
    private List<MaterialReferenceDto> materials;
    private int studentCount;

}
