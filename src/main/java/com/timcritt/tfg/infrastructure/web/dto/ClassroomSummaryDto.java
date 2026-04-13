package com.timcritt.tfg.infrastructure.web.dto;

import lombok.Data;
import java.time.Instant;
import java.util.List;

@Data
public class ClassroomSummaryDto {
    private Long id;
    private String name;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
    private int studentCount;
    private int materialCount;
    private List<TeacherSummaryDto> teachers;
}

