package com.timcritt.tfg.infrastructure.web.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TeacherDto {
    @NotNull
    private Long userId;
    @NotEmpty
    private String name;
    @NotEmpty
    private String surname;
}

