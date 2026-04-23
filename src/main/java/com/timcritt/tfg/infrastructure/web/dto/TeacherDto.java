package com.timcritt.tfg.infrastructure.web.dto;

import lombok.Data;

@Data
public class TeacherDto {
    private Long userId;
    private String name;
    private String surname;
}

